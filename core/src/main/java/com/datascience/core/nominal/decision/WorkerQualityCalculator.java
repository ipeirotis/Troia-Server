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
	
	public abstract double getError(NominalProject project, Worker w, String from, String to);

	public WorkerQualityCalculator(ILabelProbabilityDistributionCostCalculator lpdcc){
		this.labelProbabilityDistributionCostCalculator = lpdcc;
	}
	
	public double getCost(NominalProject project, Worker w){
		Map<String, Double> workerPriors = project.getResults().getWorkerResult(w).getPrior(
				project.getData().getWorkerAssigns(w), project.getData().getCategories());

		double cost = 0.;
		for (String c : project.getData().getCategories()) {
			Map<String, Double> softLabel = getSoftLabelForHardCategoryLabel(project, w, c, workerPriors);
			cost += labelProbabilityDistributionCostCalculator.predictedLabelCost(softLabel, project.getData().getCostMatrix()) * workerPriors.get(c);
		}
		return cost;
	}

	public Map<Worker, Double> getCosts(NominalProject project){
		Map<Worker, Double> ret = new HashMap<Worker, Double>();
		for (Worker w : project.getData().getWorkers()){
			ret.put(w, getCost(project, w));
		}
		return ret;
	}

	public double getQuality(NominalProject project, Worker w){
		return Quality.fromCost(project, getCost(project, w));
	}

	private Map<String, Double> getSoftLabelForHardCategoryLabel(NominalProject project, Worker w, String label, Map<String, Double> workerPriors) {
		// Pr(c | label) = Pr(label | c) * Pr (c) / Pr(label)
		Map<String, Double> result = new HashMap<String, Double>();
		for (String source : project.getData().getCategories()) {
			double soft = 0.;
			if (workerPriors.get(label) > 0){
				double error = getError(project, w, source, label);
				soft = project.getAlgorithm().prior(source) * error / workerPriors.get(label);
			}
			result.put(source, soft);
		}

		return result;
	}
}
