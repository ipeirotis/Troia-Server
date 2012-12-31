package com.datascience.gal.decision;

import com.datascience.gal.Category;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.Datum;
import com.datascience.gal.DawidSkene;
import java.util.Map;

/**
 *
 * @author konrad
 */
public class DecisionEngine {

	ILabelProbabilityDistributionCalculator labelProbabilityDistributionCalculator;
	ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator;
	IObjectLabelDecisionAlgorithm objectLabelDecisionAlgorithm;
	
	public DecisionEngine(ILabelProbabilityDistributionCalculator labelProbabilityDistributionCalculator,
			ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator,
			IObjectLabelDecisionAlgorithm objectLabelDecisionAlgorithm){
		this.labelProbabilityDistributionCalculator = labelProbabilityDistributionCalculator;
		this.labelProbabilityDistributionCostCalculator = labelProbabilityDistributionCostCalculator;
		this.objectLabelDecisionAlgorithm = objectLabelDecisionAlgorithm;
	}
	
	
	public Map<String, Double> getPD(Datum datum, DawidSkene ds){
		return labelProbabilityDistributionCalculator.calculateDistribution(datum, ds);
	}

	
	public double estimateMissclassificationCost(DawidSkene ds, Datum datum) {
		return labelProbabilityDistributionCostCalculator.predictedLabelCost(
			getPD(datum, ds), Utils.getCategoriesCostMatrix(ds));
	}
	
	
	public double evaluateMissclassificationCost(DawidSkene ds, CorrectLabel ed) {
		String correctLabel = ed.getCorrectCategory();
		if (correctLabel != null){
			Datum datum = ds.getObjects().get(ed.getObjectName());
			if (datum == null) {
				throw new IllegalArgumentException("Evalutaion object doesn't match any datum: " + correctLabel);
			}
			
			String predictedLabel = predictLabel(ds, datum);
			Category correctLabelCostVector = ds.getCategories().get(correctLabel);
			return correctLabelCostVector.getCost(predictedLabel);
		}
		return 1.; // FIXME: in previous version was 1. - maybe we should throw exception if correctLabel== null ?
	}
	
	
	public String predictLabel(DawidSkene ds, Datum datum) {
		return objectLabelDecisionAlgorithm.predictLabel(getPD(datum, ds),
			Utils.getCategoriesCostMatrix(ds));
	}
}
