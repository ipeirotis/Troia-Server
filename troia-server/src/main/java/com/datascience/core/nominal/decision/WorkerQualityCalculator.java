package com.datascience.core.nominal.decision;

import java.util.HashMap;
import java.util.Map;

import com.datascience.core.base.Worker;
import com.datascience.gal.AbstractDawidSkene;
import com.datascience.core.nominal.NominalProject;
import com.datascience.utils.ProbabilityDistributions;

/*
 * @author: Artur Ambroziak
 */

public abstract class WorkerQualityCalculator {

	private ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator;
	
	public abstract double getError(NominalProject project, Worker<String> w, String from, String to);

	public WorkerQualityCalculator(ILabelProbabilityDistributionCostCalculator lpdcc){
		this.labelProbabilityDistributionCostCalculator = lpdcc;
	}
	
	public double getCost(NominalProject project, Worker w){
		Map<String, Double> workerPriors = project.getResults().getWorkerResult(w).getPrior(
				w.getAssigns(), project.getData().getCategories());

		double cost = 0.;
		for (String c : project.getData().getCategories()) {
			Map<String, Double> softLabel = getSoftLabelForHardCategoryLabel(project, w, c);
			cost += labelProbabilityDistributionCostCalculator.predictedLabelCost(softLabel, project.getData().getCostMatrix()) * workerPriors.get(c);
		}
		return cost;
	}

	protected String getCostStr(double cost, boolean inverse){
		 return (Double.isNaN(cost)) ? "---" : Math.round(100 * (inverse ? 1. - cost : cost)) + "%";
	}
	
	private Map<String, Double> getSoftLabelForHardCategoryLabel(NominalProject project, Worker<String> w, String label) {
		// Pr(c | label) = Pr(label | c) * Pr (c) / Pr(label)
		Map<String, Double> worker_prior = project.getResults().getWorkerResult(w).getPrior(
				w.getAssigns(),
				project.getData().getCategories());
		Map<String, Double> result = new HashMap<String, Double>();
		for (String source : project.getData().getCategories()) {
			double soft = 0.;
			if (worker_prior.get(label) > 0){
				double error = getError(project, w, source, label);
				//TODO XXX FIXME
				soft = ((AbstractDawidSkene)project.getAlgorithm()).prior(source) * error / worker_prior.get(label);
			}
			result.put(source, soft);
		}

		return result;
	}
}
