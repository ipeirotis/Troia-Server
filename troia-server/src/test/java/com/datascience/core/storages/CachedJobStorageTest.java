package com.datascience.core.storages;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

/**
 *
 * @author konrad
 */
public class CachedJobStorageTest extends ProxyLikeJobStorageTest{

	public CachedJobStorageTest(){
		timeout = 100;
	}
	
	@Override
	public IJobStorage getProxy(IJobStorage jobStorage){
		return new CachedJobStorage(jobStorage, 2);
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
