package com.datascience.gal.decision;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import com.datascience.gal.Category;
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

	static public CostMatrix<String> getCategoriesCostMatrix(DawidSkene ads) {
		CostMatrix<String> cm = new CostMatrix<String>();
		for (Category c: ads.getCategories().values()) {
			String name = c.getName();
			for (Map.Entry<String, Double> entry: c.getMisclassificationCosts().entrySet()) {
				cm.add(name, entry.getKey(), entry.getValue());
			}
		}
		return cm;
	}
}
