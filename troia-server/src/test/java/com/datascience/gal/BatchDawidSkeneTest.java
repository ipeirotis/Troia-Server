package com.datascience.gal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

import com.datascience.gal.BatchDawidSkene.BatchDawidSkeneDeserializer;
import com.datascience.gal.decision.LabelProbabilityDistributionCalculators;
import com.datascience.gal.decision.LabelProbabilityDistributionCostCalculators;
import com.datascience.gal.decision.Utils;
import com.google.gson.JsonParser;
import java.util.HashSet;
import java.util.Set;

public class BatchDawidSkeneTest {

	@Test
	public final void testAddLabelWithWrongCategory() {
		Category category1 = new Category("category1");
		Category category2 = new Category("category2");
		ArrayList<Category> categories = new ArrayList<Category>();
		categories.add(category1);
		categories.add(category2);
		BatchDawidSkene ds = new BatchDawidSkene("id",categories);
		AssignedLabel correctLabel = new AssignedLabel("worker","object1","category1");
		AssignedLabel incorrectLabel = new AssignedLabel("worker","object2","wrongLabel");
		ds.addAssignedLabel(correctLabel);
		try {
			ds.addAssignedLabel(incorrectLabel);
			fail("Added label with incorrect category.");
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		assertEquals(ds.getNumberOfObjects(),1);
	}
	
	@Test
	public final void testAddObjects() {
		Category category1 = new Category("category1");
		Category category2 = new Category("category2");
		ArrayList<Category> categories = new ArrayList<Category>();
		categories.add(category1);
		categories.add(category2);
		
		BatchDawidSkene ds = new BatchDawidSkene("id",categories);
		ds.addAssignedLabel(new AssignedLabel("worker","object1","category1"));
		assertEquals(ds.getNumberOfObjects(),1);
		String j1 = ds.toString();
		
		//add 3 unassigned objects
		ArrayList<String> unassignedObjects = new ArrayList<String>();
		unassignedObjects.add("additional_object1");
		unassignedObjects.add("additional_object2");
		unassignedObjects.add("additional_object3");
		ds.addObjects(unassignedObjects);
		assertEquals(ds.getNumberOfObjects(), 1);
		assertEquals(ds.getNumberOfUnassignedObjects(), 3);
		String j2 = ds.toString();
		
		//check serialization
		assertNotSame(j1, j2);
		
		//check deserialization
		BatchDawidSkeneDeserializer dsd = new BatchDawidSkeneDeserializer();
		JsonParser jp = new JsonParser();
		BatchDawidSkene dds = (BatchDawidSkene)dsd.deserialize(jp.parse(j2), null, null);
		assertEquals(dds.getNumberOfUnassignedObjects(), 3);
		
		//check objects quality for each category. it should be 1./categories_size
		Set<Category> sCategories = new HashSet<Category>(categories);
		for (String obj : unassignedObjects) {
			assertEquals(1./categories.size(), Utils.estimateMissclassificationCost(ds, 
					new LabelProbabilityDistributionCalculators.DS(), 
					LabelProbabilityDistributionCostCalculators.get(""), new Datum(obj, sCategories)), 1e-10);
		}
		
	}
}
