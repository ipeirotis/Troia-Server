package com.datascience.gal.decision;

import java.util.Map;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.Datum;

/**
 * @author Konrad Kurdej
 */
public interface ILabelProbabilityDistributionCalculator {

	Map<String, Double> calculateDistribution(Datum datum, AbstractDawidSkene ads);
}
