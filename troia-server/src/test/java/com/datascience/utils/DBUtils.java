package com.datascience.utils;

import com.google.common.base.Strings;

import java.sql.SQLException;
import java.util.Properties;

/**
 * @Author: konrad
 */
public class DBUtils {

	public Properties getConnectionProperties(){
		Properties connectionProps = new Properties();
		return connectionProps;
	}

	public String getURL(){
		return "jdbc:h2:mem:MODE=MYSQL";
	}

	public String getDriverClass(){
		return "org.h2.Driver";
	}

	public String getDBName(){
		return null;
	}

	public DBHelper getDBHelper() throws ClassNotFoundException {
		return new H2DBHelper(getURL(), getDriverClass(),
				getConnectionProperties(), getDBName());
	}

	protected static class H2DBHelper extends DBHelper{

		public H2DBHelper(String dbUrl, String driverClass, Properties connectionProperties, String dbName) throws ClassNotFoundException {
			super(dbUrl, driverClass, connectionProperties, dbName, "");
		}

		@Override
		protected void createTable(String tableName) throws SQLException {
			executeSQL("CREATE TABLE " + tableName + " (" +
					"id VARCHAR(100) NOT NULL PRIMARY KEY, " +
					"kind VARCHAR(25) NOT NULL, " +
					"data LONGTEXT NOT NULL, " +
					"results LONGTEXT, " +
					"initializationData LONGTEXT, " +
					"model LONGTEXT, " +
					"last_use TIMESTAMP);");
			logger.info("Table " + tableName + " successfully created");
		}

		protected String getInsertPrefix(){
			return "MERGE INTO";
		}
	}
}
