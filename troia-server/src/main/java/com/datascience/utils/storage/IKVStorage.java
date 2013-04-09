package com.datascience.utils.storage;

import java.util.Collection;

/**
 * @Author: konrad
 */
public interface IKVStorage<V> {

	void put(String key, V value) throws Exception;
	V get(String key) throws Exception;
	void remove(String key) throws Exception;
	boolean contains(String key) throws Exception;
	void shutdown() throws Exception;
	Collection<V> prefixedWith(String keyPrefix) throws Exception;
}
