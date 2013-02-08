package com.datascience.core.storages;

import com.datascience.core.base.*;
import com.datascience.service.GSONSerializer;
import com.datascience.service.ISerializer;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * @Author: konrad
 */
public class TestCoreBaseSerialization {

	@Test
	public void testOverall(){
		Data<ContValue> data = new Data<ContValue>();
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

		ISerializer serializer = new GSONSerializer();
		String s = serializer.serialize(data);
		System.out.println(s);
		Data<ContValue> d = serializer.parse(s, Data.class);
		String s2 = serializer.serialize(d);
		assertEquals(s, s2);

		assertEquals(2, d.getWorkers().size());
		assertEquals(worker1, d.getWorker(worker1.getName()));
		assertEquals(worker2, d.getWorker(worker2.getName()));

		assertEquals(1, d.getWorker("worker1").getAssigns().size());
		assertTrue(d.getWorker("worker1").getAssigns().contains(assign1));
		assertEquals(1, d.getWorker("worker2").getAssigns().size());
		assertTrue(d.getWorker("worker2").getAssigns().contains(assign2));

		assertEquals(3, d.getObjects().size());
		assertEquals(object1, d.getObject(object1.getName()));
		assertEquals(object2, d.getObject(object2.getName()));
		assertEquals(object3, d.getObject(object3.getName()));

		assertEquals(1, d.getGoldObjects().size());
		assertEquals(object2, d.getGoldObject("object2"));

		assertEquals(0, d.getEvaluationObjects().size());

		assertEquals(2, d.getAssigns().size());
		assertTrue(d.getAssigns().contains(assign1));
		assertTrue(d.getAssigns().contains(assign2));
	}
}
