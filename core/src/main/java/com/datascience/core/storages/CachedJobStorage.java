package com.datascience.core.storages;

import java.util.concurrent.ExecutionException;

import com.datascience.core.base.Project;
import com.datascience.core.jobs.IJobStorage;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.apache.log4j.Logger;

import com.datascience.core.jobs.Job;
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
public class CachedJobStorage extends WrappedJobStorage{
	
	private static Logger logger = Logger.getLogger(CachedJobStorage.class);

	protected LoadingCache<String, Optional<Job>> cache;
	
	@SuppressWarnings("OverridableMethodCallInConstructor")
	public CachedJobStorage(final IJobStorage cachedJobStorage, int cacheSize){
		super(cachedJobStorage);
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
				Job job = wrappedJobStorage.get(id);
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
					wrappedJobStorage.add(rn.getValue().get());
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
		} catch (UncheckedExecutionException ex) {
			logger.fatal("CachedJobStorage", ex.getCause());
			throw ex;
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
		wrappedJobStorage.remove(job);
	}

	@Override
	public void test() throws Exception {
		wrappedJobStorage.test();
	}

	@Override
	public void stop() throws Exception {
		cache.invalidateAll();
		cache.cleanUp();
		wrappedJobStorage.stop();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		stop();
	}
	
	@Override
	public String toString() {
		return "Cached" + wrappedJobStorage.toString();
	}
	
	static protected class NotInCachedException extends Exception{
	}
}
