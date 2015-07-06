import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

/**
 * Part of the {@link LoginServer} example. Handles all database-related
 * actions.
 *
 * @author Sophie Engle
 */
public class LoginDatabaseHandler {

	/** Makes sure only one database handler is instantiated. */
	private static LoginDatabaseHandler singleton = new LoginDatabaseHandler();

	/** Used to create necessary tables for this example. */
	private static final String CREATE_SQL =
			"CREATE TABLE Login_Users (" +
					"userid INTEGER AUTO_INCREMENT PRIMARY KEY, " +
					"username VARCHAR(32) NOT NULL UNIQUE, " +
					"password CHAR(64) NOT NULL, " +
					"usersalt CHAR(32) NOT NULL);";

	/** Used to insert a new user into the database. */
	private static final String REGISTER_SQL =
			"INSERT INTO Login_Users (username, password, usersalt) " +
					"VALUES (?, ?, ?);";

	/** Used to determine if a username already exists. */
	private static final String USER_SQL =
			"SELECT username FROM Login_Users WHERE username = ?";

	/** Used to retrieve the salt associated with a specific user. */
	private static final String SALT_SQL =
			"SELECT usersalt FROM Login_Users WHERE username = ?";

	/** Used to authenticate a user. */
	private static final String AUTH_SQL =
			"SELECT username FROM Login_Users " +
					"WHERE username = ? AND password = ?";

	/** Used to change user's password. */
	private static final String CHANGE_PASSWORD_SQL = 
			"UPDATE Login_Users SET password = ? WHERE username = ?";

	/** Used to remove a user from the database. */
	private static final String DELETE_SQL =
			"DELETE FROM Login_Users WHERE username = ?";

	/** Used to create search history table. */
	private static final String CREATE_HISTORY_SQL = 
			"CREATE TABLE Query_Histories (" +
					"historyid INTEGER AUTO_INCREMENT PRIMARY KEY, " +
					"query VARCHAR(100) NOT NULL, " +
					"username VARCHAR(32) NOT NULL, " +
					"FOREIGN KEY (username) REFERENCES Login_Users(username));";

	private static final String STORE_HISTORY_SQL = 
			"INSERT INTO Query_Histories (query, username) " +
					"VALUES (?, ?);";

	private static final String GET_HISTORY_SQL = 
			"SELECT query FROM Query_Histories WHERE username = ?";

	private static final String CLEAR_HISTORY_SQL = 
			"DELETE FROM Query_Histories WHERE username = ?";
	
	/** Used to create visited pages table. */
	private static final String CREATE_VISITED_SQL = 
			"CREATE TABLE Visited_Pages (" +
					"pageid INTEGER AUTO_INCREMENT PRIMARY KEY, " +
					"url VARCHAR(200) NOT NULL, " +
					"username VARCHAR(32) NOT NULL, " +
					"FOREIGN KEY (username) REFERENCES Login_Users(username));";
	
	private static final String STORE_VISITED_SQL = 
			"INSERT INTO Visited_Pages (url, username) " +
					"VALUES (?, ?);";

	private static final String GET_VISITED_SQL = 
			"SELECT url FROM Visited_Pages WHERE username = ?";

	/** Used to configure connection to database. */
	private DatabaseConfigurator db;

	/** Used to generate password hash salt for user. */
	private Random random;

	/**
	 * Initializes a database handler for the Login example.
	 */
	private LoginDatabaseHandler() {
		db = new DatabaseConfigurator();
		random = new Random(System.currentTimeMillis());

		Status status = null;

		// exit if unable to establish connection with database
		status = db.testConfig();
		if (status != Status.OK) {
			System.out.println(status);
			System.exit(status.ordinal());
		}

		// exit if unable to setup tables necessary for this example
		status = setupTables();
		if (status != Status.OK) {
			System.out.println(status);
			System.exit(status.ordinal());
		}
	}

	/**
	 * Gets the single instance of the database handler.
	 *
	 * @return instance of the database handler
	 */
	public static LoginDatabaseHandler getInstance() {
		return singleton;
	}

