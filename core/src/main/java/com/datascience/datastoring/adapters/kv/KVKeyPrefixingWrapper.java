package com.datascience.datastoring.adapters.kv;

/**
 * @Author: konrad
 */
public class KVKeyPrefixingWrapper<V> implements IKVStorage<V> {

	protected IKVStorage<V> wrapped;
	protected String prefix;

	public KVKeyPrefixingWrapper(IKVStorage<V> wrapped, String prefix){
		this.wrapped = wrapped;
		this.prefix = prefix;
	}

	protected String k(String key){
		return prefix + key;
	}

	@Override
	public void put(String key, V value) throws Exception {
		wrapped.put(k(key), value);
	}

	@Override
	public V get(String key) throws Exception {
		return wrapped.get(k(key));
	}

	@Override
	public void remove(String key) throws Exception {
		wrapped.remove(k(key));
	}

	@Override
	public boolean contains(String key) throws Exception {
		return wrapped.contains(k(key));
	}

	@Override
	public void shutdown() throws Exception {
		wrapped.shutdown();
	}
}
