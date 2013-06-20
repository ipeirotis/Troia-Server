package com.datascience.core.nominal;

import com.datascience.core.nominal.decision.DecisionEngine;
import com.datascience.core.nominal.decision.LabelProbabilityDistributionCostCalculators;
import com.datascience.core.nominal.decision.ObjectLabelDecisionAlgorithms;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author konrad
 */
public class Quality {

	/**
	 * Returns the minimum possible cost of a "spammer" worker, who assigns
	 * completely random labels.
	 *
	 * @return The expected cost of a spammer worker
	 */
	static public double getMinSpammerCost(NominalProject project) {
		DecisionEngine de = new DecisionEngine(new LabelProbabilityDistributionCostCalculators.SelectedLabelBased(new ObjectLabelDecisionAlgorithms.MinCostDecisionAlgorithm()), null);
		return de.estimateMissclassificationCost(project, project.getAlgorithm().getCategoryPriors());
	}

	static public double getExpSpammerCost(NominalProject project){
		DecisionEngine de = new DecisionEngine(new LabelProbabilityDistributionCostCalculators.ExpectedCostAlgorithm(), null);
		return de.estimateMissclassificationCost(project, project.getAlgorithm().getCategoryPriors());
	}

	static public double fromCost(NominalProject project, double cost){
		return 1. - cost / getMinSpammerCost(project);
	}
	
	static public Map<String, Double> fromCosts(NominalProject project, Map<String, Double> costs){
		Map<String, Double> quality = new HashMap<String, Double>();
		for (Map.Entry<String, Double> e: costs.entrySet()) {
			quality.put(e.getKey(), fromCost(project, e.getValue()));
		}
		return quality;
	}

	static public double getAverage(NominalProject project, Map<String, Double> costs){
		//sum of object qualities is: n - sum_of_costs/s, where s in minSpammerCost
		int cnt = costs.size();
		double costSum = 0.;
		for (Double val : costs.values()){
			costSum += val;
		}
		return (cnt - costSum/getMinSpammerCost(project)) / cnt;

	}
}
