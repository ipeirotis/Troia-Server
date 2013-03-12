package com.datascience.core.stats;

import com.datascience.gal.ConfusionMatrix;

/**
 * @Author: konrad
 */
public interface IErrorRateCalculator {

	double getErrorRate(ConfusionMatrix cm, String from, String to);
}
