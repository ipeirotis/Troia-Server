package com.datascience.gal.decision;

import java.util.Map;

import com.datascience.utils.CostMatrix;

/**
 * @author Artur Ambroziak
 */
public interface ILabelProbabilityDistributionCostCalculator {

	Double predictedLabelCost(Map<String, Double> labelProbabilities,
			CostMatrix<String> costMatrix);
}
