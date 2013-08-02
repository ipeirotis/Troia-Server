package com.datascience.datastoring.backends.db;

import com.datascience.datastoring.IBackend;
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

	protected String dbName;
	protected DataSource ds;
	protected SQLCommandOperatorFactory sqlCmdFactory;

	public DBBackend(String dbName, PoolProperties poolProperties, SQLCommandOperatorFactory sqlCmdFactory){
		this.dbName = dbName;
		ds = new DataSource(poolProperties);
		this.sqlCmdFactory = sqlCmdFactory;
	}

	public SQLCommandOperator getSQLCmdOperatorNoDB() throws SQLException{
		return sqlCmdFactory.create(ds.getConnection());
	}

	public SQLCommandOperator getSQLCmdOperator() throws SQLException{
		SQLCommandOperator sqlCommandOperator = getSQLCmdOperatorNoDB();
		if (!dbName.equals("")) {
			try {
				sqlCommandOperator.setDB(dbName);
			} catch (SQLException ex) {
				logger.warn("Couldn't set DB: " + dbName, ex);
			}
		}
		return sqlCommandOperator;
	}

	@Override
	public void test() throws Exception {
		SQLCommandOperator sqlCO = null;
		try {
			sqlCO = getSQLCmdOperator();
			sqlCO.test();
		} finally {
			cleanup(sqlCO);
		}
	}

	protected void cleanup(SQLCommandOperator sqlCO) throws SQLException{
		if (sqlCO != null) sqlCO.close();
	}

	@Override
	public void stop() throws Exception {
		close();
	}

	protected void close() throws SQLException {
		ds.close();
	}

	public void dropTable(String tableName) throws  SQLException {
		SQLCommandOperator sqlCO = getSQLCmdOperator();
		try {
			sqlCO.dropTable(tableName);
			logger.info("Table " + tableName + " successfully dropped");
		} finally {
			sqlCO.close();
		}
	}

	public void clear(Collection<String> tables) throws SQLException {
		SQLCommandOperator sqlCO = getSQLCmdOperator();
		try {
			for (String t : tables){
				sqlCO.dropTable(t);
			}
			logger.info("Tables successfully dropped");
		} finally {
			sqlCO.close();
//			close();  I'm not sure about this here ...
		}
	}

	public void rebuild(Map<String, String> tables) throws SQLException {
		try {
			createDatabase();
		} catch (SQLException ex){
			logger.warn("Error when creating db during rebuild", ex);
		}
		SQLCommandOperator sqlCO = getSQLCmdOperator();
		try {
			for (Map.Entry<String, String> e : tables.entrySet()){
				sqlCO.createTable(e.getKey(), e.getValue());
				sqlCO.createIndex(e.getKey());
			}
		} finally {
			sqlCO.close();
//			close(); I'm not sure about this
		}
	}
	public void createDatabase() throws SQLException {
		if (dbName.equals("")) {
			logger.warn("Skipping db creation due to empty dbName");
			return;
		}
		SQLCommandOperator sqlCO = getSQLCmdOperatorNoDB();
		try {
			logger.info("Creating database");
			sqlCO.createDatabase(dbName);
			logger.info("Database created successfully");
		} finally {
			sqlCO.close();
		}
	}

	public void createTable(String tableName, String tableColumns) throws SQLException {
		SQLCommandOperator sqlCO = getSQLCmdOperator();
		try {
			sqlCO.createTable(tableName, tableColumns);
			logger.info("Table " + tableName + " successfully created");
		} finally {
			sqlCO.close();
		}
	}

	/**
	 * NOTE: case-insensitive in table names
	 */
	public void checkTables(Collection<String> tables) throws Exception {
		SQLCommandOperator sqlCO = getSQLCmdOperator();
		try {
			Set<String> existingTables = sqlCO.getAllTables(dbName);
			for (String tableName : tables){
				if (!existingTables.contains(tableName.toLowerCase())){
					throw new Exception("There is no table named: " + tableName);
				}
			}
		} finally {
			sqlCO.close();
		}
	}
}
