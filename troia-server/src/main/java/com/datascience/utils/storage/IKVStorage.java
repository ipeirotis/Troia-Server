package com.datascience.utils.storage;

/**
 * @Author: konrad
 */
public interface IKVStorage<K, V> {

	void put(K key, V value);
	V get(K key);
	void remove(K key);
	boolean contains(K key);
	void shutdown();
}
