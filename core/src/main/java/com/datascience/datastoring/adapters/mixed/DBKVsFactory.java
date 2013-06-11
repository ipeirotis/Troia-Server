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

	public DBKVsFactory(Properties connectionProperties, Properties properties, boolean utf8) throws SQLException, ClassNotFoundException {
		dbBackend = new DBBackend(connectionProperties, properties, utf8);
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
	public IBackend getBackend() {
		return dbBackend;
	}

	@Override
	public String getID() {
		return "DB";
	}
}
