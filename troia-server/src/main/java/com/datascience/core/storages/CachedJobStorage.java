package com.datascience.core.storages;

import com.datascience.core.Job;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 *
 * @author konrad
 */
public class CachedJobStorage implements IJobStorage {

	protected IJobStorage cachedJobStorage;
	protected LoadingCache<String, Job> cache; 
	
	public CachedJobStorage(final IJobStorage cachedJobStorage, int cacheSize){
		this.cachedJobStorage = cachedJobStorage;
		cache = CacheBuilder.newBuilder()
			.maximumSize(cacheSize)
			.build(new CacheLoader<String, Job>(){
				@Override
				public Job load(String id) throws Exception {
					return cachedJobStorage.get(id);
				}
		});
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
		cachedJobStorage.add(job);
	}

	/**
	 * TODO: think whether this should be synchronized
	 * @param id
	 * @throws Exception 
	 */
	@Override
	public void remove(String id) throws Exception {
		cachedJobStorage.remove(id);
		cache.invalidate(id);
	}

	@Override
	public void test() throws Exception {
		cachedJobStorage.test();
	}

	@Override
	public void stop() throws Exception {
		cachedJobStorage.stop();
	}
}
