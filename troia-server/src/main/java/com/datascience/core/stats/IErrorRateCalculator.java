package com.datascience.core.stats;

/**
 * @Author: konrad
 */
public interface IErrorRateCalculator {

	double getErrorRate(ConfusionMatrix cm, String from, String to);
}
