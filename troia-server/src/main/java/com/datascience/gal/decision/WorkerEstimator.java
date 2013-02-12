package com.datascience.gal.decision;

import com.datascience.gal.DawidSkene;
import com.datascience.gal.Worker;
import com.datascience.gal.decision.ILabelProbabilityDistributionCostCalculator;
import com.datascience.gal.decision.WorkerQualityCalculator;

public class WorkerEstimator extends WorkerQualityCalculator{

	public WorkerEstimator(ILabelProbabilityDistributionCostCalculator lpdcc) {
		super(lpdcc);
	}

	@Override
	public double getError(DawidSkene ds, Worker w, String from, String to) {
		return ds.getErrorRateForWorker(w, from, to);
	}
}
