package com.datascience.scheduler;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.nominal.decision.ILabelProbabilityDistributionCostCalculator;
import com.datascience.core.nominal.decision.LabelProbabilityDistributionCostCalculators;
import com.datascience.datastoring.storages.MemoryJobStorage;
import com.datascience.mv.IncrementalMV;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * @Author: konrad
 */
public class CostBasedPriorityCalculatorTest {

	public static NominalProject setUpNominalProject(){
		IncrementalMV imv = new IncrementalMV();
		MemoryJobStorage js = new MemoryJobStorage();
		Collection<String> categories = Arrays.asList(
				new String[]{"A","B","C"});
		NominalProject project = new NominalProject(imv, js.getNominalData("testId"), js.getNominalResults("testId", categories));
		project.initializeCategories(categories, null, null);
		project.getData().addNewUpdatableAlgorithm(imv);
		return project;
	}

	protected Worker<String> worker(int i){
		return new Worker<String>("Worker" + i);
	}

	protected AssignedLabel<String> assign(int i, LObject<String> obj, String label){
		return new AssignedLabel<String>(worker(i), obj, label);
	}

	@Test
	public void testCorrectness(){
		String costMethod = "MAXLIKELIHOOD";
		NominalProject project = setUpNominalProject();
		ILabelProbabilityDistributionCostCalculator lpdcc =
				LabelProbabilityDistributionCostCalculators.get(costMethod);
		IPriorityCalculator<String> pc = new CostBasedPriorityCalculator(lpdcc);
		Scheduler<String> scheduler = new Scheduler<String>(project, pc);
		Worker<String> worker = new Worker<String>("Worker1");
		INominalData data = project.getData();
		LObject<String> object1 = data.getOrCreateObject("object1");
		LObject<String> object2 = data.getOrCreateObject("object2");
		int w = 0;
		data.addAssign(assign(w++, object1, "A"));
		data.addAssign(assign(w++, object1, "B"));
		data.addAssign(assign(w++, object2, "A"));
		scheduler.update();
		LObject<String> object = scheduler.nextObject();
		assertEquals(object1, object);
		object = scheduler.nextObject();
		assertEquals(object1, object);
		scheduler.update();
		object = scheduler.nextObject();
		assertEquals(object1, object);

		data.addAssign(assign(w++, object2, "B"));
		data.addAssign(assign(w++, object2, "C"));
		scheduler.update();
		assertEquals(object2, scheduler.nextObject());

		data.addAssign(assign(w++, object2, "C"));
		data.addAssign(assign(w++, object2, "C"));
		scheduler.update();
		assertEquals(object1, scheduler.nextObject());
	}
}
