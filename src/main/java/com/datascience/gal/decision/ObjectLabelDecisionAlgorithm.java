package com.datascience.gal.decision;

import java.util.Map;

import com.datascience.utils.CostMatrix;

/**
 * @author Konrad Kurdej
 */
public abstract class ObjectLabelDecisionAlgorithm {

	abstract public String predictLabel(Map<String, Double> labelProbabilities,
			CostMatrix<String> costMatrix);

	abstract public Double predictedLabelCost(Map<String, Double> labelProbabilities,
			CostMatrix<String> costMatrix);
}
