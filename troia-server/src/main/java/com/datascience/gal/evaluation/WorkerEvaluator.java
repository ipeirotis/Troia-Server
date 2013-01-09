package com.datascience.gal.evaluation;

import com.datascience.gal.DawidSkene;
import com.datascience.gal.Worker;
import com.datascience.gal.decision.ILabelProbabilityDistributionCostCalculator;
import com.datascience.gal.decision.WorkerQualityCalculator;

public class WorkerEvaluator extends WorkerQualityCalculator{

	public WorkerEvaluator(ILabelProbabilityDistributionCostCalculator lpdcc) {
		super(lpdcc);
	}

	@Override
	public double getError(DawidSkene ds, Worker w, String from, String to) {
		w.computeEvalConfusionMatrix(ds.getEvaluationDatums(), ds.getCategories().values());
		return w.getEvalErrorRate(from, to);
	}

}
