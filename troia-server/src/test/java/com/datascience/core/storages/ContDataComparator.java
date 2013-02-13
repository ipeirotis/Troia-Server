package com.datascience.core.storages;

import com.datascience.core.base.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @Author: konrad
 */
public class ContDataComparator {

	protected Data<ContValue> data;
	protected Worker<ContValue> worker1;
	protected Worker<ContValue> worker2;
	protected LObject<ContValue> object1;
	protected LObject<ContValue> object2;
	protected LObject<ContValue> object3;

	protected AssignedLabel<ContValue> assign1;
	protected AssignedLabel<ContValue> assign2;

	public Data<ContValue> getContData(){
		data = new Data<ContValue>();
		fillContData(data);
		return data;
	}

	public void fillContData(Data<ContValue> data){
		worker1 = new Worker<ContValue>("worker1");
		worker2 = new Worker<ContValue>("worker2");
		object1 = new LObject<ContValue>("object1");
		object2 = new LObject<ContValue>("gobject2");
		object3 = new LObject<ContValue>("object3");
		object2.setGoldLabel(new Label<ContValue>(new ContValue(0.42, 0.1)));
		data.addGoldObject(object2);
		data.addObject(object3);
		data.addObject(object3); // just for test ...

		assign1 = new AssignedLabel<ContValue>(worker1, object1,
				new Label<ContValue>(new ContValue(1.)));
		assign2 = new AssignedLabel<ContValue>(worker2, object2,
				new Label<ContValue>(new ContValue(0.5)));

		data.addAssign(assign1);
		data.addAssign(assign2);
	}

	public void assertEqual(Data<ContValue> d){
		assertTrue(data != d);

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
		assertEquals(object2, d.getGoldObject("gobject2"));

		assertEquals(0, d.getEvaluationObjects().size());

		assertEquals(2, d.getAssigns().size());
		assertTrue(d.getAssigns().contains(assign1));
		assertTrue(d.getAssigns().contains(assign2));
	}
}
