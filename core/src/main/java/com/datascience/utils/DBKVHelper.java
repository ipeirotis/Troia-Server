package com.datascience.utils;

import com.datascience.utils.storage.*;
import net.spy.memcached.MemcachedClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.Arrays;
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

	private Properties properties;
	protected MemcachedClient memcachedClient;

	public DBKVHelper(Properties connectionProperties, Properties properties) throws ClassNotFoundException {
		super(connectionProperties, properties);
		this.properties = properties;
	}

	protected MemcachedClient getMemcachedClient() throws IOException{
		if (memcachedClient == null) {
			synchronized (this) {
				if (memcachedClient == null) {
					memcachedClient = new MemcachedClient(
							new InetSocketAddress(
									properties.getProperty(Constants.MEMCACHE_URL),
									Integer.parseInt(properties.getProperty(Constants.MEMCACHE_PORT))));
				}
			}
		}
		return memcachedClient;
	}

	public IKVStorage<String> getKV(String table){
		checkArgument(TABLES.contains(table), "Taking DBKV for not existing table " + table);
		try {
			return new MemcachedDBKVStorage(getMemcachedClient(), new DBKVStorage(table, this), table, properties);
		} catch (IOException e) {
			logger.error(e);
		}
		return null;
	}

	@Override
	protected void createTable(String tableName) throws SQLException{
		executeSQL("CREATE TABLE " + tableName + " (id VARCHAR(200) NOT NULL PRIMARY KEY, value LONGTEXT) DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;");
		logger.info("Table " + tableName + " successfully created");
	}

	@Override
	protected void finalize(){
		if (memcachedClient != null) memcachedClient.shutdown();
	}
}
