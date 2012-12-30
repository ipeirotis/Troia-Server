package com.datascience.gal.decision;

import java.util.Map;

import com.datascience.gal.Datum;
import com.datascience.gal.DawidSkene;

/**
 * @author Konrad Kurdej
 */
public interface ILabelProbabilityDistributionCalculator {

	Map<String, Double> calculateDistribution(Datum datum, DawidSkene ads);
}
