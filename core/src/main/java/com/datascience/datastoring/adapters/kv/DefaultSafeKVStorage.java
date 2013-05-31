package com.datascience.datastoring.adapters.kv;

import com.google.common.base.Throwables;
import org.apache.log4j.Logger;

/**
 * @Author: konrad
 */
public class DefaultSafeKVStorage<V> implements ISafeKVStorage<V>{

	protected Logger logger;
	protected IKVStorage<V> wrapped;
	protected String prefix;

	public DefaultSafeKVStorage(IKVStorage<V> wrapped, Logger logger, String prefix){
		this.wrapped = wrapped;
		this.logger = logger;
		this.prefix = prefix + " ";
	}

	public DefaultSafeKVStorage(IKVStorage<V> wrapped, String prefix){
		this(wrapped, Logger.getLogger(wrapped.getClass()), prefix);
	}

	protected void log(String cmd, Exception ex){
		logger.error(prefix + cmd, ex);
		Throwables.propagate(ex);
	}

	@Override
	public void put(String key, V value){
		try {
			wrapped.put(key, value);
		} catch (Exception ex) {
			log("put", ex);
		}
	}

	@Override
	public V get(String key){
		try {
			return wrapped.get(key);
		} catch (Exception ex) {
			log("get", ex);
			return null;
		}
	}

	@Override
	public void remove(String key){
		try {
			wrapped.remove(key);
		} catch (Exception ex) {
			log("remove", ex);
		}
	}

	@Override
	public boolean contains(String key){
		try {
			return wrapped.contains(key);
		} catch (Exception ex) {
			log("contains", ex);
			return false;
		}
	}

	@Override
	public void shutdown(){
		try {
			wrapped.shutdown();
		} catch (Exception ex) {
			log("shutdown", ex);
		}
	}

}
