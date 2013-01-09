package com.datascience.gal.decision;

import java.util.HashMap;
import java.util.Map;

import com.datascience.gal.Datum;
import com.datascience.gal.DawidSkene;
import com.datascience.utils.CostMatrix;

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
	
	public String predictLabel(DawidSkene ds, Datum datum) {
		return predictLabel(ds, datum, Utils.getCategoriesCostMatrix(ds));
	}

	public double estimateMissclassificationCost(DawidSkene ds, Datum datum) {
		return estimateMissclassificationCost(ds, datum, Utils.getCategoriesCostMatrix(ds));
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
}
