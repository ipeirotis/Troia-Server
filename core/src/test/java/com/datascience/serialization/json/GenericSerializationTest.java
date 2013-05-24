package com.datascience.serialization.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Random;

import com.datascience.datastoring.memory.InMemoryData;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 *
 * @author Michal Borysiak
 */
public class GenericSerializationTest {

	private Gson gson;
	private Random random = new Random();

	public GenericSerializationTest() {
		gson = JSONUtils.getFilledDefaultGsonBuilder().create();
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void labelStringJsonTest() {
		String label = "label";
		String json = gson.toJson(label);
		String deserialized = gson.fromJson(json, new TypeToken<String>(){}.getType());
		assertEquals(label, deserialized);
	}

	@Test
	public void labelDoubleJsonTest() {
		Double label = 12.0;
		String json = gson.toJson(label);
		Double deserialized = gson.fromJson(json, new TypeToken<Double>(){}.getType());
		assertEquals(label, deserialized);
	}

	@Test
	public void dataDoubleJsonTest() {
		InMemoryData<Double> data = new InMemoryData<Double>();
		ArrayList<Double> labels = new ArrayList<Double>();
		for (int i = 0; i < 2; i++) {
			labels.add(1.0 * i);
		}
		ArrayList<LObject<Double>> lObjects = new ArrayList<LObject<Double>>();
		for (int i = 0; i < 2; i++) {
			LObject<Double> lObject = new LObject<Double>("object" + i);
			lObject.setGoldLabel(labels.get(i));
			lObjects.add(lObject);
			data.addObject(lObject);
		}
		for (int i = 0; i < 4; i++) {
			Worker<Double> worker = new Worker<Double>("worker" + i);
			for (LObject<Double> lObject : lObjects) {
				AssignedLabel<Double> assign = new AssignedLabel<Double>(worker, lObject, labels.get(random.nextInt(labels.size())));
				data.addAssign(assign);
			}
			data.addWorker(worker);
		}
		String json = gson.toJson(data);
		InMemoryData deserialized = gson.fromJson(json, new TypeToken<InMemoryData>(){}.getType());
		
		assertTrue(deserialized.getAssigns().containsAll(data.getAssigns()));
		assertTrue(deserialized.getObjects().containsAll(data.getObjects()));
		assertTrue(deserialized.getWorkers().containsAll(data.getWorkers()));
		assertTrue(deserialized.getGoldObjects().containsAll(data.getGoldObjects()));
	}
}
