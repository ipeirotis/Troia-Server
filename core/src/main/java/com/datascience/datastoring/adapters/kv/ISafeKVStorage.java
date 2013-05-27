package com.datascience.datastoring.adapters.kv;

/**
 * @Author: konrad
 */
public interface ISafeKVStorage<V> {

	void put(String key, V value);
	V get(String key);
	void remove(String key);
	boolean contains(String key);
	void shutdown();
}