	/**
	 * Registers a new user, placing the username, password hash, and
	 * salt into the database if the username does not already exist.
	 *
	 * @param newuser - username of new user
	 * @param newpass - password of new user
	 * @return {@link Status.OK} if registration successful
	 */
	public Status registerUser(String newuser, String newpass) {
		Connection connection = null;
		PreparedStatement statement = null;

		Status status = Status.ERROR;

		System.out.println("Registering " + newuser + ".");

		// make sure we have non-null and non-emtpy values for login
		if (newuser == null || newpass == null ||
				newuser.trim().isEmpty() || newpass.trim().isEmpty()) {
			status = Status.INVALID_LOGIN;
			System.out.println(status);
			return status;
		}

		try {
			connection = db.getConnection();

			// make sure username doesn't already exist
			if (userExists(connection, newuser)) {
				status = Status.DUPLICATE_USER;
			}
			else {
				// create random salt for user
				byte[] saltBytes = new byte[16];
				random.nextBytes(saltBytes);

				String usersalt = encodeHex(saltBytes, 32);
				String passhash = getHash(newpass, usersalt);

				try {
					// insert username, password hash, and salt
					statement = connection.prepareStatement(REGISTER_SQL);
					statement.setString(1, newuser);
					statement.setString(2, passhash);
					statement.setString(3, usersalt);
					statement.executeUpdate();

					status = Status.OK;
				}
				catch (SQLException ex) {
					status = Status.SQL_EXCEPTION;

					System.out.println("Unable to register user " + newuser + ".");
					System.out.println(status);
					ex.printStackTrace();
				}
			}
		}
		catch (Exception ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println(status);
			ex.printStackTrace();
		}
		finally {
			try {
				statement.close();
				connection.close();
			}
			catch (Exception ignored) {
				// do nothing
			}
		}

		return status;
	}

	/**
	 * Change the password of the specific user.
	 *
	 * @param user - username of whom want to change password
	 * @param newpass - new password of the user
	 * @return {@link Status.OK} if changing successful
	 */
	public Status changePassword(String user, String newpass) {
		Connection connection = null;
		PreparedStatement statement = null;

		Status status = Status.ERROR;

		System.out.println("Changing password for " + user + ".");

		// make sure we have non-null and non-emtpy values for login
		if (user == null || newpass == null ||
				user.trim().isEmpty() || newpass.trim().isEmpty()) {
			status = Status.INVALID_LOGIN;
			System.out.println(status);
			return status;
		}

		try {
			connection = db.getConnection();

			String usersalt = getSalt(connection, user);
			String passhash = getHash(newpass, usersalt);

			try {
				// update password hash
				statement = connection.prepareStatement(CHANGE_PASSWORD_SQL);
				statement.setString(1, passhash);
				statement.setString(2, user);
				statement.executeUpdate();

				status = Status.OK;
			}
			catch (SQLException ex) {
				status = Status.SQL_EXCEPTION;

				System.out.println("Unable to change password for " + user + ".");
				System.out.println(status);
				ex.printStackTrace();
			}
		}
		catch (Exception ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println(status);
			ex.printStackTrace();
		}
		finally {
			try {
				statement.close();
				connection.close();
			}
			catch (Exception ignored) {
				// do nothing
			}
		}

		return status;
	}

	/**
	 * Checks if the provided username and password match what is stored
	 * in the database. Must retrieve the salt and hash the password to
	 * do the comparison.
	 *
	 * @param username - username to authenticate
	 * @param password - password to authenticate
	 * @return {@link Status.OK} if authentication successful
	 */
	public Status authenticateUser(String username, String password) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet results = null;

		Status status = Status.ERROR;

		System.out.println("Authenticating user " + username + ".");

		try {
			connection = db.getConnection();

			try {
				// get salt for user
				String usersalt = getSalt(connection, username);
				String passhash = getHash(password, usersalt);

				// test if username and password match
				statement = connection.prepareStatement(AUTH_SQL);
				statement.setString(1, username);
				statement.setString(2, passhash);

				results = statement.executeQuery();

				if (results.next()) {
					status = Status.OK;
				}
				else {
					status = Status.INVALID_LOGIN;
				}

				statement.close();
			}
			catch (Exception ex) {
				status = Status.SQL_EXCEPTION;
				System.out.println(status);
				ex.printStackTrace();
			}
		}
		catch (Exception ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println(status);
			ex.printStackTrace();
		}
		finally {
			try {
				// make sure database connection is closed
				connection.close();
			}
			catch (Exception ignored) {
				// do nothing
			}
		}

