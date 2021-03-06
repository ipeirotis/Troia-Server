package com.datascience.datastoring.adapters.mixed;

import com.datascience.datastoring.IBackend;
import com.datascience.datastoring.adapters.kv.*;
import com.datascience.datastoring.backends.db.DBBackend;
import com.datascience.datastoring.datamodels.kv.*;

import java.sql.SQLException;
import java.util.*;

/**
 * @Author: konrad
 */
public class DBKVsFactory<T> implements IBackendKVFactory<T> {

	protected DBBackend dbBackend;

	public DBKVsFactory(DBBackend dbBackend) {
		this.dbBackend = dbBackend;
	}

	@Override
	public IKVStorage<T> getKV(String table) {
		try {
			dbBackend.createTable(table, "id VARCHAR(200) NOT NULL PRIMARY KEY, value LONGTEXT");
		} catch (SQLException e) {}
		return new DBKVStorage(table, dbBackend);
	}

	@Override
	public void remove(String table) throws Exception {
		dbBackend.dropTable(table);
	}

	@Override
	public void rebuild() throws Exception {
		try{
			dbBackend.createDatabase();
		} catch (SQLException ex){}
	}

	@Override
	public void test(List<String> kvs) throws Exception {
		dbBackend.checkTables(kvs);
	}

	@Override
	public IBackend getBackend() {
		return dbBackend;
	}

	@Override
	public String getID() {
		return "DB";
	}
}
