package com.datascience.core.base;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @Author: konrad
 */
public class InMemoryDataTest {

	/**
	 * Testing whether Data properly overrides assigns
	 */
	@Test
	public void testAddAssigns(){
		InMemoryData<String> data = new InMemoryData<String>();
		Worker<String> w1 = data.getOrCreateWorker("Worker1");
		LObject<String> object = data.getOrCreateObject("Object1");
		String label1 = "label1";
		AssignedLabel<String> al1 = new AssignedLabel<String>(w1, object, label1);
		data.addAssign(al1);
		assertEquals(label1, data.getWorkerAssigns(w1).iterator().next().getLabel());
		assertEquals(label1, data.getAssigns().iterator().next().getLabel());
		String label2 = "label2";
		AssignedLabel<String> al2 = new AssignedLabel<String>(w1, object, label2);
		data.addAssign(al2);
		assertEquals(label2, data.getWorkerAssigns(w1).iterator().next().getLabel());
		assertEquals(label2, data.getAssigns().iterator().next().getLabel());
		assertEquals(1, data.getWorkerAssigns(w1).size());
	}

	/**
	 * Testing whether Data properly overrides golds
	 */
	@Test
	public void testAddGold(){
		InMemoryData<String> data = new InMemoryData<String>();
		LObject<String> object1 = data.getOrCreateObject("Object1");
		LObject<String> object2 = data.getOrCreateObject("Object1");
		String label = "goooooldLabel";
		object2.setGoldLabel(label);
		data.addObject(object1);
		data.addObject(object2);
		assertEquals(1, data.getObjects().size());
		assertEquals(label, data.getObject("Object1").getGoldLabel());
	}

	/**
	 * Testing whether Data properly overrides evaluation data
	 */
	@Test
	public void testAddEvaluation(){
		InMemoryData<String> data = new InMemoryData<String>();
		LObject<String> object1 = data.getOrCreateObject("Object1");
		LObject<String> object2 = data.getOrCreateObject("Object1");
		String label = "goooooldLabel";
		object2.setEvaluationLabel(label);
		data.addObject(object1);
		data.addObject(object2);
		assertEquals(1, data.getObjects().size());
		assertEquals(label, data.getObject("Object1").getEvaluationLabel());
	}
}
