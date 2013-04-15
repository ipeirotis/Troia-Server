package com.datascience.utils;

import com.datascience.utils.storage.DBKVStorage;
import com.datascience.utils.storage.DBStorage;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * User: artur
 * Date: 4/10/13
 */
public class DBKVHelper extends DBStorage {

	protected static List<String> TABLES = Arrays.asList(new String[] {
			"ObjectAssigns", "WorkerAssigns", "Objects", "Workers",
			"ObjectResults", "WorkerResults", "JobSettings", "JobTypes"});
	private static Logger logger = Logger.getLogger(DBKVHelper.class);

	public DBKVHelper(String dbUrl, String driverClass, Properties connectionProperties, String dbName) throws ClassNotFoundException {
		super(dbUrl, driverClass, connectionProperties, dbName);
	}

	public void execute() throws SQLException {
		connectDB();
		dropDatabase();
		createDatabase();
		for (String tableName : TABLES){
			createTable(tableName);
			createIndex(tableName);
		}
		close();
	}

	public DBKVStorage getKV(String table){
		checkArgument(TABLES.contains(table), "Taking DBKV for not existing table " + table);
		return new DBKVStorage(table, this);
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
