package com.datascience.core.storages;

import com.datascience.core.jobs.Job;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author konrad
 */
public abstract class ProxyLikeJobStorageTest {
	
	IJobStorage proxy;
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
	
	public abstract IJobStorage getProxy(IJobStorage jobStorage);
	
	@Before
	public void setUp() throws Exception{
		storage = new MemoryJobStorage();
		proxy = getProxy(storage);
		for (int i=0;i<3;i++) {
			storage.add(jobs[i]);
		}
	}

	
	@Test
	public void testGet() throws Exception {
		for (int i=0;i<3;i++) {
			assertIn(proxy, i);
		}
		for (int i=3; i<jobs.length;i++) {
			assertNotIn(proxy, i);
		}
		storage.add(jobs[4]);
		assertIn(proxy, 4);
	}
	
	@Test
	public void testAdd() throws Exception {
		assertNotIn(proxy, 4);
		proxy.add(jobs[4]);
		Thread.sleep(timeout);
		assertIn(proxy, 4);
	}	

	@Test
	public void testRemove() throws Exception {
		Job job = proxy.get("2");
		proxy.remove(job);
		Thread.sleep(timeout);
		assertNotIn(storage, 2);
		assertNotIn(proxy, 2);
		
	}

	@Test
	public void testStop() throws Exception {
		proxy.stop();
		assertEmpty(storage, 0, jobs.length);
	}
}