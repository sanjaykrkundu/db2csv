package tool1.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.microsoft.sqlserver.jdbc.SQLServerException;

import tool1.exceptions.DBException;
import tool1.mapping.MandatoryProperties;

public class DBConnection {

	private static Connection connection = null;
	// private static DBConnection dbConnection = null;

	private DBConnection() throws DBException {
		String url = "jdbc:sqlserver://" + Configuration.get(MandatoryProperties.DB_SERVER.name()) + ":"
				+ Configuration.get(MandatoryProperties.DB_PORT.name()) + ";DatabaseName="
				+ Configuration.get(MandatoryProperties.DB_NAME.name()) + ";user=" + Configuration.getUsername()
				+ ";password=" + Configuration.getPassword() + ";";

		try {
			connection = DriverManager.getConnection(url);
		} catch (SQLServerException e) {
			if (e.getErrorCode() == 0)
				throw new DBException("Connection Failed. Check DB Server/Port.");
			else if (e.getErrorCode() == 18456)
				throw new DBException("Login Failed. Check username/password");
			else if (e.getErrorCode() == 4060)
				throw new DBException("Cann't Open Database. Check DB Name");
			else
				throw new DBException("Error while connection to DB");
		} catch (SQLException e) {
			throw new DBException("Error while connection to DB");
		}

	}

	/*
	 * public static synchronized DBConnection getDbConnection() throws
	 * DBException { if (dbConnection == null) { dbConnection = new
	 * DBConnection(); } return dbConnection; }
	 */
	public static synchronized Connection getConnection() throws DBException {
		if (connection == null) {
			new DBConnection();
		}
		return connection;
	}

	public static void closeConnection() throws DBException {
		try {
			connection.close();
			connection = null;
		} catch (SQLException e) {
			throw new DBException("Error while closing the connection");
		}
	}

}
