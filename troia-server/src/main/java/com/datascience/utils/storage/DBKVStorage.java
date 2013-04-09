package com.datascience.utils.storage;

import com.datascience.serialization.ISerializer;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.Properties;

/**
 * @Author: konrad
 */
public class DBKVStorage<K, V> implements IKVStorage<K, V> {

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
		this.table = table;
		this.serializer = serializer;
		this.valueType = valueType;
		Class.forName(driverClass);
		this.connectionProperties = connectionProperties;
	}

	protected void connectDB() throws SQLException {
		logger.info("Trying to connect with: " + this.dbUrl);
		connection = DriverManager.getConnection(this.dbUrl,
				connectionProperties);
		logger.info("Connected to " + this.dbUrl);
	}

	protected void ensureConnection() throws SQLException {
		if (!connection.isValid(VALIDATION_TIMEOUT)) {
			connectDB();
		}
	}

	public void close() throws SQLException {
		logger.info("closing db connections");
		connection.close();
	}

	protected PreparedStatement initStatement(String command) throws SQLException {
		ensureConnection();
		return connection.prepareCall(command);
	}

	protected void cleanup(PreparedStatement sql, ResultSet result) throws SQLException {
		if (sql != null) {
			sql.close();
		}
		if (result != null) {
			result.close();
		}
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
	public void put(K key, V value) throws SQLException{
		String logmsg = "DBKV put " + key.toString();
		logger.debug(logmsg);
		PreparedStatement sql = null;
		try {
			sql = initStatement(INSERT);
			sql.setString(1, table);
			sql.setString(2, serializer.serialize(key));
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
	public V get(K key) throws SQLException{
		String logmsg = "DBKV get " + key.toString();
		logger.debug(logmsg);
		PreparedStatement sql = null;
		ResultSet result = null;
		V value = null;
		try {
			sql = initStatement(GET);
			sql.setString(1, table);
			sql.setString(2, serializer.serialize(key));
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
	public void remove(K key) throws SQLException{
		String logmsg = "DBKV remove " + key.toString();
		logger.debug(logmsg);
		PreparedStatement sql = null;
		try {
			sql = initStatement(DELETE);
			sql.setString(1, table);
			sql.setString(2, serializer.serialize(key));
			sql.executeUpdate();
			logger.debug(logmsg + " DONE");
		} catch (Exception ex) {
			logger.error(logmsg + " FAIL", ex);
		} finally {
			cleanup(sql, null);
		}
	}

	@Override
	public boolean contains(K key) throws SQLException{
		return get(key) != null;
	}

	@Override
	public void shutdown() throws SQLException{
		close();
	}
}
