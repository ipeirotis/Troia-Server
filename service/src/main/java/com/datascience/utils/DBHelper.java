package com.datascience.utils;

import com.datascience.utils.storage.DBStorage;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

/**
 * User: artur
 * Date: 4/10/13
 */
public class DBHelper extends DBStorage {

	{
		TABLES = Arrays.asList(new String[] {"Projects"});
	}

	public DBHelper(Properties connectionProperties, Properties properties) throws ClassNotFoundException {
		super(connectionProperties, properties);
	}

	public DBHelper(String dbUrl, String driverClass, Properties connectionProperties, String dbName, String extraOptions) throws ClassNotFoundException {
		super(dbUrl, driverClass, connectionProperties, dbName, extraOptions);
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
				"last_use TIMESTAMP) DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;");
		logger.info("Table " + tableName + " successfully created");
	}
}
