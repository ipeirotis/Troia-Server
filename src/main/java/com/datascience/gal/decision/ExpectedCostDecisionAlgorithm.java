package com.datascience.gal.decision;

import java.util.Map;
import java.util.Set;

import com.datascience.utils.CostMatrix;

/**
 * @author Konrad Kurdej
 */
public class ExpectedCostDecisionAlgorithm extends MaxProbabilityDecision{

	@Override
	public Double predictedLabelCost(Map<String, Double> labelProbabilities,
			CostMatrix<String> costMatrix) {
		Set<Map.Entry<String, Double>> entries = labelProbabilities.entrySet();
		double cost = 0.;
		for (Map.Entry<String, Double> entry1: entries){
			for (Map.Entry<String, Double> entry2: entries){
				double errCost = costMatrix.getCost(entry1.getKey(), entry2.getKey());
				cost += entry1.getValue() * entry2.getValue() * errCost;
			}
		}
		return cost;
	}
}
