package cs601.webmail.managers;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import java.io.*;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EmailManager {
	private static EmailManager instance;
    private Connection c;
    public static Boolean debug = false;
    private final String FILES_DIR = "./uploadFile";


    public static synchronized EmailManager instance() {
		if( instance == null ) {
			instance = new EmailManager();
		}
		return instance;
	}

    /** Save send mail into Data base table: InboxMails or OutboxMails */
    public ArrayList<Email> saveMails(ArrayList<Email> mailList)throws SQLException, ClassNotFoundException, UnsupportedEncodingException {
        File file = null;
        ArrayList<Email> savedMailList = new ArrayList<>();
        c = new DBManager().getConnection();
        int mailID = 0;
        PreparedStatement insertQuery = null;
        int rsInsert = 0;
        if (mailList.size()!=0) {
            try {
                for (Email m : mailList) {
                    mailID = new DBManager().getCurrentNum("Mails", "mailID") + 1;
                    // save data to Mails table in db
                    insertQuery = c.prepareStatement("INSERT INTO Mails VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
                    insertQuery.setInt(1, mailID);
                    insertQuery.setInt(2, m.getUserID());
                    insertQuery.setString(3, m.getSender());
                    insertQuery.setString(4, m.getReceiver());
                    insertQuery.setString(5, m.getCC());
                    insertQuery.setString(6, m.getBcc());
                    insertQuery.setString(7, m.getSubject());
                    insertQuery.setString(8, m.getContent());
                    insertQuery.setString(9, m.getDate());
                    insertQuery.setString(10, m.getType());
                    insertQuery.setString(11, m.getUIDL());
                    insertQuery.setString(12, m.getIfRead());
                    insertQuery.setInt(13, m.getFolderID());

                    c.setAutoCommit(false);
                    // Use another table to store attachments
                    rsInsert = insertQuery.executeUpdate();
                    if (rsInsert != 1) {
                        if (debug) System.err.println("save fail");
                    }
                    else {
                        if (debug) System.out.println("save mail success");
                        m.setMailID(mailID);
                        savedMailList.add(m);
                    }
                }
            }
            catch (SQLException e) {
                try {
                    c.rollback();            //Error: roll back the changes like nothing happened
                } catch (SQLException e1) {
                    ErrorManager.instance().error(e);
                }
            }
            finally {
                c.setAutoCommit(true);
                insertQuery.close();
            }

            // save file at server
            PrintWriter writer = null;
            for(Email savedMail: savedMailList) {
                for(Attachment attach: savedMail.getAttachment()) {
                    String fileName = FILES_DIR + File.separator + savedMail.getMailID()+ attach.getFileName();
                    file = new File(fileName);
//                    String fileContent = StringUtils.newStringUtf8(Base64.decodeBase64(attach.getFileContent()));
                    byte[] content = Base64.decodeBase64(attach.getFileContent());
                    String filePath = file.getAbsolutePath();
                    if (!file.exists()) {
                        try {
                            OutputStream attachStream = new FileOutputStream(filePath);
                            attachStream.write(content);
                            attachStream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    attach.setFileContent(filePath);
                }
            }

            // save data to Attachment table in db
            for(Email m: savedMailList) {
                if (m.getAttachment().size() != 0) {
                    for (Attachment attach : m.getAttachment()) {
                        int attachID = new DBManager().getCurrentNum("Attachments", "attachID") + 1;
                        String fileName = attach.getFileName();
                        String filePath = attach.getFileContent();
                        insertQuery = c.prepareStatement("INSERT INTO Attachments VALUES (?, ?, ?, ?);");
                        insertQuery.setInt(1, attachID);
                        insertQuery.setInt(2, m.getMailID());
                        insertQuery.setString(3, fileName);
                        insertQuery.setString(4, filePath);
                        c.setAutoCommit(false);
                        rsInsert = insertQuery.executeUpdate();
                        if (rsInsert != 1) {
                            if (debug) System.err.println("save attach fail");
                        } else {
                            if (debug) System.out.println("save attach success");
                            attach.setAttachID(attachID);
                            attach.setMailID(mailID);
                        }
                    }
                }
            }
        }
        return savedMailList;
    }

    // Every time connect to pop server, check if mail already in database by uidl
    // Return a map[numOfEmail, uidl]
    public HashMap<String, String> checkMail(HashMap<String, String> uidls) throws SQLException, ClassNotFoundException {
        HashMap<String, String> needPopMails = new HashMap<>();
        c = new DBManager().getConnection();
        ResultSet rs;
        PreparedStatement checkQuery = c.prepareStatement("SELECT * FROM Mails WHERE uidl = ?;");
        for (Map.Entry<String, String> entry : uidls.entrySet()) {
            String emailID = entry.getKey();
            String uidl = entry.getValue();
            checkQuery.setString(1, uidl);
            rs = checkQuery.executeQuery();
            if (!rs.next()){
                needPopMails.put(emailID, uidl);
            }
        }
        return needPopMails;
    }

    /**
     * ===================== For display usage ==========================
     */
    // Return a list of Emails that will be displayed in the page
    // merge all mails under the same user ID
    // displayed according to given order or by default: unread and date
    public ArrayList<Email> retrieveDisplayMails(int displayNum, int page, int userID, String mailType,
                             int folderID, String field, String order) throws SQLException, ClassNotFoundException {
        String  q;
        ArrayList<Email> pageMails = new ArrayList<>();
        Attachment attach = null;
        PreparedStatement findQuery;
        c = new DBManager().getConnection();
        int begin = (page - 1) * displayNum;
        PreparedStatement getMailsQuery;
        //  select * from Mails limit 15(getNumber) offset 20(begin)
        if (field != null && order != null) {
            q = String.format("SELECT * FROM  Mails WHERE userID = %d AND mailType = \"%s\" " +
                            "AND folderID = " + folderID + " ORDER BY %s %s LIMIT %d OFFSET %d;",
                    userID, mailType, field, order, displayNum, begin);
        }
        else {
            q = String.format("SELECT * FROM  Mails WHERE userID = %d AND mailType = \"%s\" " +
                            "AND folderID = " + folderID + " ORDER BY CAST(read  AS INTEGER) ASC, emailDate Desc LIMIT %d OFFSET %d;",
                    userID, mailType, displayNum, begin);
        }

        getMailsQuery = c.prepareStatement(q);
        ResultSet rs = getMailsQuery.executeQuery();
        while (rs.next()) {
            int mailID = rs.getInt("mailID");
            String sender = rs.getString("sender");
            String recipient = rs.getString("recipient");
            String cc = rs.getString("cc");
            String bcc = rs.getString("bcc");
            String subject = rs.getString("subject");
            String content = rs.getString("content");
            String date = rs.getString("emailDate");
            String uidl = rs.getString("uidl");
            String ifRead = rs.getString("read");

            // Special deal with status: turn all read mails'ifRead status
            // to null so as stringtemplate can process
            if ("1".equals(ifRead)) {
                ifRead = null;
            }
            Email mail = new Email(mailID, userID, sender, recipient, cc, bcc, subject, content, date,
                    mailType, uidl, ifRead, folderID, null);
            pageMails.add(mail);
        }


        //find attachment
        ArrayList<Attachment> attachList = new ArrayList<>();
        for(Email mail: pageMails) {
            findQuery = c.prepareStatement("SELECT * FROM Attachments WHERE mailID = ?;");
            findQuery.setInt(1, mail.getMailID());
            ResultSet rsFind = findQuery.executeQuery();
            while (rsFind.next()) {
                int attachID = rsFind.getInt("attachID");
                String fileName = rsFind.getString("fileName");
                String filePath = rsFind.getString("filePath");
                attach = new Attachment(attachID, mail.getMailID(), fileName, filePath);
                attachList.add(attach);
            }
            mail.setAttachment(attachList);
        }

        return  pageMails;
    }

    // Get display mails in specific folder
    public ArrayList<Email> getDisplayMailsInFolder(int displayNum, int page, int userID, int folderID,
                                                    String field, String order) throws SQLException, ClassNotFoundException {
        String q;
        Attachment attach = null;
        PreparedStatement findQuery;
        ArrayList<Email> pageMails = new ArrayList<>();
        c = new DBManager().getConnection();
        int begin = (page - 1) * displayNum;
        PreparedStatement getMailsQuery;
        //  select * from Mails limit 15(getNumber) offset 20(begin)

        if (field != null && order != null) {
            q = String.format("SELECT * FROM  Mails WHERE userID = %d AND folderID = %d " +
                            " ORDER BY %s %s LIMIT %d OFFSET %d;",
                    userID, folderID, field, order, displayNum, begin);
        }
        else {
            q = String.format("SELECT * FROM  Mails WHERE userID = %d AND folderID = %d " +
                            " ORDER BY emailDate Desc LIMIT %d OFFSET %d;",
                    userID, folderID, displayNum, begin);
        }

        getMailsQuery = c.prepareStatement(q);
        ResultSet rs = getMailsQuery.executeQuery();
        while (rs.next()) {
            int mailID = rs.getInt("mailID");
            String sender = rs.getString("sender");
            String recipient = rs.getString("recipient");
            String cc = rs.getString("cc");
            String bcc = rs.getString("bcc");
            String subject = rs.getString("subject");
            String content = rs.getString("content");
            String date = rs.getString("emailDate");
            String emailType = rs.getString("mailType");
            String uidl = rs.getString("uidl");
            String ifRead = rs.getString("read");

            // Special deal with status: turn all read mails'ifRead status
            // to null so as stringtemplate can process
            if ("1".equals(ifRead)) {
                ifRead = null;
            }
            Email mail = new Email(mailID, userID, sender, recipient, cc, bcc, subject, content, date,
                    emailType, uidl, ifRead, folderID, null);
            pageMails.add(mail);
        }

        //find attachment
        ArrayList<Attachment> attachList = new ArrayList<>();
        for(Email mail: pageMails) {
            findQuery = c.prepareStatement("SELECT * FROM Attachments WHERE mailID = ?;");
            findQuery.setInt(1, mail.getMailID());
            ResultSet rsFind = findQuery.executeQuery();
            while (rsFind.next()) {
                int attachID = rsFind.getInt("attachID");
                String fileName = rsFind.getString("fileName");
                String filePath = rsFind.getString("filePath");
                attach = new Attachment(attachID, mail.getMailID(), fileName, filePath);
                attachList.add(attach);
            }
            mail.setAttachment(attachList);
        }
        return  pageMails;
    }


    // Get total email number of a user
    public int getTotalNum(int userID, String mailType, int folderID) throws SQLException, ClassNotFoundException {
        int num = 0;
        c = new DBManager().getConnection();
        PreparedStatement q = c.prepareStatement("SELECT COUNT(*) FROM Mails WHERE userID = ? AND mailType = ?" +
                "AND folderID = ?;");
        q.setInt(1, userID);
        q.setString(2, mailType);
        q.setInt(3, folderID);
        ResultSet rs = q.executeQuery();
        if (rs.next()) {
            num = rs.getInt(1);
        }
        return num;
    }
    // Get total email number of a user in a folder
    public int getTotalNumOfFolderRecords(int userID, int folderID) throws SQLException, ClassNotFoundException {
        int num = 0;
        c = new DBManager().getConnection();
        PreparedStatement q = c.prepareStatement("SELECT COUNT(*) FROM Mails WHERE userID = ? AND folderID = ?;");
        q.setInt(1, userID);
        q.setInt(2, folderID);
        ResultSet rs = q.executeQuery();
        if (rs.next()) {
            num = rs.getInt(1);
        }
        return num;
    }

    // Get unread email number of a user
    public int getUnreadMailNum(int userID, String filed) throws SQLException, ClassNotFoundException {
        int num = 0;
        c = new DBManager().getConnection();
        PreparedStatement q = c.prepareStatement("SELECT COUNT(*) FROM Mails WHERE userID = ? " +
                "AND mailType= ? AND read = \"0\" AND folderID = 0;");
        q.setInt(1, userID);
        q.setString(2, filed);
        ResultSet rs = q.executeQuery();
        if (rs.next()) {
            num = rs.getInt(1);
        }
        return num;
    }


    /**
     * ===================== For Email function ==========================
     */

    // Given a mailID and find it in Mails table and set it status to be read
    public Email findMail(int mailID) throws SQLException, ClassNotFoundException {
        Email mail = null;
        Attachment attach = null;
        PreparedStatement findQuery;
        c = new DBManager().getConnection();
        PreparedStatement find = c.prepareStatement("SELECT * FROM Mails WHERE mailID = ?;");
        find.setInt(1, mailID);
        ResultSet rs = find.executeQuery();
        if (rs.next()) {
            int userID = rs.getInt("userID");
            String sender = rs.getString("sender");
            String recipient = rs.getString("recipient");
            String cc = rs.getString("cc");
            String bcc = rs.getString("bcc");
            String subject = rs.getString("subject");
            String content = rs.getString("content");
            String date = rs.getString("emailDate");
            String mailType = rs.getString("mailType");
            String uidl = rs.getString("uidl");
            String ifRead = rs.getString("read");
            int folderID = rs.getInt("folderID");

            mail = new Email(mailID, userID, sender, recipient, cc, bcc, subject, content,
                    date, mailType, uidl, ifRead, folderID, null);
            find.close();
        }


        //find attachment
        ArrayList<Attachment> attachList = new ArrayList<>();

        findQuery = c.prepareStatement("SELECT * FROM Attachments WHERE mailID = ?;");
        findQuery.setInt(1, mail.getMailID());
        ResultSet rsFind = findQuery.executeQuery();
        while (rsFind.next()) {
            int attachID = rsFind.getInt("attachID");
            String fileName = rsFind.getString("fileName");
            String filePath = rsFind.getString("filePath");
            attach = new Attachment(attachID, mail.getMailID(), fileName, filePath);
            attachList.add(attach);
        }
        mail.setAttachment(attachList);


        // Special deal with status: turn all read mails'ifRead status
        // to null so as stringtemplate can process
        if (mail != null && "0".equals(mail.getIfRead())) {
            mail = updateStatus(mailID, mail);
            mail.setIfRead(null);
        }

        return mail;
    }

    public Attachment findAttachment(int attachID) throws SQLException {
        Attachment attach = null;

        PreparedStatement findQuery = c.prepareStatement("SELECT * FROM Attachments WHERE attachID = ?;");
        findQuery.setInt(1, attachID);
        ResultSet rsFind = findQuery.executeQuery();
        if (rsFind.next()) {
            int mailID = rsFind.getInt("mailID");
            String fileName = rsFind.getString("fileName");
            String filePath = rsFind.getString("filePath");
            attach = new Attachment(attachID, mailID, fileName, filePath);
        }
        return attach;
    }

    // Method for read/unread feature
    public Email updateStatus(int mailID, Email mail) throws SQLException, ClassNotFoundException {
        // Update database
        c = new DBManager().getConnection();
        PreparedStatement update = c.prepareStatement("UPDATE Mails SET read = \"1\" WHERE mailID ="
                + mailID + " AND folderID = 0;");
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
            if (debug) System.err.println("save fail");
        } else {
            if (debug) System.out.println("save mail success");
        }

        return mail;
    }

    // Empty entire trash box
    public ArrayList<Integer> emptyTrash(ArrayList<EmailAccount> accountList) throws SQLException, ClassNotFoundException {
        ArrayList<Integer> movedMailList = new ArrayList<>();
        c = new DBManager().getConnection();
        for (EmailAccount account : accountList) {
            int userID = account.getUserID();
            PreparedStatement find = c.prepareStatement("SELECT * FROM Mails WHERE mailType = \"trash\" " +
                    "AND userID = ?;");
            find.setInt(1, userID);
            ResultSet rs = find.executeQuery();
            while (rs.next()) {
                int mailID = rs.getInt("mailID");
                movedMailList.add(mailID);

            }
            find.close();
        }

        PreparedStatement empty;
        for (EmailAccount account : accountList) {
            int userID = account.getUserID();
            empty = c.prepareStatement("UPDATE Mails SET mailType = \"empty\" WHERE userID =" + userID
                    + " AND mailType=\"trash\"" + "AND folderID = 0;");
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
        }

        return movedMailList;
    }

}
