package com.datascience.gal.decision;

import java.util.HashMap;
import java.util.Map;

import com.datascience.core.base.Worker;
import com.datascience.gal.Category;
import com.datascience.gal.NominalProject;

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
		Map<String, Double> workerPriors = project.getResults().getWokerResults().get(w).getPrior(
				w.getAssigns(), project.getData().getCategoriesNames());

		double cost = 0.;
		for (Category c : project.getData().getCategories()) {
			Map<String, Double> softLabel = getSoftLabelForHardCategoryLabel(project, w, c.getName());
			cost += labelProbabilityDistributionCostCalculator.predictedLabelCost(softLabel, Utils.getCategoriesCostMatrix(project)) * workerPriors.get(c.getName());
		}
		return cost;
	}

	protected String getCostStr(double cost, boolean inverse){
		 return (Double.isNaN(cost)) ? "---" : Math.round(100 * (inverse ? 1. - cost : cost)) + "%";
	}
	
	private Map<String, Double> getSoftLabelForHardCategoryLabel(NominalProject project, Worker w, String label) {
		// Pr(c | label) = Pr(label | c) * Pr (c) / Pr(label)
		Map<String, Double> worker_prior = project.getResults().getWokerResults().get(w).getPrior(
				w.getAssigns(),
				project.getData().getCategoriesNames());
		Map<String, Double> result = new HashMap<String, Double>();
		for (Category source : project.getData().getCategories()) {
			double soft = 0.;
			if (worker_prior.get(label) > 0){
				double error = getError(project, w, source.getName(), label);
				soft = ds.prior(source.getName()) * error / worker_prior.get(label);
			}
			result.put(source.getName(), soft);
		}

		return result;
	}
}
