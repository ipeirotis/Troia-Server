package com.datascience.datastoring.backends.db;

import com.datascience.datastoring.Constants;
import com.datascience.datastoring.IBackend;
import com.google.common.base.Strings;
import org.apache.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.sql.*;
import java.util.*;

/**
 * NOTE: we have to be case insensitive in database/table/fields names!
 *
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

	static public DBBackend createInstance(Properties connectionProperties, Properties properties, boolean utf8) throws ClassNotFoundException, SQLException {
		return new DBBackend(connectionProperties, properties, utf8);
	}

	protected DBBackend(Properties connectionProperties, Properties properties, boolean utf8) throws ClassNotFoundException, SQLException {
		this(
				properties.getProperty(Constants.DB_URL),
				properties.getProperty(Constants.DB_DRIVER_CLASS),
				connectionProperties,
				properties.getProperty(Constants.DB_NAME),
				utf8 ? "?useUnicode=true&characterEncoding=utf-8" : "",
				utf8);
	}

	protected DBBackend(String dbUrl, String driverClass, Properties connectionProperties, String dbName, String extraOptions, boolean utf8) throws ClassNotFoundException, SQLException {
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
		ensureConnection();
		if (!connection.isValid(VALIDATION_TIMEOUT))
			throw new Exception("DBBackend: connection not valid");
	}

	@Override
	public void stop() throws Exception {
		close();
	}

	public void rebuild(Map<String, String> tables) throws SQLException {
		ensureConnection();
		try{
			createDatabase();
		} catch (SQLException ex){
			logger.warn("Error when creating db during rebuild", ex);
		}
		for (Map.Entry<String, String> e : tables.entrySet()){
			createTable(e.getKey(), e.getValue());
			createIndex(e.getKey());
		}
		close();
	}

	public void clear(Collection<String> tables) throws SQLException {
		ensureConnection();
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

	/**
	 * NOTE: case-insensitive in table names
	 */
	public void checkTables(Collection<String> tables) throws Exception{
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA = '"+ dbName+"';");
		Set<String> tableNamesSet = new HashSet<String>();
		while(rs.next()){
			tableNamesSet.add(rs.getString("TABLE_NAME").toLowerCase());
		}
		for (String tableName : tables){
			if (!tableNamesSet.contains(tableName.toLowerCase())){
				throw new Exception("There is no table named: " + tableName);
			}
		}
		cleanupStatement(stmt, null);
	}

	public void createDatabase() throws SQLException{
		logger.info("Creating database");
		String createDatabase = "CREATE DATABASE " + dbName;
		createDatabase += utf8 ? " CHARACTER SET utf8 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT COLLATE utf8_general_ci;" : " ;";
		executeSQL(createDatabase);
		useDatabase();
		logger.info("Database created successfully");
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
			logger.warn("Can't use database: " + dbName, ex);
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

	public static DataSource createConnectionPool(Properties connectionProperties, Properties properties, boolean utf8){
		PoolProperties p = new PoolProperties();
		String dbUlr = properties.getProperty(Constants.DB_URL) + (utf8 ? "?useUnicode=true&characterEncoding=utf-8" : "");
		logger.info("Trying to connect with: " + dbPath);
		connection = DriverManager.getConnection(dbPath, connectionProperties);
		logger.info("Connected to " + dbPath);
		try{
			useDatabase();
		}
		catch (SQLException ex){
			logger.warn("Can't use database: " + dbName, ex);
		}
		p.
		p.setDriverClassName(properties.getProperty(Constants.DB_DRIVER_CLASS));
		p.setUrl(properties.getProperty(Constants.DB_URL));
		p.setUsername();
		p.setPassword();
		p.setInitialSize(10);
		p.setMaxActive(20);

		return new DataSource(p);

				properties.getProperty(Constants.DB_URL),
				properties.getProperty(Constants.DB_DRIVER_CLASS),
				connectionProperties,
				properties.getProperty(Constants.DB_NAME),
				utf8 ? "?useUnicode=true&characterEncoding=utf-8" : "",
				utf8);
	}
}
