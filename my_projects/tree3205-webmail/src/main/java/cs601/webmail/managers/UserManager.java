package cs601.webmail.managers;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserManager {

    public static Boolean debug = false;
    private static UserManager instance;
    private Connection c;
    private static final String ALGORITHM = "AES";
    private static final byte[] keyValue =
            new byte[] { 'T', 'y', 'i', 's', 'I', 'c', 'A', 'S', 'o', 'V', 'r', 'e', 'P', 'K', 'F', 'M' };

    public static synchronized UserManager instance() {
        if( instance == null ) {
            instance = new UserManager();
        }
        return instance;
    }

    public User createUser(String username, String webmailPWD) throws Exception {
        c = new DBManager().getConnection();
        User u = null;
        PreparedStatement insertQuery;
        int rsInsert = 0;

        // Make sure no user name duplicate
        PreparedStatement findQuery = c.prepareStatement("SELECT * FROM Users WHERE username = ?;");
        findQuery.setString(1, username);
        ResultSet rs = findQuery.executeQuery();
        if (debug) {
            System.out.println("validate register query: " + findQuery);
            System.out.println(rs.toString());
        }

        c.setAutoCommit(false);  // Need to do shut down the auto commit,so can do transaction manually

        if (!rs.next()) {  // check register email and username not exist
            String currentTime = String.valueOf(System.currentTimeMillis() / 1000l);
            String saltedwebmailPWD = getSaltedPWD(currentTime, webmailPWD);
            int userID = new DBManager().getCurrentNum("Users", "userID") + 1;

            insertQuery = c.prepareStatement("INSERT INTO Users VALUES (?, ?, ?, ?);");
            insertQuery.setInt(1, userID);
            insertQuery.setString(2, username);
            insertQuery.setString(3, saltedwebmailPWD);
            insertQuery.setString(4, currentTime);

            // Use transaction to ensure the data integrity
            try {
                rsInsert = insertQuery.executeUpdate();
                c.commit();                  // commit the changes here
                if (debug) {
                    System.out.println("insert query: " + insertQuery);
                }
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

            if ( rsInsert != 1 ) {
                if (debug) System.err.println("Bad update");
                return u;
            }
            else {
                // After insert successfully, return new User
                u = new User(userID, username);
            }
        }
        findQuery.close();
        return u;
    }

    public User authenticate(String username, String webmailPWD) throws Exception {
        c = new DBManager().getConnection();
        User u = null;
        // Check email exists
        PreparedStatement checkQuery = c.prepareStatement("SELECT * FROM Users WHERE " +
                "username = ?;");
        checkQuery.setString(1, username);
        ResultSet rs = checkQuery.executeQuery();
        if (debug) {
            System.out.println("validate register query: " + checkQuery);
            System.out.println(rs.toString());
        }

        // If email match, check password
        if (rs.next()) {
            String createTime = rs.getString("createtime");
            String pwd = rs.getString("webmailPWD");
            String saltedPWD = getSaltedPWD(createTime, webmailPWD);
            if (saltedPWD.equals(pwd)) {
                int userID = rs.getInt("userID");
                u = new User(userID, username);
            }
        }
        checkQuery.close();
        return u;
    }

    public void saveEmailAccount(User u, String email, String password, String smtp, String pop,
                          int smtpport, int popport) throws Exception {
        c = new DBManager().getConnection();
        EmailAccount account = null;
        PreparedStatement insertQuery;
        int accountID = new DBManager().getCurrentNum("EmailAccount", "accountID") + 1;
        int rsUpdate = 0;
        String encryptedPWD = encrypt(password);

        insertQuery = c.prepareStatement("INSERT INTO EmailAccount VALUES (?, ?, ?, ?, ?, ?, ?,?);");
        insertQuery.setInt(1, accountID);
        insertQuery.setInt(2, u.getUserID());
        insertQuery.setString(3, email);
        insertQuery.setString(4, encryptedPWD);
        insertQuery.setString(5, smtp);
        insertQuery.setString(6, pop);
        insertQuery.setInt(7, smtpport);
        insertQuery.setInt(8, popport);

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
            if (debug) System.err.println("Bad update");
        }
    }

    // Find all email accounts under the user name
    public ArrayList<EmailAccount> getAllAcounts(User u) throws Exception {
        ArrayList<EmailAccount> accountsList = new ArrayList<>();
        c = new DBManager().getConnection();
        PreparedStatement findQuery = c.prepareStatement("SELECT * FROM EmailAccount WHERE " +
                "userID = ?;");
        findQuery.setInt(1, u.getUserID());
        ResultSet rs = findQuery.executeQuery();
        while(rs.next()) {
            int accountID = rs.getInt("accountID");
            String email = rs.getString("email");
            String encryptedPWD = rs.getString("password");
            String decryptedPWD = decrypt(encryptedPWD);
            String smtp = rs.getString("smtp");
            String pop = rs.getString("pop");
            int smtpport = rs.getInt("smtpport");
            int popport = rs.getInt("popport");
            EmailAccount account = new EmailAccount(accountID, u.getUserID(), email, decryptedPWD,
                                smtp, pop, smtpport, popport);
            accountsList.add(account);
        }
        return accountsList;
    }


    public EmailAccount findAccount(String sender) throws Exception {
        EmailAccount account = null;
        c = new DBManager().getConnection();
        PreparedStatement find = c.prepareStatement("SELECT * FROM EmailAccount WHERE email = ?;");
        find.setString(1, sender);
        ResultSet rs = find.executeQuery();
        if (rs.next()) {
            int accountID = rs.getInt("accountID");
            int userID = rs.getInt("userID");
            String email = rs.getString("email");
            String pwd = rs.getString("password");
            String password = decrypt(pwd);
            String smtp = rs.getString("smtp");
            String pop = rs.getString("pop");
            int smtpport = rs.getInt("smtpport");
            int popport = rs.getInt("popport");

            account = new EmailAccount(accountID, userID, email, password, smtp, pop,
                    smtpport, popport);
            find.close();
        }
        return account;
    }


    // Get salted password by createTime and password
    public String getSaltedPWD(String createTime, String password) {
        int hash1 = myHashFunc(createTime);
        int hash2 = myHashFunc(password);
        String saltedPWD = String.valueOf(hash1) + String.valueOf(hash2);
        return saltedPWD;
    }

    // Hash Function from http://stackoverflow.com/questions/2624192/good-hash-function-for-strings
    private int myHashFunc(String s) {
        int hash = 7;
        for (int i = 0; i < s.length(); i++) {
            hash = hash * 31 + s.charAt(i);
        }
        return hash;
    }

    // Encrypt and Decrypt from
    // http://stackoverflow.com/questions/7762771/how-do-i-encrypt-decrypt-a-string-with-another-string-as-a-password
    private String encrypt(String valueToEnc) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = c.doFinal(valueToEnc.getBytes());
        String encryptedValue = new BASE64Encoder().encode(encValue);
        return encryptedValue;
    }

    private String decrypt(String encryptedValue) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedValue);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGORITHM);
        return key;
    }
}
