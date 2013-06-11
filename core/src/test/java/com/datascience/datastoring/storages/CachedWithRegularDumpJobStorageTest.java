package com.datascience.datastoring.storages;

import com.datascience.datastoring.datamodels.full.MemoryJobStorage;
import com.datascience.datastoring.jobs.IJobStorage;
import com.datascience.datastoring.jobs.Job;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 *
 * @author konrad
 */
public class CachedWithRegularDumpJobStorageTest extends CachedJobStorageTest {
	
	protected IJobStorage internalStorage;
	
	public CachedWithRegularDumpJobStorageTest(){
		timeout = 100;
	}
	
	@Override
	public IJobStorage getProxy(IJobStorage jobStorage){
		internalStorage = jobStorage;
		return new CachedWithRegularDumpJobStorage(jobStorage, 2, 10, TimeUnit.SECONDS);
	}
	
	@Test
	public void lazyDumpingTest() throws Exception{
		IJobStorage iStorage = new MemoryJobStorage();
		IJobStorage eagerStorage = new CachedWithRegularDumpJobStorage(iStorage, 10, 1, TimeUnit.DAYS);
		Job job = jobs[4];
		eagerStorage.add(job);
		Thread.sleep(200);
		assertNotIn(iStorage, 4);
		assertIn(eagerStorage, 4);
	}
	
	@Test
	public void noLazyDumpingTest() throws Exception{
		IJobStorage iStorage = new MemoryJobStorage();
		IJobStorage eagerStorage = new CachedWithRegularDumpJobStorage(iStorage, 10, 100, TimeUnit.NANOSECONDS);
		Job job = jobs[4];
		eagerStorage.add(job);
		Thread.sleep(200);
		assertIn(iStorage, 4);
		assertIn(eagerStorage, 4);
	}
}
