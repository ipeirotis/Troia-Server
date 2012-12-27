package com.datascience.core.storages;

import com.datascience.core.Job;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author konrad
 */
public class CachedJobStorageTest {
	
	IJobStorage cache;
	IJobStorage storage;
	Job[] jobs = new Job[]{
		new Job(null, "0"),
		new Job(null, "1"),
		new Job(null, "2"),
		new Job(null, "3"),
		new Job(null, "4"),
	};
	
	public void assertIn(IJobStorage storage, int i) throws Exception{
		assertEquals(jobs[i], storage.get("" + i));
	}
	
	public void assertNotIn(IJobStorage storage, int i){
		try {
			storage.get("" + i);
			fail("Should raise exception for missing element " + i + " in cache");
		} catch (Exception ex){
		}
	}
	
	void assertEmpty(IJobStorage storage, int from, int too){
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
	
	@Before
	public void setUp() throws Exception{
		storage = new MemoryJobStorage();
		cache = new CachedJobStorage(storage, 2);
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
		assertIn(cache, 4);
	}	

	@Test
	public void testRemove() throws Exception {
		cache.get("2");
		cache.remove("2");
		Thread.sleep(100);
		assertNotIn(storage, 2);
		assertNotIn(cache, 2);
		
	}

	@Test
	public void testStop() throws Exception {
		cache.stop();
		assertEmpty(storage, 0, jobs.length);
	}

	
	@Test
	public void testLogic() throws Exception {
		cache.stop();
		assertEmpty(storage, 0, jobs.length);
		assertEmpty(cache, 0, jobs.length);
		cache.add(jobs[0]);
		assertEmpty(storage, 0, jobs.length);
		cache.add(jobs[1]);
		assertEmpty(storage, 0, jobs.length);
		cache.add(jobs[2]);
		assertInOut(storage, 1);
		cache.add(jobs[3]);
		assertInOut(storage, 2);

		assertIn(cache, 0);
		assertInOut(storage, 3);
		Set<Integer> in = new HashSet<Integer>();
		in.add(0);
		in.add(1);
		in.add(2);
		for (int i=0;i<jobs.length;i++) {
			if (in.contains(i)) {
				assertIn(storage, i);
			} else {
				assertNotIn(storage, i);
			}
		}
	}
}
