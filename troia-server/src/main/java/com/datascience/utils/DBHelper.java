package com.datascience.utils;

import com.datascience.utils.storage.DBStorage;

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

	public DBHelper(String dbUrl, String driverClass, Properties connectionProperties, String dbName) throws ClassNotFoundException {
		super(dbUrl, driverClass, connectionProperties, dbName);
	}
}
