package com.datascience.utils.storage;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Properties;

/**
 * User: artur
 * Date: 4/10/13
 */
public class DBStorage {

	private static Logger logger = Logger.getLogger(DBStorage.class);
	protected static int VALIDATION_TIMEOUT = 2;

	protected String dbUrl;
	protected Connection connection;
	protected Properties connectionProperties;

	public DBStorage(String dbUrl, String driverClass, Properties connectionProperties) throws ClassNotFoundException {
		this.dbUrl = dbUrl;
		this.connectionProperties = connectionProperties;
		Class.forName(driverClass);
	}

	public void connectDB() throws SQLException {
		logger.info("Trying to connect with: " + this.dbUrl);
		connection = DriverManager.getConnection(this.dbUrl, connectionProperties);
		logger.info("Connected to " + this.dbUrl);
	}

	protected void ensureConnection() throws SQLException {
		if (!connection.isValid(VALIDATION_TIMEOUT)) {
			connectDB();
		}
	}

	public void close() throws SQLException {
		logger.info("closing db connections");
		connection.close();
	}

	protected PreparedStatement initStatement(String command) throws SQLException {
		ensureConnection();
		return connection.prepareCall(command);
	}

	protected void cleanup(Statement sql, ResultSet result) throws SQLException {
		if (sql != null) {
			sql.close();
		}
		if (result != null) {
			result.close();
		}
	}
}
