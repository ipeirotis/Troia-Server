package com.datascience.gal.decision;

import com.datascience.utils.CostMatrix;
import com.datascience.utils.ProbabilityDistributions;
import com.google.common.base.Strings;
import java.util.Map;

/**
 *
 * @author konrad
 */
public class ObjectLabelDecisionAlgorithms {
	
	public static class MaxProbabilityDecisionAlgorithm implements IObjectLabelDecisionAlgorithm {

		@Override
		public String predictLabel(Map<String, Double> labelProbabilities,
								   CostMatrix<String> costMatrix) {
			String mostProbableLabel = null;
			double mostProbableLabelprob = Double.NEGATIVE_INFINITY;
			for (Map.Entry<String, Double> entry: labelProbabilities.entrySet()) {
				if (entry.getValue() > mostProbableLabelprob) {
					mostProbableLabel = entry.getKey();
					mostProbableLabelprob = entry.getValue();
				}
			}
			return mostProbableLabel;
		}
	}

	public static class MinCostDecisionAlgorithm implements IObjectLabelDecisionAlgorithm {

		@Override
		public String predictLabel(Map<String, Double> labelProbabilities,
								   CostMatrix<String> costMatrix) {
			String minCostLabel = null;
			double minCostLabelCost = Double.POSITIVE_INFINITY;
			for (String label: labelProbabilities.keySet()) {
				double cost = ProbabilityDistributions.calculateLabelCost(label, labelProbabilities, costMatrix);
				if (cost < minCostLabelCost) {
					minCostLabel = label;
					minCostLabelCost = cost;
				}
			}
			return minCostLabel;
		}
	}
	
	public static IObjectLabelDecisionAlgorithm get(String algorithmName){
		if (Strings.isNullOrEmpty(algorithmName)) {
			algorithmName = "MaxLikelihood";
		}
		algorithmName = algorithmName.toUpperCase();
		if ("MINCOST".equals(algorithmName)) {
			return new MinCostDecisionAlgorithm();
		}
		if ("MAXLIKELIHOOD".equals(algorithmName)) {
			return new MaxProbabilityDecisionAlgorithm();
		}
		throw new IllegalArgumentException("Unknown decision algorithm: " + algorithmName);
	}
}
