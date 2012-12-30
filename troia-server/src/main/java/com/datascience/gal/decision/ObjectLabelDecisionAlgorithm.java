package com.datascience.gal.decision;

import java.util.Map;

import com.datascience.utils.CostMatrix;

/**
 * @author Konrad Kurdej
 */
public abstract class ObjectLabelDecisionAlgorithm {

	abstract public String predictLabel(Map<String, Double> labelProbabilities,
										CostMatrix<String> costMatrix);

	static public ObjectLabelDecisionAlgorithm get(String olda) {
		if (olda.equals("MinCost")){
			return new MinCostDecisionAlgorithm();
		}else { // Maxlikelihood
			return new MaxProbabilityDecisionAlgorithm();
		}
	}
}
