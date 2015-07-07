package cs601.webmail.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FolderManager {

    private static FolderManager instance;
    private Connection c;
    public static Boolean debug = false;

    public static synchronized FolderManager instance() {
        if( instance == null ) {
            instance = new FolderManager();
        }
        return instance;
    }


    public void addFolders(String folderName, User u) throws SQLException, ClassNotFoundException {
        c = new DBManager().getConnection();
        PreparedStatement insertQuery;
        int folderID = new DBManager().getCurrentNum("Folders", "folderID") + 1;
        int rsUpdate = 0;
        int userID = u.getUserID();

        insertQuery = c.prepareStatement("INSERT INTO Folders VALUES (?, ?, ?);");
        insertQuery.setInt(1, folderID);
        insertQuery.setInt(2, userID);
        insertQuery.setString(3, folderName);

        c.setAutoCommit(false);  // Need to do shut down the auto commit,so can do transaction manually

        // Use transaction to ensure the data integrity
        try {
            rsUpdate = insertQuery.executeUpdate();
            c.commit();                  // commit the changes here
        }catch (SQLException e) {
            try {
                c.rollback();            //Error: roll back the changes like nothing happened
            }catch (SQLException e1) {
                // May need to add into log.
                e.printStackTrace();
            }
        }finally {
            c.setAutoCommit(true);
            insertQuery.close();
        }

        if ( rsUpdate != 1 ) {
            ErrorManager.instance().error("Failed to add new folder.");
        }
    }

    public ArrayList<String> getAllFolders(User u) throws SQLException, ClassNotFoundException {
        ArrayList<String> folderList = new ArrayList<>();
        c = new DBManager().getConnection();
        PreparedStatement query = c.prepareStatement("SELECT folderName FROM Folders WHERE userID = ?");
        query.setInt(1, u.getUserID());
        ResultSet rs = query.executeQuery();

        while(rs.next()) {
            folderList.add(rs.getString("folderName"));
        }
        return folderList;
    }

    public int getFolderID(String folder, int userID) throws SQLException, ClassNotFoundException {
        int folderID = 0;
        c = new DBManager().getConnection();
        PreparedStatement query = c.prepareStatement("SELECT folderID FROM Folders WHERE userID = ? " +
                "AND folderName = ?;");
        query.setInt(1, userID);
        query.setString(2, folder);
        ResultSet rs = query.executeQuery();
        if(rs.next()) {
            folderID = rs.getInt("folderID");
        }
        return folderID;
    }

    public String getFolderName(int folderID) throws SQLException, ClassNotFoundException {
        String folderName = "";
        c = new DBManager().getConnection();
        PreparedStatement query = c.prepareStatement("SELECT folderName FROM Folders WHERE folderID = ?;");
        query.setInt(1, folderID);
        ResultSet rs = query.executeQuery();
        if(rs.next()) {
            folderName = rs.getString("folderName");
        }
        return folderName;
    }

    public void saveToFolder(int folderID, int mailID)
            throws SQLException, ClassNotFoundException {
        //find folderID in folders tabler

        c = new DBManager().getConnection();
        //update the folderID in Mails table use folderID
        PreparedStatement update = c.prepareStatement("UPDATE Mails SET folderID = ? WHERE " +
                "mailID =?;");
        update.setInt(1, folderID);
        update.setInt(2, mailID);
        int rsUpdate = 0;
        try {
            c.setAutoCommit(false);
            rsUpdate = update.executeUpdate();
        } catch (SQLException e) {
            try {
                c.rollback();            //Error: roll back the changes like nothing happened
            } catch (SQLException e1) {
                if (rsUpdate!=1) {
                    ErrorManager.instance().error("Failed to save to folder.");
                }
            }
        } finally {
            c.setAutoCommit(true);
            update.close();
        }
    }
}
