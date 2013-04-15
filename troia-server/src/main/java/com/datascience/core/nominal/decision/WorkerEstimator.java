package com.datascience.core.nominal.decision;

import com.datascience.core.nominal.NominalAlgorithm;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.NominalProject;

public class WorkerEstimator extends WorkerQualityCalculator{

	public WorkerEstimator(ILabelProbabilityDistributionCostCalculator lpdcc) {
		super(lpdcc);
	}

	@Override
	public double getError(NominalProject project, Worker<String> w, String from, String to) {
		NominalAlgorithm algorithm = (NominalAlgorithm) project.getAlgorithm();

		return project.getResults().getWorkerResult(w).getErrorRate(
				algorithm.getErrorRateCalculator(),from, to);
	}
}
