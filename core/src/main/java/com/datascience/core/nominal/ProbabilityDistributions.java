package com.datascience.core.nominal;

import com.datascience.core.base.AssignedLabel;

import java.util.Collection;
import java.util.Map;

import static com.datascience.utils.ProbabilityDistributions.generateConstantDistribution;

/**
 * User: artur
 * Date: 5/6/13
 */
public class ProbabilityDistributions {

	static public Map<String, Double> getPriorBasedDistribution(INominalData data, NominalAlgorithm alg){
		if (data.arePriorsFixed())
			return data.getCategoryPriors();
		else
			return alg.getModel().categoryPriors;
	}

	public static Map<String, Double> generateMV_PD(Collection<String> categories,
													Collection<AssignedLabel<String>> assigns){
		Map<String, Double> pd = generateConstantDistribution(categories, 0.);
		Double base = 1. / assigns.size();
		for (AssignedLabel<String> assign: assigns) {
			String label = assign.getLabel();
			pd.put(label, pd.get(label) + base);
		}
		return pd;
	}

	static public Map<String, Double> generateOneLabelDistribution(NominalProject project, String label) {
		Map<String, Double> pd = generateConstantDistribution(project.getData().getCategories(), 0.);
		pd.put(label, 1.);
		return pd;
	}
}
