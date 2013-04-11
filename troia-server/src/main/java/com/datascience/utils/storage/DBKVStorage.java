package com.datascience.utils.storage;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Properties;

/**
 * @Author: konrad
 */
public class DBKVStorage extends DBStorage implements IKVStorage<String> {

	private static Logger logger = Logger.getLogger(DBKVStorage.class);

	private static final String GET = "SELECT value FROM (?) WHERE id = (?);";
	private static final String INSERT = "REPLACE INTO (?) (id, value) VALUES (?, ?);";
	private static final String DELETE = "DELETE FROM (?) WHERE id = (?);";

	protected String table;

	public DBKVStorage(String dbUrl, String table, Properties connectionProperties,
					   String driverClass) throws ClassNotFoundException {
		super(dbUrl, driverClass, connectionProperties);
		this.table = table;
	}

	/**
	 * Added hoping that this will sometime turn out to be useful
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();
	}

	@Override
	public void put(String key, String value) throws SQLException{
		String logmsg = "DBKV put " + key;
		logger.debug(logmsg);
		PreparedStatement sql = null;
		try {
			sql = initStatement(INSERT);
			sql.setString(1, table);
			sql.setString(2, key);
			sql.setString(3, value);
			sql.executeUpdate();
			logger.debug(logmsg + " DONE");
		} catch (Exception ex) {
			logger.error(logmsg + " FAIL", ex);
		} finally {
			cleanup(sql, null);
		}
	}

	@Override
	public String get(String key) throws SQLException{
		String logmsg = "DBKV get " + key;
		logger.debug(logmsg);
		PreparedStatement sql = null;
		ResultSet result = null;
		String value = null;
		try {
			sql = initStatement(GET);
			sql.setString(1, table);
			sql.setString(2, key);
			result = sql.executeQuery();
			if (!result.next()) {
				logger.debug(logmsg + " DONE no results");
				return value;
			}
			value = result.getString("value");
			logger.debug(logmsg + " DONE");
		} catch (Exception ex) {
			logger.error(logmsg + " FAIL", ex);
		} finally {
			cleanup(sql, result);
			return value;
		}
	}

	@Override
	public void remove(String key) throws SQLException{
		String logmsg = "DBKV remove " + key;
		logger.debug(logmsg);
		PreparedStatement sql = null;
		try {
			sql = initStatement(DELETE);
			sql.setString(1, table);
			sql.setString(2, key);
			sql.executeUpdate();
			logger.debug(logmsg + " DONE");
		} catch (Exception ex) {
			logger.error(logmsg + " FAIL", ex);
		} finally {
			cleanup(sql, null);
		}
	}

	@Override
	public boolean contains(String key) throws SQLException{
		// TODO XXX FIXME rewrite this to run without deserialization (count results)
		logger.debug("DBKV contains");
		return get(key) != null;
	}

	@Override
	public void shutdown() throws SQLException{
		close();
	}
}
