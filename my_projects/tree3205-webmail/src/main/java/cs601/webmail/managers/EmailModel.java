package cs601.webmail.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmailModel {

    public static Boolean debug = true;
    public static EmailManager objects;
    private Connection c;

    public EmailModel() {
        this.objects = EmailManager.instance();
    }


    // Change the type of the mail in database
    public void deleteMail(int mailID) throws SQLException, ClassNotFoundException {
        c = new DBManager().getConnection();
        PreparedStatement update = c.prepareStatement("UPDATE Mails SET mailType = \"trash\" WHERE mailID ="
                + mailID + ";");
        int rsUpdate = 0;
        try {
            c.setAutoCommit(false);
            // Use another table to store attachments
            rsUpdate = update.executeUpdate();
        } catch (SQLException e) {
            try {
                c.rollback();            //Error: roll back the changes like nothing happened
            } catch (SQLException e1) {
                ErrorManager.instance().error(e);
            }
        } finally {
            c.setAutoCommit(true);
            update.close();
        }

        if (rsUpdate != 1) {
            if (debug) System.err.println("save trash mail fail");
        } else {
            if (debug) System.out.println("save trash mail success");
        }
    }

    // Delete one mail from trash
    public Email clearOneMail(int mailID) throws SQLException, ClassNotFoundException {
        Email mail = null;
        c = new DBManager().getConnection();
        PreparedStatement empty = c.prepareStatement("UPDATE Mails SET mailType = \"empty\" WHERE mailID ="
                + mailID + ";");
        int rsUpdate = 0;
        try {
            c.setAutoCommit(false);

            // Use another table to store attachments
            rsUpdate = empty.executeUpdate();

        } catch (SQLException e) {
            try {
                c.rollback();            //Error: roll back the changes like nothing happened
            } catch (SQLException e1) {
                ErrorManager.instance().error(e);
            }
        } finally {
            c.setAutoCommit(true);
            empty.close();
        }

        if (rsUpdate != 1) {
            if (debug) System.err.println("save mail to removed state fail");
        } else {
            if (debug) System.out.println("save mail to removed state success");
        }
        return mail;
    }
}