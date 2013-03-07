package com.datascience.gal.evaluation;

import com.datascience.core.base.Worker;
import com.datascience.gal.NominalProject;
import com.datascience.gal.WorkerResult;
import com.datascience.gal.decision.ILabelProbabilityDistributionCostCalculator;
import com.datascience.gal.decision.WorkerQualityCalculator;

public class WorkerEvaluator extends WorkerQualityCalculator{

	public WorkerEvaluator(ILabelProbabilityDistributionCostCalculator lpdcc) {
		super(lpdcc);
	}

	@Override
	public double getError(NominalProject project, Worker<String> w, String from, String to) {
		WorkerResult wr = project.getResults().getWokerResults().get(w);
		wr.computeEvalConfusionMatrix(project.getData().getCategories(), w.getAssigns());
		return wr.getEvalErrorRate(from, to);
	}

}
