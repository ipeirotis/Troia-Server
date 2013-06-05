package com.datascience.datastoring.backends.db;

import com.datascience.datastoring.Constants;
import com.datascience.datastoring.IBackend;
import com.google.common.base.Strings;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.*;

/**
 * User: artur
 * Date: 6/4/13
 */
public class DBBackend implements IBackend {

	protected final static Logger logger = Logger.getLogger(DBBackend.class);
	protected final static int VALIDATION_TIMEOUT = 2;
	protected String dbUrl;
	protected String dbName;
	protected Connection connection;
	protected Properties connectionProperties;
	protected String extraOptions;

	public DBBackend(Properties connectionProperties, Properties properties) throws ClassNotFoundException {
		this(
				properties.getProperty(Constants.DB_URL),
				properties.getProperty(Constants.DB_DRIVER_CLASS),
				connectionProperties,
				properties.getProperty(Constants.DB_NAME),
				"?useUnicode=true&characterEncoding=utf-8");
	}

	public DBBackend(String dbUrl, String driverClass, Properties connectionProperties, String dbName, String extraOptions) throws ClassNotFoundException {
		this.dbUrl = dbUrl;
		this.dbName = dbName;
		this.connectionProperties = connectionProperties;
		this.extraOptions = extraOptions;
		Class.forName(driverClass);
	}

	@Override
	public void test() throws Exception {
		connectDatabase();
		ensureConnection();
		if (!connection.isValid(VALIDATION_TIMEOUT))
			throw new Exception("DBBackend: connection not valid");
	}

	@Override
	public void stop() throws Exception {
		close();
	}

	public void rebuild(Map<String, String> tables) throws  Exception {
		connectDatabase();
		if (dbName != null) {
			try {
				dropDatabase();
			}
			catch (SQLException ex){
				logger.warn("Can't drop database: " + dbName);
			}
			try {
				createDatabase();
			}
			catch (SQLException ex){
				logger.warn("Can't create database: " + dbName);
			}
		} else {
			dropAllObjects();
		}
		for (Map.Entry<String, String> e : tables.entrySet()){
			createTable(e.getKey(), e.getValue());
			createIndex(e.getKey());
		}
		close();
	}

	public void clear() throws SQLException {
		connectDatabase();
		if (dbName != null) {
			try {
				dropDatabase();
			}
			catch (SQLException ex){
				logger.warn("Can't drop database: " + dbName);
			}
		} else {
			dropAllObjects();
		}
		close();
	}

	public Connection getConnection(){
		return connection;
	}

	public void checkTables(List<String> tables) throws  Exception{
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA = '"+ dbName+"';");
		Set<String> tableNamesSet = new HashSet<String>();
		int i=0;
		while(rs.next()){
			tableNamesSet.add(rs.getString("TABLE_NAME"));
			i++;
		}
		for (String tableName : tables){
			if (!tableNamesSet.contains(tableName)){
				throw new Exception("There is no table named: " + tableName);
			}
		}
		if (tables.size() != i)
			throw new Exception("Invalid tables size");
		cleanupStatement(stmt, null);
	}

	protected void createTable(String tableName, String tableColumns) throws SQLException {
		executeSQL("CREATE TABLE " + tableName + " (" + tableColumns + ") DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;");
		logger.info("Table " + tableName + " successfully created");
	}

	protected void createIndex(String tableName) throws  SQLException{
		executeSQL("CREATE INDEX " + tableName + "Index on " + tableName + " (id);");
		logger.info("Index on table " + tableName + " successfully created");
	}

	protected void connectDatabase() throws SQLException {
		String dbPath = String.format("%s%s", dbUrl, extraOptions);
		logger.info("Trying to connect with: " + dbPath);
		connection = DriverManager.getConnection(dbPath, connectionProperties);
		logger.info("Connected to " + dbPath);
		try{
			useDatabase();
		}
		catch (SQLException ex){
			logger.warn("Can't use database: " + dbName);
		}
	}

	protected void dropDatabase() throws SQLException {
		logger.info("Deleting database " + dbName);
		executeSQL("DROP DATABASE " + dbName + ";");
		logger.info("Database deleted successfully");
	}

	protected void createDatabase() throws SQLException{
		logger.info("Creating database");
		executeSQL("CREATE DATABASE " + dbName + " CHARACTER SET utf8 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT COLLATE utf8_general_ci;");
		useDatabase();
		logger.info("Database created successfully");
	}

	protected void useDatabase() throws  SQLException{
		executeSQL("USE " + dbName + ";");
	}

	protected void dropAllObjects() throws SQLException {
		logger.info("Droping objects " + dbName);
		executeSQL("DROP ALL OBJECTS;");
		logger.info("Droping objects - done");
	}

	public void ensureConnection() throws SQLException {
		if (!connection.isValid(VALIDATION_TIMEOUT)) {
			connectDatabase();
		}
	}

	protected void close() throws SQLException {
		connection.close();
	}

	protected void executeSQL(String sql) throws SQLException {
		PreparedStatement stmt = initStatement(sql);
		stmt.executeUpdate();
		cleanupStatement(stmt, null);
	}

	protected PreparedStatement initStatement(String command) throws SQLException {
		ensureConnection();
		return connection.prepareCall(command);
	}

	protected void cleanupStatement(Statement sql, ResultSet result) throws SQLException {
		if (sql != null) {
			sql.close();
		}
		if (result != null) {
			result.close();
		}
	}

	protected String getInsertPrefix(){
		return "REPLACE INTO";
	}

	public String insertReplacingSQL(String table, String params){
		int paramsNumber = params.split(",").length;
		return String.format("%s %s %s VALUES (%s);", getInsertPrefix(), table, params,
				"?" + Strings.repeat(",?", paramsNumber - 1));
	}
}
