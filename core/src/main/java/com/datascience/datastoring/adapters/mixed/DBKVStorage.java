package com.datascience.datastoring.adapters.mixed;

import com.datascience.datastoring.adapters.kv.IKVStorage;
import com.datascience.datastoring.backends.db.DBBackend;
import org.apache.log4j.Logger;

import java.sql.*;

/**
* @Author: konrad
*/
public class DBKVStorage<T> implements IKVStorage<T> {

	private static Logger logger = Logger.getLogger(DBKVStorage.class);

	private static final String GET = "SELECT value FROM %s WHERE id = ?;";
	private static final String EXISTS = "SELECT COUNT(*) as c FROM %s WHERE id = ?;";
	private static final String INSERT_PARAMS = "(id, value)";
	private static final String DELETE = "DELETE FROM %s WHERE id = ?;";

	protected String table;
	protected DBBackend backend;

	public DBKVStorage(String table, DBBackend backend){
		this.table = table;
		this.backend = backend;
	}

	protected String prepareString(String sql){
		return String.format(sql, table);
	}

	protected PreparedStatement prepareStatement(String sql) throws SQLException {
		return backend.initStatement(prepareString(sql));
	}

	protected String startLog(String method, String key){
		String logmsg = "DBKV (" + table + ") " + method + " " + key;
		logger.debug(logmsg);
		return logmsg;
	}

	protected void handleError(String msg, SQLException ex) throws SQLException {
		logger.error(msg + " FAIL", ex);
		throw ex;
	}

	@Override
	public void put(String key, T value) throws SQLException{
		String logmsg = startLog("put", key);
		PreparedStatement sql = null;
		try {
			sql = backend.initStatement(backend.insertReplacingSQL(table, INSERT_PARAMS));
			sql.setString(1, key);
			sql.setString(2, (String) value); //TODO: casting
			sql.executeUpdate();
			logger.debug(logmsg + " DONE");
		} catch (SQLException ex) {
			handleError(logmsg, ex);
		} finally {
			backend.cleanupStatement(sql, null);
		}
	}

	@Override
	public T get(String key) throws SQLException{
		String logmsg = startLog("get", key);
		PreparedStatement sql = null;
		ResultSet result = null;
		String value = null;
		try {
			sql = prepareStatement(GET);
			sql.setString(1, key);
			result = sql.executeQuery();
			if (!result.next()) {
				logger.debug(logmsg + " DONE no results");
				return (T) value;
			}
			value = result.getString("value");
			logger.debug(logmsg + " DONE");
		} catch (SQLException ex) {
			handleError(logmsg, ex);
		} finally {
			backend.cleanupStatement(sql, result);
		}
		return (T) value;
	}

	@Override
	public void remove(String key) throws SQLException{
		String logmsg = startLog("remove ", key);
		PreparedStatement sql = null;
		try {
			sql = prepareStatement(DELETE);
			sql.setString(1, key);
			sql.executeUpdate();
			logger.debug(logmsg + " DONE");
		} catch (SQLException ex) {
			handleError(logmsg, ex);
		} finally {
			backend.cleanupStatement(sql, null);
		}
	}

	@Override
	public boolean contains(String key) throws SQLException{
		String logmsg = startLog("contains", key);
		PreparedStatement sql = null;
		ResultSet result = null;
		long value = 0;
		try {
			sql = prepareStatement(EXISTS);
			sql.setString(1, key);
			result = sql.executeQuery();
			result.next();
			value = result.getLong("c");
			logger.debug(logmsg + " DONE");
		} catch (SQLException ex) {
			handleError(logmsg, ex);
		} finally {
			backend.cleanupStatement(sql, result);
		}
		return value == 1;
	}

	@Override
	public void shutdown() throws SQLException{
		// TODO XXX FIXME THINK - I think we don't should close DB here
//		dbStorage.close();
	}
}
