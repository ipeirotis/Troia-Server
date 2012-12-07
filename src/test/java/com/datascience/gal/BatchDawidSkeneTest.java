package com.datascience.gal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.datascience.gal.quality.ClassificationCostEvaluator;
import com.datascience.gal.quality.DS_MaxLikelihoodCostEvaluator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

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
		
		//add 2 unassigned objects
		ArrayList<String> unassignedObjects = new ArrayList<String>();
		unassignedObjects.add("additional_object1");
		unassignedObjects.add("additional_object2");
		unassignedObjects.add("additional_object3");
		ds.addObjects(unassignedObjects);
		assertEquals(ds.getNumberOfObjects(), 4);
		
		//check objects quality for each category. it should be 1./categories_size
		ClassificationCostEvaluator evaluator = new DS_MaxLikelihoodCostEvaluator();
		Map<String,Datum> objects = ds.getObjects();
		Collection<String> objectNames = objects.keySet();
		//first compute
		for (String obj : objectNames) {
			for (Category c : categories) {
				ds.computeProjectQuality(evaluator, c.getName(), obj);
			}
		}

		Map<String,Map<String,Double>> qualities = ds.getQualities();
		for (String obj : unassignedObjects) {
			for (Category c : categories) {
				assertEquals(1./categories.size(), (double)qualities.get(obj).get(c.getName()), 1e-10);
			}
		}
	}
}
