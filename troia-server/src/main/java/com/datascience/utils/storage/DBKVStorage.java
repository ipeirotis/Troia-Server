package com.datascience.utils.storage;

import com.datascience.serialization.ISerializer;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.Properties;

/**
 * @Author: konrad
 */
public class DBKVStorage<V> extends DBStorage implements IKVStorage<V> {

	private static Logger logger = Logger.getLogger(DBKVStorage.class);
	protected static int VALIDATION_TIMEOUT = 2;

	private static final String GET = "SELECT value FROM (?) WHERE key = (?);";
	private static final String INSERT = "REPLACE INTO (?) (key, value) VALUES (?, ?);";
	private static final String DELETE = "DELETE FROM (?) WHERE key = (?);";

	protected Connection connection;
	protected Properties connectionProperties;
	protected String dbUrl;
	protected String table;
	protected ISerializer serializer;
	protected Type valueType;

	public DBKVStorage(String dbUrl, String table, Properties connectionProperties,
					   String driverClass, ISerializer serializer, Type valueType) throws ClassNotFoundException {
		super(dbUrl, driverClass, connectionProperties);
		this.table = table;
		this.serializer = serializer;
		this.valueType = valueType;
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
	public void put(String key, V value) throws SQLException{
		String logmsg = "DBKV put " + key;
		logger.debug(logmsg);
		PreparedStatement sql = null;
		try {
			sql = initStatement(INSERT);
			sql.setString(1, table);
			sql.setString(2, key);
			sql.setString(3, serializer.serialize(value));
			sql.executeUpdate();
			logger.debug(logmsg + " DONE");
		} catch (Exception ex) {
			logger.error(logmsg + " FAIL", ex);
		} finally {
			cleanup(sql, null);
		}
	}

	@Override
	public V get(String key) throws SQLException{
		String logmsg = "DBKV get " + key;
		logger.debug(logmsg);
		PreparedStatement sql = null;
		ResultSet result = null;
		V value = null;
		try {
			sql = initStatement(GET);
			sql.setString(1, table);
			sql.setString(2, key);
			result = sql.executeQuery();
			if (!result.next()) {
				logger.debug(logmsg + " DONE no results");
				return value;
			}
			String sValue = result.getString("value");
			value = serializer.parse(sValue, valueType);
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
