package com.datascience.gal.decision;

import java.util.Map;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.Datum;
import com.datascience.utils.CostMatrix;

/**
 * @author Konrad Kurdej
 */
public class ClassificationAlgorithm {

	private LabelProbabilityDistributionCalculator labelProbDistrCalc;
	private ObjectLabelDecisionAlgorithm objectLabelDecisionAlgorithm;
	
	public ClassificationAlgorithm(LabelProbabilityDistributionCalculator lpdc,
			ObjectLabelDecisionAlgorithm slda){
		labelProbDistrCalc = lpdc;
		objectLabelDecisionAlgorithm = slda;
	}
	
	public String predictLabel(Datum datum, AbstractDawidSkene ads){
		Map<String, Double> labelProbabilities = getLabelProbabilities(datum, ads);
		CostMatrix<String> costMatrix = Utils.getCategoriesCostMatrix(ads);
		return objectLabelDecisionAlgorithm.predictLabel(labelProbabilities, costMatrix);		
	}
	
	public Double predictedLabelCost(Datum datum, AbstractDawidSkene ads){
		Map<String, Double> labelProbabilities = getLabelProbabilities(datum, ads);
		CostMatrix<String> costMatrix = Utils.getCategoriesCostMatrix(ads);
		return objectLabelDecisionAlgorithm.predictedLabelCost(labelProbabilities, costMatrix);
	}
	
	private Map<String, Double> getLabelProbabilities(Datum datum, AbstractDawidSkene ads){
		return labelProbDistrCalc.calculateDistribution(datum, ads);
	}
}
