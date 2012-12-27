package com.datascience.core.storages;

import com.datascience.core.Job;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.apache.log4j.Logger;

/**
 *
 * @author konrad
 */
public class CachedJobStorage implements IJobStorage {
	
	private static Logger logger = Logger.getLogger(CachedJobStorage.class.getName());

	protected IJobStorage cachedJobStorage;
	protected LoadingCache<String, Job> cache; 
	
	@SuppressWarnings("OverridableMethodCallInConstructor")
	public CachedJobStorage(final IJobStorage cachedJobStorage, int cacheSize){
		this.cachedJobStorage = cachedJobStorage;
		cache = CacheBuilder.newBuilder()
			.maximumSize(cacheSize)
			.removalListener(getRemovalListener())
			.build(getLoader());
	}
	
	protected CacheLoader<String, Job> getLoader() {
		return new CacheLoader<String, Job>(){
			@Override
			public Job load(String id) throws Exception {
				return cachedJobStorage.get(id);
			}
		};
	}
	
	protected RemovalListener<String, Job> getRemovalListener(){
		return new RemovalListener<String, Job>() {
			@Override
			public void onRemoval(RemovalNotification<String, Job> rn){
				try {
					cachedJobStorage.add(rn.getValue());
				} catch (Exception ex) {
					logger.error("CachedJobStorage on eviction", ex);
				}
			}
		};
	}
	
	@Override
	public Job get(String id) throws Exception {
		return cache.get(id);
	}

	/**
	 * @param job
	 * @throws Exception 
	 */
	@Override
	public void add(Job job) throws Exception {
		cache.put(job.getId(), job);
	}

	/**
	 * TODO: think whether this should be synchronized
	 * @param id
	 * @throws Exception 
	 */
	@Override
	public void remove(String id) throws Exception {
		cache.invalidate(id);
		cachedJobStorage.remove(id);
	}

	@Override
	public void test() throws Exception {
		cachedJobStorage.test();
	}

	@Override
	public void stop() throws Exception {
		cache.invalidateAll();
		cachedJobStorage.stop();
	}
}
