package com.datascience.utils.storage;

import com.google.common.cache.*;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @Author: konrad
 */
public class CachedKV<V> implements IKVStorage<V>{

	protected static Logger log = Logger.getLogger(CachedKV.class);

	protected final IKVStorage<V> wrapped;
	protected LoadingCache<String, V> cache;

	public CachedKV(IKVStorage<V> wrapped, CacheBuilder cacheBuilder){
		this.wrapped = wrapped;
		this.cache = cacheBuilder
				.removalListener(getRemovalListener())
				.build(getDataLoader());
	}

	public CachedKV(IKVStorage<V> wrapped, long cacheSize){
		this(wrapped, CacheBuilder.newBuilder().maximumSize(cacheSize));
	}

	protected RemovalListener<String, V> getRemovalListener(){
		return new RemovalListener<String, V>() {
			@Override
			public void onRemoval(RemovalNotification<String, V> objectObjectRemovalNotification) {
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

	protected CacheLoader<String, V> getDataLoader(){
		return new CacheLoader<String, V>() {
			@Override
			public V load(String k) throws Exception {
				V value = wrapped.get(k);
				if (value != null) return wrapped.get(k);
				throw new NotInCachedException();
			}
		};
	}

	@Override
	public void put(String key, V value) {
		cache.put(key, value);
	}

	@Override
	public V get(String key) throws ExecutionException {
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
	public void remove(String key) {
		cache.invalidate(key);
	}

	@Override
	public boolean contains(String key) throws ExecutionException {
		return get(key) != null;
	}

	@Override
	public void shutdown() throws Exception {
		for (Map.Entry<String, V> entry: cache.asMap().entrySet()){
			wrapped.put(entry.getKey(), entry.getValue());
		}
		cache.invalidateAll();
	}

	protected static class NotInCachedException extends Exception{}
}
