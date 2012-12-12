package com.datascience.gal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

import com.datascience.gal.core.DataCostEstimator;

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
		assertEquals(ds.getNumberOfObjects(), 4);
		String j2 = ds.toString();
		assertNotSame(j1, j2);
		
		//check objects quality for each category. it should be 1./categories_size
		for (String obj : unassignedObjects) {
			assertEquals(1./categories.size(), DataCostEstimator.getInstance().estimateMissclassificationCost(ds, null, obj), 1e-10);
		}
	}
}
