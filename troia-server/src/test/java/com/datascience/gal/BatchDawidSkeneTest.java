package com.datascience.gal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.Category;
import com.datascience.core.base.LObject;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.base.Worker;
import org.junit.Before;
import org.junit.Test;

import com.datascience.core.nominal.decision.DecisionEngine;
import com.datascience.core.nominal.decision.LabelProbabilityDistributionCalculators;
import com.datascience.core.nominal.decision.LabelProbabilityDistributionCostCalculators;

public class BatchDawidSkeneTest {

	NominalProject project;


	@Before
	public void setUp(){
		ArrayList<Category> categories = new ArrayList<Category>();
		Category category1 = new Category("category1");
		Category category2 = new Category("category2");
		categories.add(category1);
		categories.add(category2);

		project = new NominalProject(new BatchDawidSkene());
		project.initializeCategories(categories);
	}

	@Test
	public final void testAddLabelWithWrongCategory() {
		Worker<String> w = project.getData().getOrCreateWorker("worker");
		LObject<String> obj = project.getData().getOrCreateObject("object1");
		project.getData().addAssign(new AssignedLabel<String>(w, obj, "category1"));
		try {
			project.getData().addAssign(new AssignedLabel<String>(w, obj, "wrongLabel"));
			fail("Added label with incorrect category.");
		} catch(Exception e) {
		}
		assertEquals(project.getData().getAssigns().size(),1);
	}

	@Test
	public final void testMissclassificationCost() {
		LObject<String> obj = new LObject<String>("object1");
		project.getData().addObject(obj);

		DecisionEngine de = new DecisionEngine(
			new LabelProbabilityDistributionCalculators.DS(),
			LabelProbabilityDistributionCostCalculators.get(""), null);
		try{
			de.estimateMissclassificationCost(project, obj);
			fail("trying to get estimated value for not computed object");
		}
		catch(Exception ex){
		}
		project.getAlgorithm().compute();
		assertEquals(
				1. / project.getData().getCategories().size(),
				de.estimateMissclassificationCost(project, obj),
				1e-10);
		try{
			de.estimateMissclassificationCost(project, new LObject<String>("testObj"));
			fail("trying to get estimated value for non existions object");
		}
		catch(Exception e){
		}
	}
}