		return status;
	}

	/**
	 * Removes a user from the database if the username and password are
	 * provided correctly.
	 *
	 * @param username - username to remove
	 * @param password - password of user
	 * @return {@link Status.OK} if removal successful
	 */
	public Status removeUser(String username, String password) {
		Status status = authenticateUser(username, password);

		System.out.println("Removing user " + username + ".");

		if (status == Status.OK) {
			Connection connection = null;
			PreparedStatement statement = null;

			try {
				connection = db.getConnection();

				try {
					statement = connection.prepareStatement(DELETE_SQL);
					statement.setString(1, username);

					int count = statement.executeUpdate();
					status = (count == 1) ? Status.OK : Status.INVALID_USER;

					statement.close();
				}
				catch (Exception ex) {
					status = Status.SQL_EXCEPTION;
					System.out.println(status);
					ex.printStackTrace();
				}
			}
			catch (Exception ex) {
				status = Status.CONNECTION_FAILED;
				System.out.println(status);
				ex.printStackTrace();
			}
			finally {
				try {
					// make sure database connection is closed
					connection.close();
				}
				catch (Exception ignored) {
					// do nothing
				}
			}
		}

		return status;
	}

	/**
	 * Inserts a new query history, placing the query and username.
	 *
	 * @param query - new coming query
	 * @param username - username of query owner
	 * @return {@link Status.OK} if insertion successful
	 */
	public Status insertQueryHistory(String query, String username) {
		Connection connection = null;
		PreparedStatement statement = null;

		Status status = Status.ERROR;

		System.out.println("Inserting Query:" + query + " of " + username + "'s.");

		// make sure we have non-null and non-emtpy values for login
		if (query == null || username == null ||
				query.trim().isEmpty() || username.trim().isEmpty()) {
			status = Status.INVALID_LOGIN;
			System.out.println(status);
			return status;
		}

		try {
			connection = db.getConnection();

			// insert query, username
			statement = connection.prepareStatement(STORE_HISTORY_SQL);
			statement.setString(1, query);
			statement.setString(2, username);
			statement.executeUpdate();

			status = Status.OK;

		}
		catch (SQLException ex) {
			status = Status.SQL_EXCEPTION;

			System.out.println("Unable to insert " + query + ".");
			System.out.println(status);
			ex.printStackTrace();
		}
		catch (Exception ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println(status);
			ex.printStackTrace();
		}
		finally {
			try {
				statement.close();
				connection.close();
			}
			catch (Exception ignored) {
				// do nothing
			}
		}

		return status;
	}

	/**
	 * Gets the query history for a specific user.
	 *
	 * @param user - which user to retrieve query history for
	 * @throws SQLException if any issues with database connection
	 */
	public ArrayList<String> getQueryHistory(String user) throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;

		ResultSet results = null;
		String query = null;
		ArrayList<String> queryList = new ArrayList<String>();

		try {
			connection = db.getConnection();
			statement = connection.prepareStatement(GET_HISTORY_SQL);
			statement.setString(1, user);

			results = statement.executeQuery();

			while (results.next()) {
				query = results.getString("query");
				queryList.add(query);
			}

			results.close();
			statement.close();
		}
		catch (Exception ignored) {
			// do nothing
		}

		return queryList;
	}

	/**
	 * Clear query histories for specific user.
	 *
	 * @param username - username for whom clear the query history
	 * @return {@link Status.OK} if clearing successful
	 */
	public Status clearQueryHistory(String username) {
		Connection connection = null;
		PreparedStatement statement = null;

		Status status = Status.ERROR;

		System.out.println("Clear query for:" + username + ".");

		// make sure we have non-null and non-emtpy values for login
		if (username == null || username.trim().isEmpty()) {
			status = Status.INVALID_LOGIN;
			System.out.println(status);
			return status;
		}

		try {
			connection = db.getConnection();

			// insert query, username
			statement = connection.prepareStatement(CLEAR_HISTORY_SQL);
			statement.setString(1, username);
			statement.executeUpdate();

			status = Status.OK;

		}
		catch (SQLException ex) {
			status = Status.SQL_EXCEPTION;

			System.out.println("Unable to clear query history for " + username + ".");
			System.out.println(status);
			ex.printStackTrace();
		}
		catch (Exception ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println(status);
			ex.printStackTrace();
		}
		finally {
			try {
				statement.close();
				connection.close();
			}
			catch (Exception ignored) {
				// do nothing
			}
		}

		return status;
	}
	
	/**
	 * Inserts a visited page, placing the url and username.
	 *
	 * @param url - new coming visited page
	 * @param username - username of the visited page
	 * @return {@link Status.OK} if insertion successful
	 */
	public Status insertVisitedPage(String url, String username) {
		Connection connection = null;
		PreparedStatement statement = null;

		Status status = Status.ERROR;

		System.out.println("Inserting Url:" + url + " of " + username + "'s.");

		// make sure we have non-null and non-emtpy values for login
		if (url == null || username == null ||
				url.trim().isEmpty() || username.trim().isEmpty()) {
			status = Status.INVALID_LOGIN;
			System.out.println(status);
			return status;
		}

		try {
			connection = db.getConnection();

			// insert query, username
			statement = connection.prepareStatement(STORE_VISITED_SQL);
			statement.setString(1, url);
			statement.setString(2, username);
			statement.executeUpdate();

			status = Status.OK;

		}
		catch (SQLException ex) {
			status = Status.SQL_EXCEPTION;

			System.out.println("Unable to insert " + url + ".");
			System.out.println(status);
			ex.printStackTrace();
		}
		catch (Exception ex) {
			status = Status.CONNECTION_FAILED;
			System.out.println(status);
			ex.printStackTrace();
		}
		finally {
			try {
				statement.close();
				connection.close();
			}
			catch (Exception ignored) {
				// do nothing
			}
		}

		return status;
	}

	/**
	 * Gets the visited pages for a specific user.
	 *
	 * @param user - which user to retrieve visited pages for
	 * @throws SQLException if any issues with database connection
	 */
	public ArrayList<String> getVisitedPage(String user) throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;

		ResultSet results = null;
		String url = null;
		ArrayList<String> queryList = new ArrayList<String>();

		try {
			connection = db.getConnection();
			statement = connection.prepareStatement(GET_VISITED_SQL);
			statement.setString(1, user);

			results = statement.executeQuery();

			while (results.next()) {
				url = results.getString("url");
				queryList.add(url);
			}

			results.close();
			statement.close();
		}
		catch (Exception ignored) {
			// do nothing
		}

		return queryList;
	}

	/**
	 * Checks if necessary table exists in database, and if not tries to
	 * create it.
	 *
	 * @return {@link Status.OK} if table exists or create is successful
	 */
	private Status setupTables() {

		Connection connection = null;
		Statement statement = null;
		ResultSet results = null;
		Status status = Status.ERROR;

		try {
			connection = db.getConnection();
			statement = connection.createStatement();
			results = statement.executeQuery("SHOW TABLES LIKE 'Login_Users';");

			if (!results.next()) {
				// attempt to create table
				statement.executeUpdate(CREATE_SQL);

				// check if table now exists
				results = statement.executeQuery("SHOW TABLES LIKE 'Login_Users';");
				status = (results.next()) ? Status.OK : Status.CREATE_FAILED;
			}
			else {
				// could check if correct columns here
				status = Status.OK;
			}

			results.close();

			// modified here, add a new table Query_Histories.
			results = statement.executeQuery("SHOW TABLES LIKE 'Query_Histories';");

			if (!results.next()) {
				// attempt to create table
				statement.executeUpdate(CREATE_HISTORY_SQL);

				// check if table now exists
				results = statement.executeQuery("SHOW TABLES LIKE 'Query_Histories';");
				status = (results.next()) ? Status.OK : Status.CREATE_FAILED;
			}
			else {
				// could check if correct columns here
				status = Status.OK;
			}
			
			results = statement.executeQuery("SHOW TABLES LIKE 'Visited_Pages';");

			if (!results.next()) {
				// attempt to create table
				statement.executeUpdate(CREATE_VISITED_SQL);

				// check if table now exists
				results = statement.executeQuery("SHOW TABLES LIKE 'Visited_Pages';");
				status = (results.next()) ? Status.OK : Status.CREATE_FAILED;
			}
			else {
				// could check if correct columns here
				status = Status.OK;
			}

			statement.close();
		}
		catch (Exception ex) {
			status = Status.CREATE_FAILED;
			System.out.println(status);
			ex.printStackTrace();
		}
		finally {
			try {
				// make sure database connection is closed
				connection.close();
			}
			catch (Exception ignored) {
				// do nothing
			}
		}

		return status;
	}

	/**
	 * Tests if a user already exists in the database. Requires an active
	 * database connection.
	 *
	 * @param connection - active database connection
	 * @param user - username to check
	 * @return <code>true</code> if user exists in database
	 * @throws SQLException if problem with database connection
	 */
	private boolean userExists(Connection connection, String user) throws SQLException {
		assert connection != null;
		assert user != null;

		PreparedStatement statement = null;
		ResultSet results = null;
		boolean exists = false;

		// user is not safe to use directly
		statement = connection.prepareStatement(USER_SQL);
		statement.setString(1, user);

		results = statement.executeQuery();

		// if any results, then user exists
		if (results.next()) {
			exists = true;
		}

		try {
			results.close();
			statement.close();
		}
		catch (Exception ignored) {
			// do nothing
		}

		return exists;
	}

	/**
	 * Calculates the hash of a password and salt using SHA-256.
	 *
	 * @param password - password to hash
	 * @param salt - salt associated with user
	 * @return hashed password
	 */
	private String getHash(String password, String salt) {
		String salted = salt + password;
		String hashed = salted;

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salted.getBytes());
			hashed = encodeHex(md.digest(), 64);
		}
		catch (Exception ex) {
			System.out.println("Unable to properly hash password.");
			ex.printStackTrace();
		}

		return hashed;
	}

	/**
	 * Returns the hex encoding of a byte array.
	 *
	 * @param bytes - byte array to encode
	 * @param length - desired length of encoding
	 * @return hex encoded byte array
	 */
	private String encodeHex(byte[] bytes, int length) {
		BigInteger bigint = new BigInteger(1, bytes);
		String hex = String.format("%0" + length + "X", bigint);

		assert hex.length() == length;
		return hex;
	}

	/**
	 * Gets the salt for a specific user.
	 *
	 * @param connection - active database connection
	 * @param user - which user to retrieve salt for
	 * @return salt for the specified user or null if user does not exist
	 * @throws SQLException if any issues with database connection
	 */
	private String getSalt(Connection connection, String user) throws SQLException {
		assert connection != null;
		assert user != null;

		PreparedStatement statement = null;
		ResultSet results = null;
		String salt = null;

		statement = connection.prepareStatement(SALT_SQL);
		statement.setString(1, user);

		results = statement.executeQuery();

		if (results.next()) {
			salt = results.getString("usersalt");
		}

		try {
			results.close();
			statement.close();
		}
		catch (Exception ignored) {
			// do nothing
		}

		return salt;
	}

	public static void main(String[] args) throws Exception {
		LoginDatabaseHandler logindb = LoginDatabaseHandler.getInstance();

		Status status = logindb.registerUser("test01", "test01");
		System.out.println("Register test01: " + status);

		status = logindb.registerUser("test02", "test02");
		System.out.println("Register test02: " + status);

		status = logindb.registerUser("test01", "test01");
		System.out.println("Register test01: " + status);

		status = logindb.authenticateUser("test01", "test01");
		System.out.println("Auth test01/test01: " + status);

		status = logindb.authenticateUser("test01", "mypass");
		System.out.println("Auth test01/mypass: " + status);

		status = logindb.removeUser("test01", "test01");
		System.out.println("Remove test01: " + status);

		status = logindb.removeUser("test02", "test02");
		System.out.println("Remove test02: " + status);
	}
}
