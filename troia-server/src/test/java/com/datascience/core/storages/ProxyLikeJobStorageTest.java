package com.datascience.core.storages;

import com.datascience.core.Job;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author konrad
 */
public abstract class ProxyLikeJobStorageTest {
	
	IJobStorage cache;
	IJobStorage storage;
	Job[] jobs = new Job[]{
		new Job(null, "0"),
		new Job(null, "1"),
		new Job(null, "2"),
		new Job(null, "3"),
		new Job(null, "4"),
	};
	
	long timeout;
	
	public void assertIn(IJobStorage storage, int i) throws Exception{
		assertEquals(jobs[i], storage.get("" + i));
	}
	
	public void assertNotIn(IJobStorage storage, int i) throws Exception{
		assertNull("Should raise exception for missing element " + i + " in cache",
			storage.get("" + i));
	}
	
	void assertEmpty(IJobStorage storage, int from, int too) throws Exception{
		for (int i=from;i<too;i++) {
			assertNotIn(storage, i);
		}
	}
	
	void assertInOut(IJobStorage storage, int in_to_excluding) throws Exception{
		for (int i=0;i<in_to_excluding; i++) {
			assertIn(storage, i);
		}
		assertEmpty(storage, in_to_excluding, jobs.length);
	}
	
	public IJobStorage getProxy(IJobStorage jobStorage){
		return new CachedJobStorage(jobStorage, 2);
	}
	
	@Before
	public void setUp() throws Exception{
		storage = new MemoryJobStorage();
		cache = getProxy(storage);
		for (int i=0;i<3;i++) {
			storage.add(jobs[i]);
		}
	}

	
	@Test
	public void testGet() throws Exception {
		for (int i=0;i<3;i++) {
			assertIn(cache, i);
		}
		for (int i=3; i<jobs.length;i++) {
			assertNotIn(cache, i);
		}
		storage.add(jobs[4]);
		assertIn(cache, 4);
	}
	
	@Test
	public void testAdd() throws Exception {
		assertNotIn(cache, 4);
		cache.add(jobs[4]);
		Thread.sleep(timeout);
		assertIn(cache, 4);
	}	

	@Test
	public void testRemove() throws Exception {
		Job job = cache.get("2");
		cache.remove(job);
		Thread.sleep(timeout);
		assertNotIn(storage, 2);
		assertNotIn(cache, 2);
		
	}

	@Test
	public void testStop() throws Exception {
		cache.stop();
		assertEmpty(storage, 0, jobs.length);
	}
}