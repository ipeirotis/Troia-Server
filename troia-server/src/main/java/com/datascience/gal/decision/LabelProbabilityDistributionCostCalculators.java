package com.datascience.gal.decision;

import com.datascience.utils.CostMatrix;
import com.google.common.base.Strings;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Artur & Konrad
 */
public class LabelProbabilityDistributionCostCalculators {

	public static class ExpectedCostAlgorithm implements ILabelProbabilityDistributionCostCalculator {

		@Override
		public Double predictedLabelCost(Map<String, Double> labelProbabilities,
										 CostMatrix<String> costMatrix) {
			Set<Map.Entry<String, Double>> entries = labelProbabilities.entrySet();
			double cost = 0.;
			for (Map.Entry<String, Double> entry1: entries) {
				for (Map.Entry<String, Double> entry2: entries) {
					double errCost = costMatrix.getCost(entry1.getKey(), entry2.getKey());
					cost += entry1.getValue() * entry2.getValue() * errCost;
				}
			}
			return cost;
		}
	}
	
	public static class MinCostAlgorithm implements ILabelProbabilityDistributionCostCalculator {

		@Override
		public Double predictedLabelCost(Map<String, Double> labelProbabilities,
										 CostMatrix<String> costMatrix) {
			double minCostLabelCost = Double.MAX_VALUE;
			for (String label: labelProbabilities.keySet()) {
				double cost = Utils.calculateLabelCost(label, labelProbabilities, costMatrix);
				if (cost < minCostLabelCost) {
					minCostLabelCost = cost;
				}
			}
			return minCostLabelCost;
		}
	}
	
	public static ILabelProbabilityDistributionCostCalculator get(String method){
		if (Strings.isNullOrEmpty(method)) {
			method = "ExpectedCost";
		}
		method = method.toUpperCase();
		if ("MINCOST".equals(method)){
			return new MinCostAlgorithm();
		}
		if ("EXPECTEDCOST".equals(method)) {
			return new ExpectedCostAlgorithm();
		}
		throw new IllegalArgumentException(
			"Unknown cost calculation method: " + method);
	}
}
