package com.datascience.datastoring.storages;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

/**
 *
 * @author konrad
 */
public abstract class CachedJobStorageBaseTest extends ProxyLikeJobStorageTest{

	public CachedJobStorageBaseTest(){
		timeout = 100;
	}

	@Test
	public void testLogic() throws Exception {
		proxy.stop();
		assertEmpty(storage, 0, jobs.length);
		assertEmpty(proxy, 0, jobs.length);
		proxy.add(jobs[0]);
		assertEmpty(storage, 0, jobs.length);
		proxy.add(jobs[1]);
		assertEmpty(storage, 0, jobs.length);
		proxy.add(jobs[2]);
		assertInOut(storage, 1);
		proxy.add(jobs[3]);
		assertInOut(storage, 2);

		assertIn(proxy, 0);
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
