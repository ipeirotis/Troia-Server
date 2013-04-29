package com.datascience.core.storages;

import com.datascience.core.jobs.IJobStorage;
import com.datascience.core.jobs.Job;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.AbstractScheduledService;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author konrad
 */
public class CachedWithRegularDumpJobStorage extends CachedJobStorage{
	
	protected DumpingService dumpingService;
	
	public CachedWithRegularDumpJobStorage(IJobStorage cachedJobStorage, int cacheSize,
			long period, TimeUnit unit){
		super(cachedJobStorage, cacheSize);
		dumpingService = new DumpingService(period, unit);
		startService();
	}
	
	private void startService(){
		dumpingService.startAndWait();
	}
	
	@Override
	public void stop() throws Exception {
		dumpingService.stopAndWait();
		dumpingService.executor.shutdown();
		dumpingService.executor.awaitTermination(1, TimeUnit.MINUTES);
		super.stop();
	}
	
	@Override
	public String toString() {
		return "CachedWithRegularDump" + wrappedJobStorage.toString();
	}
	
	public class DumpingService extends AbstractScheduledService {
		
		protected Scheduler scheduler;
		public ScheduledExecutorService executor;
		
		public DumpingService(long delay, TimeUnit unit) {
			scheduler = AbstractScheduledService.Scheduler.newFixedDelaySchedule(delay, delay, unit);
		}

		@Override
		protected void runOneIteration() throws Exception {
			for (Optional<Job> oJob: cache.asMap().values()) {
				wrappedJobStorage.add(oJob.get());
			}
		}

		@Override
		protected Scheduler scheduler() {
			return scheduler;
		}

		@Override
		protected ScheduledExecutorService executor(){
			executor = super.executor();
			return executor;
		}
	}
}
