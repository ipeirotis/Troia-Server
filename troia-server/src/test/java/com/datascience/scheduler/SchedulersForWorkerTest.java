package com.datascience.scheduler;

import com.datascience.core.nominal.NominalProject;
import com.datascience.core.nominal.decision.ILabelProbabilityDistributionCostCalculator;
import com.datascience.core.nominal.decision.LabelProbabilityDistributionCostCalculators;
import org.junit.Ignore;
import org.junit.Test;

import static com.datascience.scheduler.CostBasedPriorityCalculatorTest.setUpNominalProject;

/**
 * @Author: konrad
 */
public class SchedulersForWorkerTest {

	@Test
	@Ignore("I don't have ideas for this")
	public void baseTest(){
		String costMethod = "MAXLIKELIHOOD";
		NominalProject project = setUpNominalProject();
		ILabelProbabilityDistributionCostCalculator lpdcc =
				LabelProbabilityDistributionCostCalculators.get(costMethod);
		IPriorityCalculator<String> pc = new CostBasedPriorityCalculator(lpdcc);
		Scheduler<String> scheduler = new Scheduler<String>(project, pc);
		SchedulersForWorker.ConfusionMatrixBased cmb = new SchedulersForWorker.ConfusionMatrixBased();
//		cmb.se
	}


}
