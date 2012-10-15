package com.datascience.gal.quality;

import java.util.HashMap;
import java.util.Map;

import com.datascience.gal.Datum;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.decision.LabelProbabilityDistributionCalculator;
import com.datascience.gal.decision.LabelProbabilityDistributionCalulators;

public abstract class MaxLikelihoodCostEvaluator extends ClassificationCostEvaluator {

	protected MaxLikelihoodCostEvaluator(
			LabelProbabilityDistributionCalculator calculator) {
		super(calculator);
	}

	@Override
	protected Map<String, Double> getProbabilityVector(DawidSkene ds,
			Datum datum) {
		Map<String, Double>	categoryProbability = this.calculator.calculateDistribution(datum, ds);
		Map<String, Double> result = new HashMap<String, Double>();
		for (String c : categoryProbability.keySet()) {
			result.put(c, 0.0);
		}
		
		if (datum.isGold()) {
			result.put(datum.getCorrectCategory(), 1.0);
		}else{
			String label = null;
			double maxProbability = -1;

			for (String category : categoryProbability.keySet()) {
				Double probability = categoryProbability.get(category);
				if (probability > maxProbability) {
					maxProbability = probability;
					label = category;
				} else if (probability == maxProbability) {
					// In case of a tie, break ties randomly
					// TODO: This is a corner case. We can also break ties
					// using the priors. But then we also need to group together
					// all the ties, and break ties probabilistically across the
					// group. Otherwise, we slightly favor the later comparisons.
					if (Math.random() > 0.5) {
						maxProbability = probability;
						label = category;
					}
				}
			}

			result.put(label, 1.0);
		}
		
		return result;
	}

}
