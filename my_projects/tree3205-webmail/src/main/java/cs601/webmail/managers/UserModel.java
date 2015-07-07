package cs601.webmail.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserModel {

    public static Boolean debug = false;
    public static UserManager objects;
    public Connection c;

    public UserModel() {
        this.objects = UserManager.instance();
    }

    public boolean changePwd(User user, String currentPwd, String newPwd) throws SQLException, ClassNotFoundException {
        boolean succ = false;
        c = new DBManager().getConnection();
        PreparedStatement updateQuery;
        int rsUpdate = 0;
        //Comfirm currentPwd correct
        PreparedStatement checkQuery = c.prepareStatement("SELECT * FROM Users WHERE " +
                "username = ?;");
        checkQuery.setString(1, user.getName());
        ResultSet rs = checkQuery.executeQuery();
        if (debug) {
            System.out.println("validate register query: " + checkQuery);
            System.out.println(rs.toString());
        }
        if (rs.next()) {
            String createTime = rs.getString("createtime");
            String pwd = rs.getString("webmailPWD");
            String saltedPWD = objects.getSaltedPWD(createTime, currentPwd);
            if (saltedPWD.equals(pwd)) {
                //Update the new password
                String currentTime = String.valueOf(System.currentTimeMillis() / 1000l);
                String newSaltedPwd = objects.getSaltedPWD(currentTime, newPwd);
                updateQuery = c.prepareStatement("UPDATE Users SET webmailPWD = ?, createtime = ?;");
                updateQuery.setString(1, newSaltedPwd);
                updateQuery.setString(2, currentTime);

                c.setAutoCommit(false);  // Need to do shut down the auto commit,so can do transaction manually
                try {
                    rsUpdate = updateQuery.executeUpdate();
                    c.commit();                  // commit the changes here
                    if (debug) {
                        System.out.println("insert query: " + updateQuery);
                    }
                }catch (SQLException e) {
                    try {
                        c.rollback();            //Error: roll back the changes like nothing happened
                    }catch (SQLException e1) {
                        e.printStackTrace();
                    }
                }finally {
                    c.setAutoCommit(true);
                    checkQuery.close();
                    updateQuery.close();
                }

                if ( rsUpdate != 1 ) {
                    if (debug) System.err.println("Bad update for change password");
                }
                else {
                    // After insert successfully, return new User
                    succ = true;
                }
            }
        }
        return succ;
    }
}


