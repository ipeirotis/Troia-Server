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

	{
		TABLES = Arrays.asList(new String[] {
				"ObjectAssigns", "WorkerAssigns", "Objects", "GoldObjects", "EvaluationObjects", "Workers",
				"ObjectResults", "WorkerResults", "JobSettings", "JobTypes"});
	}

	public DBKVHelper(String dbUrl, String driverClass, Properties connectionProperties, String dbName) throws ClassNotFoundException {
		super(dbUrl, driverClass, connectionProperties, dbName);
	}

	public DBKVStorage getKV(String table){
		checkArgument(TABLES.contains(table), "Taking DBKV for not existing table " + table);
		return new DBKVStorage(table, this);
	}
}
