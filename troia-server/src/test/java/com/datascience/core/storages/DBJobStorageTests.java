package com.datascience.core.storages;

import com.datascience.core.jobs.Job;
import com.datascience.core.base.ContValue;
import com.datascience.galc.ContinuousIpeirotis;
import com.datascience.galc.ContinuousProject;
import com.datascience.service.GSONSerializer;
import org.junit.Test;

/**
 * @Author: konrad
 */
public class DBJobStorageTests {


	@Test
	public void testContJob() throws Exception {
		IJobStorage jobStorage = new DBJobStorage("root", "root", "dawid", "localhost", new GSONSerializer());
		jobStorage.test();
		MemoryJobStorage js = new MemoryJobStorage();
		ContinuousProject cp = new ContinuousProject(new ContinuousIpeirotis(), js.<ContValue>getData("testId"), js.getContResults("testId"));
		ContDataComparator cdc = new ContDataComparator();
		cdc.fillContData(cp.getData());
		Job<ContinuousProject> job = new Job<ContinuousProject>(cp, "TEST_J");
		try {
			jobStorage.remove(job);
		} catch (Exception ex) {}
		jobStorage.add(job);

		Job<ContinuousProject> cp_db = jobStorage.get("TEST_J");
		cdc.assertEqual(cp_db.getProject().getData());
	}

}
