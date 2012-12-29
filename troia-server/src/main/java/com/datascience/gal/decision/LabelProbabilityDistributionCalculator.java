package com.datascience.gal.decision;

import java.util.Map;

import com.datascience.gal.Datum;
import com.datascience.gal.DawidSkene;

/**
 * @author Konrad Kurdej
 */
public abstract class LabelProbabilityDistributionCalculator {

	public abstract Map<String, Double> calculateDistribution(Datum datum, DawidSkene ads);

	static public LabelProbabilityDistributionCalculator get(String lpdc) {
		if (lpdc.equals("MV")){
			return new LabelProbabilityDistributionCalculators.MV();
		}
		else // DS
			return new LabelProbabilityDistributionCalculators.DS();
	}
}
