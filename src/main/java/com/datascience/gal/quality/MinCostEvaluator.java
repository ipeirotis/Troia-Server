package com.datascience.gal.quality;

import java.util.HashMap;
import java.util.Map;

import com.datascience.gal.Datum;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.decision.LabelProbabilityDistributionCalculator;

public abstract class MinCostEvaluator extends ClassificationCostEvaluator {

	protected MinCostEvaluator(LabelProbabilityDistributionCalculator calculator) {
		super(calculator);
	}

	@Override
	protected Map<String, Double> getProbabilityVector(DawidSkene ds,
			Datum datum) {
		Map<String, Double> categoryProbability = this.calculator
				.calculateDistribution(datum, ds);
		Map<String, Double> result = new HashMap<String, Double>();
		for (String c : categoryProbability.keySet()) {
			result.put(c, 0.0);
		}

		if (datum.isGold()) {
			result.put(datum.getCorrectCategory(), 1.0);
		} else {
			String label = null;
			Double min_cost = Double.MAX_VALUE;

			for (String c1 : categoryProbability.keySet()) {
				// So, with probability p1 it belongs to class c1
				// Double p1 = probabilities.get(c1);

				// What is the cost in this case?
				Double costfor_c1 = 0.0;
				for (String c2 : categoryProbability.keySet()) {
					// With probability p2 it actually belongs to class c2
					Double p2 = categoryProbability.get(c2);
					Double cost = ds.getCategories().get(c2).getCost(c1);
					costfor_c1 += p2 * cost;

				}

				if (costfor_c1 < min_cost) {
					label = c1;
					min_cost = costfor_c1;
				}

			}
			result.put(label, 1.0);
		}
		return result;
	}

}
