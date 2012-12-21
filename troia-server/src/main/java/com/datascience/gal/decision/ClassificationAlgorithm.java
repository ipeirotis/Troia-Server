package com.datascience.gal.decision;

import java.util.Map;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.Category;
import com.datascience.gal.Datum;
import com.datascience.utils.CostMatrix;

/**
 * @author Konrad Kurdej
 */
public class ClassificationAlgorithm {

	private LabelProbabilityDistributionCalculator labelProbDistrCalc;
	private ObjectLabelDecisionAlgorithm objectLabelDecisionAlgorithm;

	public ClassificationAlgorithm(LabelProbabilityDistributionCalculator lpdc,
								   ObjectLabelDecisionAlgorithm slda) {
		labelProbDistrCalc = lpdc;
		objectLabelDecisionAlgorithm = slda;
	}

	public String predictLabel(Datum datum, AbstractDawidSkene ads) {
		Map<String, Double> labelProbabilities = getLabelProbabilities(datum, ads);
		CostMatrix<String> costMatrix = Utils.getCategoriesCostMatrix(ads);
		return objectLabelDecisionAlgorithm.predictLabel(labelProbabilities, costMatrix);
	}

	public Double predictedLabelCost(Datum datum, AbstractDawidSkene ads) {
		Map<String, Double> labelProbabilities = getLabelProbabilities(datum, ads);
		CostMatrix<String> costMatrix = Utils.getCategoriesCostMatrix(ads);
		return objectLabelDecisionAlgorithm.predictedLabelCost(labelProbabilities, costMatrix);
	}

	private Map<String, Double> getLabelProbabilities(Datum datum, AbstractDawidSkene ads) {
		return labelProbDistrCalc.calculateDistribution(datum, ads);
	}
	
	public Double predictedLabelEvalCost(Datum datum, AbstractDawidSkene ads) {
		String correctLabel = ads.getEvaluationDatum(datum.getName()).getCorrectCategory();
		Double cost = 1.0;
		if (correctLabel != null){
			cost = 0.;
			CostMatrix<String> costMatrix = Utils.getCategoriesCostMatrix(ads);
			String predictedLabel = objectLabelDecisionAlgorithm.predictLabel(getLabelProbabilities(datum, ads), costMatrix);
			Category correctLabelCostVector = ads.getCategories().get(correctLabel);
			return correctLabelCostVector.getCost(predictedLabel);
		}
		return cost;
	}
}
