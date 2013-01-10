package com.datascience.core.storages;

import com.google.common.util.concurrent.AbstractScheduledService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author konrad
 */
public class CachedWithRegularDumpJobStorage extends CachedJobStorage{
	
	protected AbstractScheduledService dumpingService;
	
	public CachedWithRegularDumpJobStorage(final IJobStorage cachedJobStorage, int cacheSize,
			long period, TimeUnit unit){
		super(cachedJobStorage, cacheSize);
		dumpingService = new DumpingService(period, unit);
		start();
	}
	
	private void start(){
		// TODO
	}
	
	@Override
	public void stop() throws Exception {
		dumpingService.stopAndWait();
		super.stop();
	}
	
	@Override
	public String toString() {
		return "CachedWithRegularDump" + cachedJobStorage.toString();
	}
	
	public class DumpingService extends AbstractScheduledService {
		
		protected Scheduler scheduler;
		
		public DumpingService(long delay, TimeUnit unit) {
			scheduler = AbstractScheduledService.Scheduler.newFixedDelaySchedule(delay, delay, unit);
		}

		@Override
		protected void runOneIteration() throws Exception {
			// TODO
		}

		@Override
		protected Scheduler scheduler() {
			return scheduler;
		}
	}
}
