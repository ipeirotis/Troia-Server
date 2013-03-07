package com.datascience.gal.decision;

import com.datascience.core.base.Worker;
import com.datascience.gal.NominalProject;

public class WorkerEstimator extends WorkerQualityCalculator{

	public WorkerEstimator(ILabelProbabilityDistributionCostCalculator lpdcc) {
		super(lpdcc);
	}

	@Override
	public double getError(NominalProject project, Worker<String> w, String from, String to) {
		return project.getResults().getWokerResults().get(w).getErrorRate(from, to);
	}
}
