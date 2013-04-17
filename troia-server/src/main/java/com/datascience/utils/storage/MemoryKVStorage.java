package com.datascience.utils.storage;

import java.util.HashMap;
import java.util.Map;

/**
 * User: artur
 * Date: 4/10/13
 */
public class MemoryKVStorage<V> implements IKVStorage<V>{

	private Map<String, V> storage;

	public MemoryKVStorage(){
		storage = new HashMap<String, V>();
	}

	public MemoryKVStorage(String key, V val){
		this();
		storage.put(key, val);
	}

	@Override
	public void put(String key, V value) throws Exception {
		storage.put(key, value);
	}

	@Override
	public V get(String key) throws Exception {
		return storage.get(key);
	}

	@Override
	public void remove(String key) throws Exception {
		storage.remove(key);
	}

	@Override
	public boolean contains(String key) throws Exception {
		return storage.containsKey(key);
	}

	@Override
	public void shutdown() throws Exception {
		storage.clear();
	}
}
