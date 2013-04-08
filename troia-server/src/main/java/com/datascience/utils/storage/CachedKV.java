package com.datascience.utils.storage;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * @Author: konrad
 */
public class CachedKV<K, V> implements IKVStorage<K, V>{

	protected final IKVStorage<K, V> wrapped;
	protected Cache<K, V> cache;

	public CachedKV(IKVStorage<K, V> wrapped, long cacheSize){
		this.wrapped = wrapped;
		cache = CacheBuilder.newBuilder()
				.maximumSize(cacheSize)
				.removalListener(new RemovalListener<K, V>() {
					@Override
					public void onRemoval(RemovalNotification<K, V> objectObjectRemovalNotification) {
						CachedKV.wrapped.put(objectObjectRemovalNotification.getKey(), objectObjectRemovalNotification.getValue());
					}
				})
				.build();
	}

	@Override
	public void put(K key, V value) {
		cache.put(key, value);
	}

	@Override
	public V get(K key) {
		return cache.get(key);
	}

	@Override
	public void remove(K key) {
		cache.invalidate(key);
	}

	@Override
	public boolean contains(K key) {
		return cache.get(key) != null;
	}

	@Override
	public void shutdown() {
		cache.invalidateAll();
	}
}
