package com.datascience.core.storages;

import java.util.concurrent.ExecutionException;

import com.datascience.core.base.Project;
import org.apache.log4j.Logger;

import com.datascience.core.Job;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 *
 * @author konrad
 */
public class CachedJobStorage implements IJobStorage {
	
	private static Logger logger = Logger.getLogger(CachedJobStorage.class);

	protected IJobStorage cachedJobStorage;
	protected LoadingCache<String, Optional<Job>> cache; 
	
	@SuppressWarnings("OverridableMethodCallInConstructor")
	public CachedJobStorage(final IJobStorage cachedJobStorage, int cacheSize){
		this.cachedJobStorage = cachedJobStorage;
		cache = CacheBuilder.newBuilder()
			.maximumSize(cacheSize)
			.removalListener(getRemovalListener())
			.build(getLoader());
	}
	
	protected CacheLoader<String, Optional<Job>> getLoader() {
		return new CacheLoader<String, Optional<Job>>(){
			@Override
			public Optional<Job> load(String id) throws Exception {
				// This is tricky - if absent we throw 
				// exception to avoid putting this value into cache
				Job job = cachedJobStorage.get(id);
				if (job == null) {
					throw new NotInCachedException();
				}
				return Optional.of(job);
			}
		};
	}
	
	protected RemovalListener<String, Optional<Job>> getRemovalListener(){
		return new RemovalListener<String, Optional<Job>>() {
			@Override
			public void onRemoval(RemovalNotification<String, Optional<Job>> rn){
				try {
					cachedJobStorage.add(rn.getValue().get());
				} catch (Exception ex) {
					logger.error("CachedJobStorage on eviction", ex);
				}
			}
		};
	}
	
	@Override
	public <T extends Project> Job<T> get(String id) throws Exception {
		logger.debug("CACHED_JS: get " + id);
		try {
			return cache.get(id).get();
		} catch (ExecutionException ex){
			if (ex.getCause() instanceof NotInCachedException) {
				return null;
			} else {
				throw ex;
			}
		}
	}

	/**
	 * @param job
	 * @throws Exception 
	 */
	@Override
	public void add(Job job) throws Exception {
		logger.debug("CACHED_JS: add " + job.getId());
		cache.put(job.getId(), Optional.of(job));
	}

	/**
	 * TODO: think whether this should be synchronized
	 * @param id
	 * @throws Exception 
	 */
	@Override
	public void remove(Job job) throws Exception {
		logger.debug("CACHED_JS: rm " + job.getId());
		String id = job.getId();
		cache.invalidate(id);
		cachedJobStorage.remove(job);
	}

	@Override
	public void test() throws Exception {
		cachedJobStorage.test();
	}

	@Override
	public void stop() throws Exception {
		cache.invalidateAll();
		cache.cleanUp();
		cachedJobStorage.stop();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		stop();
	}
	
	@Override
	public String toString() {
		return "Cached" + cachedJobStorage.toString();
	}
	
	static protected class NotInCachedException extends Exception{
	}
}
