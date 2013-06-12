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
	protected boolean utf8;
	protected String dbUrl;
	protected String dbName;
	protected Connection connection;
	protected Properties connectionProperties;
	protected String extraOptions;

	public DBBackend(Properties connectionProperties, Properties properties, boolean utf8) throws ClassNotFoundException, SQLException {
		this(
				properties.getProperty(Constants.DB_URL),
				properties.getProperty(Constants.DB_DRIVER_CLASS),
				connectionProperties,
				properties.getProperty(Constants.DB_NAME),
				utf8 ? "?useUnicode=true&characterEncoding=utf-8" : "",
				utf8);
	}

	public DBBackend(String dbUrl, String driverClass, Properties connectionProperties, String dbName, String extraOptions, boolean utf8) throws ClassNotFoundException, SQLException {
		this.dbUrl = dbUrl;
		this.dbName = dbName;
		this.connectionProperties = connectionProperties;
		this.extraOptions = extraOptions;
		this.utf8 = utf8;
		Class.forName(driverClass);
		connectDatabase();
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

	public void rebuild(Map<String, String> tables) throws SQLException {
		connectDatabase();
		for (Map.Entry<String, String> e : tables.entrySet()){
			createTable(e.getKey(), e.getValue());
			createIndex(e.getKey());
		}
		close();
	}

	public void clear(Collection<String> tables) throws SQLException {
		connectDatabase();
		for (String t : tables){
			dropTable(t);
		}
		close();
	}

	public PreparedStatement initStatement(String command) throws SQLException {
		ensureConnection();
		return connection.prepareCall(command);
	}

	public void cleanupStatement(Statement sql, ResultSet result) throws SQLException {
		if (sql != null) {
			sql.close();
		}
		if (result != null) {
			result.close();
		}
	}

	public Connection getConnection(){
		return connection;
	}

	public void checkTables(Collection
									<String> tables) throws  Exception{
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
//		if (tables.size() != i)
//			throw new Exception("Invalid tables size");
		cleanupStatement(stmt, null);
	}

	public void createTable(String tableName, String tableColumns) throws SQLException {
		String createTable = "CREATE TABLE " + tableName + " (" + tableColumns + ") ";
		createTable += utf8 ? "DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;" : ";";
		executeSQL(createTable);
		logger.info("Table " + tableName + " successfully created");
	}

	public void dropTable(String tableName) throws  SQLException {
		executeSQL("DROP TABLE " + tableName + ";");
		logger.info("Table " + tableName + " successfully droped");
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

	protected void useDatabase() throws  SQLException{
		executeSQL("USE " + dbName + ";");
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

	protected String getInsertPrefix(){
		return "REPLACE INTO";
	}

	public String insertReplacingSQL(String table, String params){
		int paramsNumber = params.split(",").length;
		return String.format("%s %s %s VALUES (%s);", getInsertPrefix(), table, params,
				"?" + Strings.repeat(",?", paramsNumber - 1));
	}
}
