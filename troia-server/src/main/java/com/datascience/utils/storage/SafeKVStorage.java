package com.datascience.utils.storage;

import org.apache.log4j.Logger;

/**
 * @Author: konrad
 */
public class SafeKVStorage<V> {

	protected Logger logger;
	protected IKVStorage<V> wrapped;
	protected String prefix;

	public SafeKVStorage(IKVStorage<V> wrapped, Logger logger, String prefix){
		this.wrapped = wrapped;
		this.logger = logger;
		this.prefix = prefix + " ";
	}

	protected void log(String cmd, Exception ex){
		logger.error(prefix + cmd, ex);
	}

	public void put(String key, V value){
		try {
			wrapped.put(key, value);
		} catch (Exception ex) {
			log("put", ex);
		}
	}

	public V get(String key){
		try {
			return wrapped.get(key);
		} catch (Exception ex) {
			log("get", ex);
			return null;
		}
	}

	public void remove(String key){
		try {
			wrapped.remove(key);
		} catch (Exception ex) {
			log("remove", ex);
		}
	}

	public boolean contains(String key){
		try {
			return wrapped.contains(key);
		} catch (Exception ex) {
			log("contains", ex);
			return false;
		}
	}

	public void shutdown(){
		try {
			wrapped.shutdown();
		} catch (Exception ex) {
			log("shutdown", ex);
		}
	}

}
