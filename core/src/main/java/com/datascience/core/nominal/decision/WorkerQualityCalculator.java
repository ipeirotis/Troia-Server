package com.datascience.core.nominal.decision;

import java.util.HashMap;
import java.util.Map;

import com.datascience.core.base.Worker;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.nominal.Quality;

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
				project.getData().getWorkerAssigns(w), project.getData().getCategories());

		double cost = 0.;
		for (String c : project.getData().getCategories()) {
			Map<String, Double> softLabel = getSoftLabelForHardCategoryLabel(project, w, c);
			cost += labelProbabilityDistributionCostCalculator.predictedLabelCost(softLabel, project.getData().getCostMatrix()) * workerPriors.get(c);
		}
		return cost;
	}

	public Map<String, Double> getCosts(NominalProject project){
		Map<String, Double> ret = new HashMap<String, Double>();
		for (Worker<String> w : project.getData().getWorkers()){
			ret.put(w.getName(), getCost(project, w));
		}
		return ret;
	}

	public double getQuality(NominalProject project, Worker w){
		return Quality.fromCost(project, getCost(project, w));
	}

	private Map<String, Double> getSoftLabelForHardCategoryLabel(NominalProject project, Worker<String> w, String label) {
		// Pr(c | label) = Pr(label | c) * Pr (c) / Pr(label)
		Map<String, Double> worker_prior = project.getResults().getWorkerResult(w).getPrior(
				project.getData().getWorkerAssigns(w),
				project.getData().getCategories());
		Map<String, Double> result = new HashMap<String, Double>();
		for (String source : project.getData().getCategories()) {
			double soft = 0.;
			if (worker_prior.get(label) > 0){
				double error = getError(project, w, source, label);
				soft = project.getAlgorithm().prior(source) * error / worker_prior.get(label);
			}
			result.put(source, soft);
		}

		return result;
	}
}
