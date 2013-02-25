package com.datascience.galc.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 *
 * @author Michal Borysiak
 */
public class GenericSerializationTest {

	private Gson gson;
	private Random random = new Random();
	private Type workerType = new TypeToken<Worker>(){}.getType();
	private Type dataType = new TypeToken<Data>(){}.getType();

	public GenericSerializationTest() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(workerType, new GenericWorkerDeserializer());
		builder.registerTypeAdapter(workerType, new GenericWorkerSerializer());
		builder.registerTypeAdapter(dataType, new GenericDataSerializer());
		gson = builder.create();
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
		System.out.println(label + " = " + deserialized);
		assertEquals(label, deserialized);
	}

	@Test
	public void labelDoubleJsonTest() {
		Double label = 12.0;
		String json = gson.toJson(label);
		Double deserialized = gson.fromJson(json, new TypeToken<Double>(){}.getType());
		System.out.println(label + " = " + deserialized);
		assertEquals(label, deserialized);
	}

	@Test
	public void dataDoubleJsonTest() {
		Data<Double> data = new Data<Double>();
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
				worker.addAssign(assign);
				data.addAssign(assign);
			}
			data.addWorker(worker);
		}
		String json = gson.toJson(data);
		System.out.println(json);
		Data deserialized = gson.fromJson(json, new TypeToken<Data>(){}.getType());
		
		assertTrue(deserialized.getAssigns().containsAll(data.getAssigns()));
		assertTrue(deserialized.getObjects().containsAll(data.getObjects()));
		assertTrue(deserialized.getWorkers().containsAll(data.getWorkers()));
		assertTrue(deserialized.getGoldObjects().containsAll(data.getGoldObjects()));
	}

	@Test
	public void assignedLabelStringJsonTest() {
		String label = "label";
		Worker<String> worker = new Worker<String>("Worker");
		AssignedLabel<String> assignedLabel = new AssignedLabel<String>(worker, null, label);
		String json = gson.toJson(assignedLabel);
		AssignedLabel<String> deserialized = gson.fromJson(json, new TypeToken<AssignedLabel<String>>(){}.getType());
		System.out.println(assignedLabel + " = " + deserialized);
		assertEquals(assignedLabel, deserialized);
	}

	@Test
	public void assignedLabelStringCollectionJsonTest() {
		Collection<AssignedLabel<String>> assignedLabels = new ArrayList<AssignedLabel<String>>();
		for (int i = 0; i < 10; i++) {
			assignedLabels.add(new AssignedLabel<String>(new Worker<String>("worker" + i), null, "label" + i));
		}
		String json = gson.toJson(assignedLabels);
		Collection<AssignedLabel<String>> deserialized = gson.fromJson(json, new TypeToken<Collection<AssignedLabel<String>>>(){}.getType());
		System.out.println(deserialized);
		assertEquals(assignedLabels, deserialized);
	}
}
