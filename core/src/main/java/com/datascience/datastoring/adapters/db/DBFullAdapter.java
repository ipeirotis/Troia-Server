package com.datascience.datastoring.adapters.db;

import com.datascience.datastoring.IBackend;
import com.datascience.datastoring.IBackendAdapter;
import com.datascience.datastoring.backends.db.DBBackend;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: artur
 * Date: 6/4/13
 */
public class DBFullAdapter implements IBackendAdapter {

	static Logger logger = Logger.getLogger(DBFullAdapter.class);
	static Map<String, String> tables;
	static {
		tables = new HashMap<String, String>();
		tables.put("Projects", "id VARCHAR(100) NOT NULL PRIMARY KEY, " +
				"kind VARCHAR(25) NOT NULL, " +
				"data LONGTEXT NOT NULL, " +
				"results LONGTEXT, " +
				"initializationData LONGTEXT, " +
				"model LONGTEXT, " +
				"last_use TIMESTAMP");
	}
	static final String GET_DS = "SELECT kind, data, results, initializationData, model FROM Projects WHERE id IN (?);";
	static final String INSERT_DS_PARAMS = "(id, kind, data, results, initializationData, model)";
	static final String DELETE_DS = "DELETE FROM Projects WHERE id = (?);";

	DBBackend backend;

	public DBFullAdapter(DBBackend backend){
		this.backend = backend;
	}

	@Override
	public IBackend getBackend() {
		return backend;
	}

	@Override
	public void clear() throws SQLException {
		backend.clear(tables.keySet());
	}

	@Override
	public void rebuild() throws Exception {
		backend.rebuild(tables);
	}

	@Override
	public String getID() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void test() throws Exception {
		backend.test();
		backend.checkTables(tables.keySet());
	}

	public String[] get(String id) throws SQLException {
		logger.debug("Getting job from DB: " + id);
		ResultSet dsResults = null;
		backend.ensureConnection();
		PreparedStatement dsStatement = null;
		try {
			dsStatement = backend.getConnection().prepareStatement(GET_DS);
			dsStatement.setString(1, id);
			dsResults = dsStatement.executeQuery();
			if (!dsResults.next()) {
				return null;
			}
			String data = dsResults.getString("data");
			String kind = dsResults.getString("kind");
			String results = dsResults.getString("results");
			String initializationData = dsResults.getString("initializationData");
			String model = dsResults.getString("model");
			dsStatement.close();
			logger.debug("Getting job from DB: " + id + " DONE");
			return new String[]{kind, initializationData, data, results, model};
		} finally {
			backend.cleanupStatement(dsStatement, dsResults);
		}
	}

	public void add(String[] data) throws SQLException{
		logger.debug("Adding job to DB: " + data[0]);
		backend.ensureConnection();
		PreparedStatement dsStatement = null;
		try {
			dsStatement = backend.getConnection().prepareStatement(backend.insertReplacingSQL("Projects", INSERT_DS_PARAMS));
			int i = 1;
			for (String s : data){
				dsStatement.setString(i++, s);
			}
			dsStatement.executeUpdate();
			logger.debug("Adding job to DB: " + data[0] + " DONE");
		} finally {
			backend.cleanupStatement(dsStatement, null);
		}
	}

	public void remove(String id) throws Exception {
		logger.debug("Removing job from DB: " + id);
		backend.ensureConnection();
		PreparedStatement dsStatement = null;
		try {
			dsStatement = backend.getConnection().prepareStatement(DELETE_DS);
			dsStatement.setString(1, id);
			dsStatement.executeUpdate();
			dsStatement.close();
			logger.debug("Removing job from DB: " + id + " DONE");
		} finally {
			backend.cleanupStatement(dsStatement, null);
		}
	}
}
