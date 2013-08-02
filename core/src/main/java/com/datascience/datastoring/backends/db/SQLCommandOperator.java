package com.datascience.datastoring.backends.db;

import com.google.common.base.Strings;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: konrad
 */
public class SQLCommandOperator {

	protected Connection connection;
	protected String insertReplacingSQL;
	protected boolean utf8Modification;

	protected SQLCommandOperator(Connection connection, String insertReplacingSQL, boolean utf8Modification) {
		this.connection = connection;
		this.insertReplacingSQL = insertReplacingSQL;
		this.utf8Modification = utf8Modification;
	}

	public void close() throws SQLException{
		connection.close();
	}

	public void test() throws Exception {
		if (!connection.isValid(2))
			throw new Exception("DBBackend: connection not valid");
	}

	protected void executeSQL(String sql) throws SQLException {
		PreparedStatement stmt = initStatement(sql);
		stmt.executeUpdate();
		cleanupStatement(stmt, null);
	}

	protected void cleanupStatement(Statement sql, ResultSet result) throws SQLException {
		if (sql != null) {
			sql.close();
		}
		if (result != null) {
			result.close();
		}
	}

	public void cleanup(Statement sql, ResultSet result) throws SQLException {
		cleanupStatement(sql, result);
		close();
	}

	public PreparedStatement initStatement(String command) throws SQLException {
		return connection.prepareCall(command);
	}

	protected void setDB(String dbName) throws SQLException {
		executeSQL("USE " + dbName + ";");
	}

	public void createTable(String tableName, String tableColumns) throws SQLException {
		String createTable = "CREATE TABLE " + tableName + " (" + tableColumns + ") ";
		createTable += utf8Modification ? "DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;" : ";";
		executeSQL(createTable);
	}

	public void dropTable(String tableName) throws  SQLException {
		executeSQL("DROP TABLE " + tableName + ";");
	}

	protected void createIndex(String tableName) throws  SQLException{
		executeSQL("CREATE INDEX " + tableName + "Index on " + tableName + " (id);");
	}

	public void createDatabase(String dbName) throws SQLException{
		String createDatabase = "CREATE DATABASE " + dbName;
		createDatabase += utf8Modification ? " CHARACTER SET utf8 DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT COLLATE utf8_general_ci;" : " ;";
		executeSQL(createDatabase);
	}

	public Set<String> getAllTables(String dbName) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA = '"+ dbName+"';");
			Set<String> tableNamesSet = new HashSet<String>();
			while(rs.next()){
				tableNamesSet.add(rs.getString("TABLE_NAME").toLowerCase());
			}
			return tableNamesSet;
		} finally {
			cleanupStatement(stmt, rs);
		}
	}

	public PreparedStatement getPreparedInsertReplacing(String table, String params) throws SQLException {
		int paramsNumber = params.split(",").length;
		String sql = String.format("%s %s %s VALUES (%s);", insertReplacingSQL, table, params,
				"?" + Strings.repeat(",?", paramsNumber - 1));
		return initStatement(sql);
	}

}
