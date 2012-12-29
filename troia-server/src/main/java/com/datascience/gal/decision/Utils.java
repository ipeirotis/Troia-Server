package com.datascience.gal.decision;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.Category;
import com.datascience.gal.Datum;
import com.datascience.gal.DawidSkene;
import com.datascience.utils.CostMatrix;

/**
 * @author Konrad Kurdej
 */
public class Utils {

	static public Double calculateLabelCost(String calcLabel,
			Map<String, Double> labelProbabilities, CostMatrix<String> costMatrix) {
		double sum = 0.;
		for (String label: costMatrix.getKnownValues()) {
			double cost = costMatrix.getCost(label, calcLabel);
			double prob = labelProbabilities.get(label);
			sum += cost * prob;
		}
		return sum;
	}

	static public <T> Map<T, Double> generateConstantDistribution(
		Collection<T> objects, double value) {
		Map<T, Double> cd = new HashMap<T, Double>();
		for (T object : objects) {
			cd.put(object, value);
		}
		return cd;
	}

	static public <T> Map<T, Double> generateUniformDistribution(
		Collection<T> objects) {
		Map<T, Double> inital = new HashMap<T, Double>();
		return generateUniformDistribution(objects, inital);
	}

	static public <T> Map<T, Double> generateUniformDistribution(
		Collection<T> objects, Map<T, Double> initialDistribution) {
		double revn = 1. / objects.size();
		for (T object : objects) {
			initialDistribution.put(object, revn);
		}
		return initialDistribution;
	}

	static public CostMatrix<String> getCategoriesCostMatrix(AbstractDawidSkene ads) {
		CostMatrix<String> cm = new CostMatrix<String>();
		for (Category c: ads.getCategories().values()) {
			String name = c.getName();
			for (Map.Entry<String, Double> entry: c.getMisclassificationCosts().entrySet()) {
				cm.add(name, entry.getKey(), entry.getValue());
			}
		}
		return cm;
	}
	
	static public double estimateMissclassificationCost(DawidSkene ds, 
			LabelProbabilityDistributionCalculator lpdc, 
			LabelingCostAlgorithm lca, 
			String object_id) {
		// Ugly as hell but I don't see any other way ...
		AbstractDawidSkene ads = (AbstractDawidSkene) ds;
		Datum datum = ads.getObject(object_id);
		
		return lca.predictedLabelCost(lpdc.calculateDistribution(datum, ads), getCategoriesCostMatrix(ads));
	}
	
	static public double evaluateMissclassificationCost(DawidSkene ds, 
			LabelProbabilityDistributionCalculator lpdc, 
			ObjectLabelDecisionAlgorithm olda,
			String object_id) {
		AbstractDawidSkene ads = (AbstractDawidSkene) ds;
		Datum datum = ads.getObject(object_id);
		
		String correctLabel = ads.getEvaluationDatum(datum.getName()).getCorrectCategory();
		Double cost = 1.0;
		if (correctLabel != null){
			cost = 0.;
			String predictedLabel = olda.predictLabel(lpdc.calculateDistribution(datum, ads), 
					Utils.getCategoriesCostMatrix(ads));
			Category correctLabelCostVector = ads.getCategories().get(correctLabel);
			return correctLabelCostVector.getCost(predictedLabel);
		}
		return cost;
	}
	
	static public String predictLabel(DawidSkene ds, 
			LabelProbabilityDistributionCalculator lpdc, 
			ObjectLabelDecisionAlgorithm olda,
			String object_id) {
		AbstractDawidSkene ads = (AbstractDawidSkene) ds;
		Datum datum = ads.getObject(object_id);
		
		return olda.predictLabel(lpdc.calculateDistribution(datum, ads), Utils.getCategoriesCostMatrix(ads));
	}
}
