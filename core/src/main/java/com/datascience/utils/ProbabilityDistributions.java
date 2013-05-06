package com.datascience.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Konrad Kurdej
 */
public class ProbabilityDistributions {

	static public Double calculateLabelCost(String calcLabel,
			Map<String, Double> labelProbabilities, CostMatrix<String> costMatrix) {
		if (calcLabel == null) {
			return Double.NaN;
		}
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
	
	static public Map<String, Double> generateGoldDistribution(Collection<String> categories, String correctCat){
		Map<String, Double> ret = generateConstantDistribution(categories, 0.);
		ret.put(correctCat, 1.);
		return ret;
	}
}
