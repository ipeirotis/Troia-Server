package com.datascience.serialization.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Random;

import com.datascience.core.base.ContValue;
import com.datascience.datastoring.datamodels.memory.InMemoryData;
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
	public void dataContValueJsonTest() {
		InMemoryData<ContValue> data = new InMemoryData<ContValue>();
		ArrayList<ContValue> labels = new ArrayList<ContValue>();
		for (int i = 0; i < 2; i++) {
			labels.add(new ContValue(1.0 * i, 2.0 * i));
		}
		ArrayList<LObject<ContValue>> lObjects = new ArrayList<LObject<ContValue>>();
		for (int i = 0; i < 2; i++) {
			LObject<ContValue> lObject = new LObject<ContValue>("object" + i);
			lObject.setGoldLabel(labels.get(i));
			lObjects.add(lObject);
			data.addObject(lObject);
		}
		for (int i = 0; i < 4; i++) {
			Worker worker = new Worker("worker" + i);
			for (LObject<ContValue> lObject : lObjects) {
				AssignedLabel<ContValue> assign = new AssignedLabel<ContValue>(worker, lObject, labels.get(random.nextInt(labels.size())));
				data.addAssign(assign);
			}
			data.addWorker(worker);
		}
		String json = gson.toJson(data);
		InMemoryData deserialized = gson.fromJson(json, new TypeToken<InMemoryData<ContValue>>(){}.getType());
		
		assertTrue(deserialized.getAssigns().containsAll(data.getAssigns()));
		assertTrue(deserialized.getObjects().containsAll(data.getObjects()));
		assertTrue(deserialized.getWorkers().containsAll(data.getWorkers()));
		assertTrue(deserialized.getGoldObjects().containsAll(data.getGoldObjects()));
	}
}
