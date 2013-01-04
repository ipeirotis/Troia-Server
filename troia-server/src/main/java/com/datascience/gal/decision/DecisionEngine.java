package com.datascience.gal.decision;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.Category;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.Datum;
import com.datascience.gal.DawidSkene;
import com.datascience.utils.CostMatrix;
import java.util.Collection;
import java.util.HashMap;
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

	
	public String predictLabel(DawidSkene ds, Datum datum, CostMatrix<String> cm) {
		return objectLabelDecisionAlgorithm.predictLabel(getPD(datum, ds), cm);
	}
	
	public double estimateMissclassificationCost(DawidSkene ds, Datum datum, CostMatrix<String> cm) {
		return labelProbabilityDistributionCostCalculator.predictedLabelCost(
			getPD(datum, ds), cm);
	}
	
	public double evaluateMissclassificationCost(DawidSkene ds, CorrectLabel ed, CostMatrix<String> cm) {
		String correctLabel = ed.getCorrectCategory();
		if (correctLabel != null){
			Datum datum = ds.getObjects().get(ed.getObjectName());
			if (datum == null) {
				throw new IllegalArgumentException("Evaluation object doesn't match any datum: " + correctLabel);
			}
			
			String predictedLabel = predictLabel(ds, datum, cm);
			Category correctLabelCostVector = ds.getCategories().get(correctLabel);
			return correctLabelCostVector.getCost(predictedLabel);
		}
		return 1.; // FIXME: in previous version was 1. - maybe we should throw exception if correctLabel== null ?
	}
	
	public double costToQuality(DawidSkene ds, double cost){
		return 1. - cost / ((AbstractDawidSkene) ds).getMinSpammerCost();
	}
	
	public Map<String, Double> costToQuality(DawidSkene ds, Map<String, Double> costs){
		Map<String, Double> quality = new HashMap<String, Double>();
		for (Map.Entry<String, Double> e: costs.entrySet()) {
			quality.put(e.getKey(), costToQuality(ds, e.getValue()));
		}
		return quality;
	}
	
	public String predictLabel(DawidSkene ds, Datum datum) {
		return predictLabel(ds, datum, Utils.getCategoriesCostMatrix(ds));
	}

	public double estimateMissclassificationCost(DawidSkene ds, Datum datum) {
		return estimateMissclassificationCost(ds, datum, Utils.getCategoriesCostMatrix(ds));
	}

	public double evaluateMissclassificationCost(DawidSkene ds, CorrectLabel ed) {
		return evaluateMissclassificationCost(ds, ed, Utils.getCategoriesCostMatrix(ds));
	}
	
	
	public Map<String, String> predictLabels(DawidSkene ds){
		Map<String, Datum> datums = ds.getObjects();
		CostMatrix<String> cm = Utils.getCategoriesCostMatrix(ds);
		Map<String, String> ret = new HashMap<String, String>();
		for (Map.Entry<String, Datum> e: datums.entrySet()) {
			ret.put(e.getKey(), predictLabel(ds, e.getValue(), cm));
		}
		return ret;
	}
	
	public Map<String, Double> estimateMissclassificationCosts(DawidSkene ds){
		Map<String, Datum> datums = ds.getObjects();
		CostMatrix<String> cm = Utils.getCategoriesCostMatrix(ds);
		Map<String, Double> ret = new HashMap<String, Double>();
		for (Map.Entry<String, Datum> e: datums.entrySet()) {
			ret.put(e.getKey(), estimateMissclassificationCost(ds, e.getValue(), cm));
		}
		return ret;
	}
	
	public Map<String, Double> evaluateMissclassificationCosts(DawidSkene ds){
		Collection<CorrectLabel> evalData = ds.getEvaluationDatums();
		CostMatrix<String> cm = Utils.getCategoriesCostMatrix(ds);
		Map<String, Double> ret = new HashMap<String, Double>();
		for (CorrectLabel cl: evalData) {
			ret.put(cl.getObjectName(), evaluateMissclassificationCost(ds, cl, cm));
		}
		return ret;
	}
}
