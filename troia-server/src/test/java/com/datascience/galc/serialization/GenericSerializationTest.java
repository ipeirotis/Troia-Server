package com.datascience.galc.serialization;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.Label;
import com.datascience.core.base.Worker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Micha? Borysiak
 */
public class GenericSerializationTest {
	
	private Gson gson = new Gson();
	
	public GenericSerializationTest() {
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
	public void assignedLabelStringJsonTest() {
		Label<String> label = new Label<String>("label");
		Worker<String> worker = new Worker<String>("Worker");
		AssignedLabel<String> assignedLabel = new AssignedLabel<String>();
		assignedLabel.setLabel(label);
		assignedLabel.setWorker(worker);
		String json = gson.toJson(assignedLabel);
		AssignedLabel<String> deserialized = gson.fromJson(json, new TypeToken<AssignedLabel<String>>(){}.getType());
		System.out.println(assignedLabel + " = " + deserialized);
		assertEquals(assignedLabel, deserialized);
	}
	
	@Test
	public void assignedLabelStringCollectionJsonTest() {
		Collection<AssignedLabel<String>> assignedLabels = new ArrayList<AssignedLabel<String>>();
		for (int i = 0; i < 10; i++) {
			AssignedLabel<String> assignedLabel = new AssignedLabel<String>();
			assignedLabel.setLabel(new Label<String>("label" + i));
			assignedLabel.setWorker(new Worker<String>("worker" + i));
			assignedLabels.add(assignedLabel);
		}
		String json = gson.toJson(assignedLabels);
		Collection<AssignedLabel<String>> deserialized = gson.fromJson(json, new TypeToken<Collection<AssignedLabel<String>>>(){}.getType());
		System.out.println(deserialized);
		assertEquals(assignedLabels, deserialized);
	}
}
