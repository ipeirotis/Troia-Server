package com.datascience.core.storages;

import com.datascience.core.Job;
import com.datascience.core.base.*;
import com.datascience.galc.ContinuousProject;
import com.datascience.service.GSONSerializer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @Author: konrad
 */
public class DBJobStorageTests {

	protected void fillContData(Data<ContValue> data){
		Worker<ContValue> worker1 = new Worker<ContValue>("worker1");
		Worker<ContValue> worker2 = new Worker<ContValue>("worker2");
		LObject<ContValue> object1 = new LObject<ContValue>("object1");
		LObject<ContValue> object2 = new LObject<ContValue>("object2");
		LObject<ContValue> object3 = new LObject<ContValue>("object3");
		object2.setGoldLabel(new Label<ContValue>(new ContValue(0.42, 0.1)));
		data.addGoldObject(object2);
		data.addObject(object3);
		data.addObject(object3); // just for test ...

		AssignedLabel<ContValue> assign1 = new AssignedLabel<ContValue>(worker1, object1,
				new Label<ContValue>(new ContValue(1.)));
		AssignedLabel<ContValue> assign2 = new AssignedLabel<ContValue>(worker2, object2,
				new Label<ContValue>(new ContValue(0.5)));

		data.addAssign(assign1);
		data.addAssign(assign2);
	}

	@Test
	public void testContJob() throws Exception {
		IJobStorage jobStorage = new DBJobStorage("root", "root", "dawid", "localhost", new GSONSerializer());
		jobStorage.test();
		ContinuousProject cp = new ContinuousProject();
		fillContData(cp.getData());
		Job<ContinuousProject> job = new Job<ContinuousProject>(cp, "TEST_J");
		try {
			jobStorage.remove(job);
		} catch (Exception ex) {}
		jobStorage.add(job);

		Job<ContinuousProject> cp_db = jobStorage.get("TEST_J");
		assertTrue(cp_db != null);

	}

}
