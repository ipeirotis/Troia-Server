//package com.datascience.datastoring.adapters.mixed;
//
//import com.datascience.datastoring.Constants;
//import com.datascience.datastoring.adapters.kv.IKVStorage;
//import com.datascience.datastoring.adapters.kv.MemcachedDBKVStorage;
//import com.datascience.datastoring.backends.db.DBStorage;
//import net.spy.memcached.MemcachedClient;
//
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.sql.*;
//import java.util.Arrays;
//import java.util.Properties;
//
//import static com.google.common.base.Preconditions.checkArgument;
//
///**
// * User: artur
// * Date: 4/10/13
// */
//public abstract class DBKVHelper extends DBStorage {
//
//	{
//		TABLES = Arrays.asList(new String[] {
//				"ObjectAssigns", "WorkerAssigns", "Objects", "GoldObjects", "EvaluationObjects", "Workers",
//				"ObjectResults", "WorkerResults", "JobSettings", "JobTypes"});
//	}
//
//	private Properties properties;
//	private boolean memCache;
//	protected MemcachedClient memcachedClient;
//
//	public DBKVHelper(Properties connectionProperties, Properties properties, boolean memCache) throws ClassNotFoundException {
//		super(connectionProperties, properties);
//		this.properties = properties;
//		this.memCache = memCache;
//	}
//
//	protected MemcachedClient getMemcachedClient() throws IOException{
//		if (memcachedClient == null) {
//			synchronized (this) {
//				if (memcachedClient == null) {
//					memcachedClient = new MemcachedClient(
//							new InetSocketAddress(
//									properties.getProperty(Constants.MEMCACHE_URL),
//									Integer.parseInt(properties.getProperty(Constants.MEMCACHE_PORT))));
//				}
//			}
//		}
//		return memcachedClient;
//	}
//
//	public IKVStorage<String> getKV(String table){
//		checkArgument(TABLES.contains(table), "Taking DBKV for not existing table " + table);
//		IKVStorage<String> storage = new DBKVStorage(table, this);
//		try {
//			if (memCache) {
//				return new MemcachedDBKVStorage(getMemcachedClient(), new DBKVStorage(table, this), table, properties);
//			}
//			else {
//				return storage;
//			}
//		} catch (IOException e) {
//			logger.error(e);
//		}
//		return null;
//	}
//
//	@Override
//	protected void createTable(String tableName) throws SQLException{
//		executeSQL("CREATE TABLE " + tableName + " (id VARCHAR(200) NOT NULL PRIMARY KEY, value LONGTEXT) DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;");
//		logger.info("Table " + tableName + " successfully created");
//	}
//
//	@Override
//	public void stop() throws Exception {
//		super.stop();
//		if (memcachedClient != null) memcachedClient.shutdown();
//	}
//}
