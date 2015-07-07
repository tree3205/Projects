package cs601.webmail.managers;

import java.sql.*;


public class DBManager {

    private static Connection c = null;
    public static Boolean debug = false;
    private static String dbFile = "db/webmail.db";

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        if (c == null) {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
            if (debug) {
                System.out.println(c);
                System.out.println("Opened database successfully");
            }
        }
            return c;
    }


    // SELECT count(*) FROM COMPANY;
    public int getCurrentNum(String table, String field) throws SQLException {
        int currentNum = 0;
        PreparedStatement getIDQuery;
        String query = "SELECT " + field + " FROM " + table + " WHERE " +
                field + " = (SELECT MAX(" + field + ") FROM " + table + ");";
        getIDQuery = c.prepareStatement(query);

        ResultSet rs = getIDQuery.executeQuery();
        if (debug) {
            System.out.println("getID query: " + getIDQuery);
            System.out.println(rs.toString());
        }
        if (rs.next()) {
            currentNum = rs.getInt(field);
        }
        getIDQuery.close();
        return currentNum;
    }
}
