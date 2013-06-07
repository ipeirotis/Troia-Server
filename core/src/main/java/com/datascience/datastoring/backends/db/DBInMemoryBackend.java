package com.datascience.datastoring.backends.db;

import java.sql.SQLException;
import java.util.*;

/**
 * User: artur
 * Date: 6/4/13
 */
public class DBInMemoryBackend extends DBBackend {

	public DBInMemoryBackend() throws ClassNotFoundException, SQLException {
		super(getURL(), getDriverClass(), getConnectionProperties(), getDBName(), "", false);
	}

	public static Properties getConnectionProperties(){
		return new Properties();
	}

	public static String getURL(){
		return "jdbc:h2:mem:" + getDBName() + ";DB_CLOSE_DELAY=-1";
	}

	public static String getDriverClass(){
		return "org.h2.Driver";
	}

	public static String getDBName(){
		return "test";
	}

	@Override
	protected String getInsertPrefix(){
		return "MERGE INTO";
	}

	@Override
	public void test(){
	}
}
