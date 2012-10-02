package com.datascience.gal.decision;

import java.util.Map;

import com.datascience.utils.CostMatrix;

/**
 * @author Konrad Kurdej
 */
public abstract class MaxProbabilityDecision extends ObjectLabelDecisionAlgorithm{

	@Override
	public String predictLabel(Map<String, Double> labelProbabilities,
			CostMatrix<String> costMatrix) {
		String mostProbableLabel = null;
		double mostProbableLabelprob = Double.MIN_VALUE;
		for (Map.Entry<String, Double> entry: labelProbabilities.entrySet()){
			if (entry.getValue() > mostProbableLabelprob){
				mostProbableLabel = entry.getKey();
				mostProbableLabelprob = entry.getValue();
			}
		}
		return mostProbableLabel;
	}
}
