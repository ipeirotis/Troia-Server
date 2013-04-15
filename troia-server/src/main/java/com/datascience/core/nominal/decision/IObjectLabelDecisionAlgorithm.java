package com.datascience.core.nominal.decision;

import java.util.Map;

import com.datascience.utils.CostMatrix;

/**
 * @author Konrad Kurdej
 */
public interface IObjectLabelDecisionAlgorithm {

	String predictLabel(Map<String, Double> labelProbabilities, CostMatrix<String> costMatrix);
}
