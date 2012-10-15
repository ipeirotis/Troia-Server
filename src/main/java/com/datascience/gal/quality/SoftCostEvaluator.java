package com.datascience.gal.quality;

import java.util.Map;

import com.datascience.gal.Datum;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.decision.LabelProbabilityDistributionCalculator;

public class SoftCostEvaluator extends ClassificationCostEvaluator{

	protected SoftCostEvaluator(
			LabelProbabilityDistributionCalculator calculator) {
		super(calculator);
	}

	@Override
	protected Map<String, Double> getProbabilityVector(DawidSkene ds,
			Datum datum) {
		return this.calculator
				.calculateDistribution(datum, ds);
	}

}
