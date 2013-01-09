package com.datascience.gal.decision;

import java.util.HashMap;
import java.util.Map;

import com.datascience.gal.Category;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.Worker;

/*
 * @author: Artur Ambroziak
 */

public abstract class WorkerQualityCalculator {

	private ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator;
	
	public abstract double getError(DawidSkene ds, Worker w, String from, String to);

	public WorkerQualityCalculator(ILabelProbabilityDistributionCostCalculator lpdcc){
		this.labelProbabilityDistributionCostCalculator = lpdcc;
	}
	
	public double getCost(DawidSkene ds, Worker w){
		Map<String, Double> categoryPriors = ds.getCategoryPriors();
		Map<String, Double> workerPriors = w.getPrior(categoryPriors);

		double cost = 0.;
		for (Category c : ds.getCategories().values()) {
			Map<String, Double> softLabel = getSoftLabelForHardCategoryLabel(ds, w, c.getName());
			cost += labelProbabilityDistributionCostCalculator.predictedLabelCost(softLabel, Utils.getCategoriesCostMatrix(ds)) * workerPriors.get(c.getName());
		}
		return cost;
	}

	protected String getCostStr(double cost, boolean inverse){
		 return (Double.isNaN(cost)) ? "---" : Math.round(100 * (inverse ? 1. - cost : cost)) + "%";
	}
	
	private Map<String, Double> getSoftLabelForHardCategoryLabel(DawidSkene ds, Worker w, String label) {

		// Pr(c | label) = Pr(label | c) * Pr (c) / Pr(label)
		Map<String, Double> worker_prior = w.getPrior(ds.getCategoryPriors());
		Map<String, Double> result = new HashMap<String, Double>();
		for (Category source : ds.getCategories().values()) {
			double soft = 0.;
			if (worker_prior.get(label) > 0){
				double error = getError(ds, w, source.getName(), label);
				soft = ds.prior(source.getName()) * error / worker_prior.get(label);
			}
			result.put(source.getName(), soft);
		}

		return result;
	}
}
