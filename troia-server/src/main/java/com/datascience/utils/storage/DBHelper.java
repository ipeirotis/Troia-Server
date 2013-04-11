package com.datascience.utils.storage;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Properties;

/**
 * User: artur
 * Date: 4/10/13
 */
public class DBHelper extends DBStorage {

	private static Logger logger = Logger.getLogger(DBHelper.class);
	protected static int VALIDATION_TIMEOUT = 2;

	protected String dbUrl;
	protected String dbName;
	protected Connection connection;
	protected Properties connectionProperties;

	public DBHelper(String dbUrl, String driverClass, Properties connectionProperties, String dbName) throws ClassNotFoundException {
		super(dbUrl, driverClass, connectionProperties);
		this.dbName = dbName;
		Class.forName(driverClass);
	}

	public void execute() throws SQLException {
		connectDB();
		dropDatabase();
		createDatabase();
		for (String tableName : new String[] {"ObjectAssigns", "WorkerAssigns", "Objects", "Workers"}){
			createTable(tableName);
			createIndex(tableName);
		}
		close();
	}

	protected void createTable(String tableName) throws SQLException{
		executeSQL("CREATE TABLE " + tableName + " (id VARCHAR(100) NOT NULL PRIMARY KEY, value LONGTEXT) DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;");
		logger.info("Table " + tableName + " successfully created");
	}

	protected void createIndex(String tableName) throws  SQLException{
		executeSQL("CREATE INDEX " + tableName + "Index on " + tableName + " (id);");
		logger.info("Index on table " + tableName + " successfully created");
	}

	protected void dropDatabase() throws SQLException {
		logger.info("Deleting database " + dbName);
		executeSQL("DROP DATABASE " + dbName + ";");
		logger.info("Database deleted successfully");
	}

	protected void createDatabase() throws SQLException{
		logger.info("Creating database");
		executeSQL("CREATE DATABASE "+ dbName + " CHARACTER SET utf8 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT COLLATE utf8_general_ci;");
		executeSQL("USE " + dbName + ";");
		logger.info("Database created successfully");
	}

	private void executeSQL(String sql) throws SQLException {
		PreparedStatement stmt = initStatement(sql);
		stmt.executeUpdate();
		cleanup(stmt, null);
	}
}