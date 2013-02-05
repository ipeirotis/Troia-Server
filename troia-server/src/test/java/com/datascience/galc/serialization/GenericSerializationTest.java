package com.datascience.galc.serialization;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Label;
import com.datascience.core.base.Worker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
		Label<String> label = new Label<String>("label");
		String json = gson.toJson(label);
		Label<String> deserialized = gson.fromJson(json, new TypeToken<Label<String>>(){}.getType());
		System.out.println(label + " = " + deserialized);
		assertEquals(label, deserialized);
	}

	@Test
	public void labelDoubleJsonTest() {
		Label<Double> label = new Label<Double>(12.0);
		String json = gson.toJson(label);
		Label<Double> deserialized = gson.fromJson(json, new TypeToken<Label<Double>>(){}.getType());
		System.out.println(label + " = " + deserialized);
		assertEquals(label, deserialized);
	}

	@Test
	public void dataDoubleJsonTest() {
		Data<Double> data = new Data<Double>();
		ArrayList<Label<Double>> labels = new ArrayList<Label<Double>>();
		for (int i = 0; i < 2; i++) {
			labels.add(new Label<Double>(1.0 * i));
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
		Label<String> label = new Label<String>("label");
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
			assignedLabels.add(new AssignedLabel<String>(new Worker<String>("worker" + i), null, new Label<String>("label" + i)));
		}
		String json = gson.toJson(assignedLabels);
		Collection<AssignedLabel<String>> deserialized = gson.fromJson(json, new TypeToken<Collection<AssignedLabel<String>>>(){}.getType());
		System.out.println(deserialized);
		assertEquals(assignedLabels, deserialized);
	}
}
