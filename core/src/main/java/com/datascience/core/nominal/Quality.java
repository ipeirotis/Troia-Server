package com.datascience.core.nominal;

import java.util.HashMap;
import java.util.Map;

import static com.datascience.core.nominal.ProbabilityDistributions.getPriorBasedDistribution;

/**
 *
 * @author konrad
 */
public class Quality {

	/**
	 * Gets as input a "soft label" (i.e., a distribution of probabilities over
	 * classes) and returns the smallest possible cost for this soft label.
	 *
	 * @return The expected cost of this soft label
	 */
	static private double getMinSoftLabelCost(Map<String, Double> probabilities, INominalData data) {

		double min_cost = Double.MAX_VALUE;

		for (String c1 : probabilities.keySet()) {
			// So, with probability p1 it belongs to class c1
			// Double p1 = probabilities.get(c1);

			// What is the cost in this case?
			double costfor_c2 = 0.0;
			for (String c2 : probabilities.keySet()) {
				// With probability p2 it actually belongs to class c2
				double p2 = probabilities.get(c2);
				Double cost = data.getCostMatrix().getCost(c1, c2);
				costfor_c2 += p2 * cost;

			}
			min_cost = Math.min(min_cost, costfor_c2);
		}

		return min_cost;
	}

	/**
	 * Returns the minimum possible cost of a "spammer" worker, who assigns
	 * completely random labels.
	 *
	 * @return The expected cost of a spammer worker
	 */
	static public double getMinSpammerCost(INominalData data, NominalAlgorithm alg) {
		return getMinSoftLabelCost(getPriorBasedDistribution(data, alg), data);
	}

	static public double fromCost(NominalProject project, double cost){
		return 1. - cost / getMinSpammerCost(project.getData(), project.getAlgorithm());
	}
	
	static public Map<String, Double> fromCosts(NominalProject project, Map<String, Double> costs){
		Map<String, Double> quality = new HashMap<String, Double>();
		for (Map.Entry<String, Double> e: costs.entrySet()) {
			quality.put(e.getKey(), fromCost(project, e.getValue()));
		}
		return quality;
	}

	static public double getAverage(NominalProject project, Map<String, Double> costs){
		//sum of object qualities is: n - sum_of_costs/s, where s in minSpammerCost
		int cnt = costs.size();
		double costSum = 0.;
		for (Double val : costs.values()){
			costSum += val;
		}
		return (cnt - costSum/getMinSpammerCost(project.getData(), project.getAlgorithm())) / cnt;

	}
}
