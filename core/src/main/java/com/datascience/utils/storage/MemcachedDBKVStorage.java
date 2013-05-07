package com.datascience.utils.storage;

import net.spy.memcached.MemcachedClient;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

/**
 * @Author: artur
 */
public class MemcachedDBKVStorage implements IKVStorage<String> {

	private static Logger logger = Logger.getLogger(MemcachedDBKVStorage.class);
	protected IKVStorage<String> storage;
	protected MemcachedClient client;
	protected String prefix;
	protected int expirationTime;

	public MemcachedDBKVStorage(IKVStorage<String> wrapped, String prefix, Properties properties) throws IOException {
		client = new MemcachedClient(
				new InetSocketAddress(
					properties.getProperty(Constants.MEMCACHE_URL),
					Integer.parseInt(properties.getProperty(Constants.MEMCACHE_PORT))));
		this.prefix = prefix;
		this.expirationTime = Integer.parseInt(properties.getProperty(Constants.MEMCACHE_EXPIRATION_TIME));
		this.storage = wrapped;
	}

	private String prepareKey(String key){
		return prefix + "_" + key;
	}

	@Override
	public void put(String key, String value) throws Exception {
		storage.put(key, value);
		client.set(prepareKey(key), expirationTime, value);
	}

	@Override
	public String get(String key) throws Exception {
		Object memObj = client.get(prepareKey(key));
		if (memObj != null){
			return (String) memObj;
		}
		else {
			String value = storage.get(key);
			if (value != null)
				put(key, value);
			return value;
		}
	}

	@Override
	public void remove(String key) throws Exception {
		client.delete(prepareKey(key));
		storage.remove(key);
	}

	@Override
	public boolean contains(String key) throws Exception {
		return client.get(prepareKey(key)) != null || storage.contains(key);
	}

	@Override
	public void shutdown() throws Exception {
		client.shutdown();
		storage.shutdown();
	}
}
