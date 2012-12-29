package com.datascience.gal.decision;

import java.util.Map;

import com.datascience.utils.CostMatrix;

/**
 * @author Artur Ambroziak
 */
public class MinCostAlgorithm extends LabelingCostAlgorithm {

	@Override
	public Double predictedLabelCost(Map<String, Double> labelProbabilities,
									 CostMatrix<String> costMatrix) {
		double minCostLabelCost = Double.MAX_VALUE;
		for (String label: labelProbabilities.keySet()) {
			double cost = Utils.calculateLabelCost(label, labelProbabilities, costMatrix);
			if (cost < minCostLabelCost)
				minCostLabelCost = cost;
		}
		return minCostLabelCost;
	}
}
