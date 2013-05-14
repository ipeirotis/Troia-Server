package com.datascience.core.nominal.decision;

import com.datascience.utils.CostMatrix;
import com.datascience.utils.ProbabilityDistributions;
import com.google.common.base.Strings;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Artur & Konrad
 */
public class LabelProbabilityDistributionCostCalculators {

	protected static class SelectedLabeBased implements ILabelProbabilityDistributionCostCalculator {

		private IObjectLabelDecisionAlgorithm labelChooser;

		public SelectedLabeBased(IObjectLabelDecisionAlgorithm labelChooser){
			this.labelChooser = labelChooser;
		}

		@Override
		public Double predictedLabelCost(Map<String, Double> labelProbabilities,
				CostMatrix<String> costMatrix) {
			String choosenLabel = labelChooser.predictLabel(labelProbabilities, costMatrix);
			return ProbabilityDistributions.calculateLabelCost(choosenLabel, labelProbabilities, costMatrix);
		}
	}

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
	
	public static ILabelProbabilityDistributionCostCalculator get(String method){
		if (Strings.isNullOrEmpty(method)) {
			method = "ExpectedCost";
		}
		method = method.toUpperCase();
		if ("EXPECTEDCOST".equals(method)) {
			return new ExpectedCostAlgorithm();
		}
		try {
			IObjectLabelDecisionAlgorithm olda = ObjectLabelDecisionAlgorithms.get(method);
			return new SelectedLabeBased(olda);
		} catch (IllegalArgumentException ex) {
			throw new IllegalArgumentException(
				"Unknown cost calculation method: " + method);
		}
	}
}
