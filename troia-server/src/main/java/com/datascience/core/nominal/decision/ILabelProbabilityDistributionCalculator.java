package com.datascience.core.nominal.decision;

import java.util.Map;

import com.datascience.core.base.LObject;
import com.datascience.core.nominal.NominalProject;

/**
 * @author Konrad Kurdej
 */
public interface ILabelProbabilityDistributionCalculator {

	Map<String, Double> calculateDistribution(LObject<String> datum, NominalProject project);
}
