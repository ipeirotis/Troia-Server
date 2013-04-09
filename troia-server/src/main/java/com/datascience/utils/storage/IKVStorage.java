package com.datascience.utils.storage;

/**
 * @Author: konrad
 */
public interface IKVStorage<K, V> {

	void put(K key, V value) throws Exception;
	V get(K key) throws Exception;
	void remove(K key) throws Exception;
	boolean contains(K key) throws Exception;
	void shutdown() throws Exception;
}
