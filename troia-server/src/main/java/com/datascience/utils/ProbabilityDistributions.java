package com.datascience.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.NominalData;
import com.datascience.core.stats.Category;
import com.datascience.core.base.NominalProject;

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

	static public CostMatrix<String> getCategoriesCostMatrix(NominalProject project) {
		CostMatrix<String> cm = new CostMatrix<String>();
		for (Category c: project.getData().getCategories()) {
			String name = c.getName();
			for (Map.Entry<String, Double> entry: c.getMisclassificationCosts().entrySet()) {
				cm.add(name, entry.getKey(), entry.getValue());
			}
		}
		return cm;
	}

	static public Map<String, Double> getSpammerDistribution(NominalData data){
		return getPriorBasedDistribution(data);
	}

	static public Map<String, Double> getPriorBasedDistribution(NominalData data){
		Map<String, Double> pd = new HashMap<String, Double>();
		for (Category c: data.getCategories()) {
			pd.put(c.getName(), c.getPrior());
		}
		return pd;
	}

	public static Map<String, Double> generateMV_PD(Collection<String> categories,
				Collection<AssignedLabel<String>> assigns){
		Map<String, Double> pd = ProbabilityDistributions.generateConstantDistribution(categories, 0.);
		Double base = 1. / assigns.size();
		for (AssignedLabel<String> assign: assigns) {
			String label = assign.getLabel();
			pd.put(label, pd.get(label) + base);
		}
		return pd;
	}
}
