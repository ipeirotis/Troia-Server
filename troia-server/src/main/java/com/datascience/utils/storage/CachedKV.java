package com.datascience.utils.storage;

import com.google.common.cache.*;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @Author: konrad
 */
public class CachedKV<K, V> implements IKVStorage<K, V>{

	protected static Logger log = Logger.getLogger(CachedKV.class);

	protected final IKVStorage<K, V> wrapped;
	protected LoadingCache<K, V> cache;

	public CachedKV(IKVStorage<K, V> wrapped, CacheBuilder cacheBuilder){
		this.wrapped = wrapped;
		this.cache = cacheBuilder
				.removalListener(getRemovalListener())
				.build(getDataLoader());
	}

	public CachedKV(IKVStorage<K, V> wrapped, long cacheSize){
		this(wrapped, CacheBuilder.newBuilder().maximumSize(cacheSize));
	}

	protected RemovalListener<K, V> getRemovalListener(){
		return new RemovalListener<K, V>() {
			@Override
			public void onRemoval(RemovalNotification<K, V> objectObjectRemovalNotification) {
				try{
					if (objectObjectRemovalNotification.getCause() == RemovalCause.EXPLICIT){
						wrapped.remove(objectObjectRemovalNotification.getKey());
					} else {
						wrapped.put(objectObjectRemovalNotification.getKey(), objectObjectRemovalNotification.getValue());
					}
				} catch (Exception ex){
					log.error("CachedKV: onRemoval", ex);
				}
			}
		};
	}

	protected CacheLoader<K, V> getDataLoader(){
		return new CacheLoader<K, V>() {
			@Override
			public V load(K k) throws Exception {
				V value = wrapped.get(k);
				if (value != null) return wrapped.get(k);
				throw new NotInCachedException();
			}
		};
	}

	@Override
	public void put(K key, V value) {
		cache.put(key, value);
	}

	@Override
	public V get(K key) throws ExecutionException {
		try{
			return cache.get(key);
		} catch (ExecutionException ex){
			if (ex.getCause() instanceof NotInCachedException) {
				return null;
			} else {
				throw ex;
			}
		}
	}

	@Override
	public void remove(K key) {
		cache.invalidate(key);
	}

	@Override
	public boolean contains(K key) throws ExecutionException {
		return get(key) != null;
	}

	@Override
	public void shutdown() throws Exception {
		for (Map.Entry<K, V> entry: cache.asMap().entrySet()){
			wrapped.put(entry.getKey(), entry.getValue());
		}
		cache.invalidateAll();
	}

	protected static class NotInCachedException extends Exception{}
}
