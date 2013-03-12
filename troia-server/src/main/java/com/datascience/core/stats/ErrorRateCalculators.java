package com.datascience.core.stats;

import com.datascience.core.stats.ConfusionMatrix;
import com.datascience.core.stats.IErrorRateCalculator;

/**
 * User: artur
 * Date: 3/12/13
 */
public class ErrorRateCalculators {

	public static class BatchErrorRateCalculator implements IErrorRateCalculator {

		@Override
		public double getErrorRate(ConfusionMatrix cm, String from, String to) {
			return cm.getErrorRateBatch(from, to);
		}
	}

	public static class IncrementalErrorRateCalculator implements IErrorRateCalculator{

		@Override
		public double getErrorRate(ConfusionMatrix cm, String from, String to) {
			return cm.getIncrementalErrorRate(from, to);
		}
	}

	public static class LaplacedNormializedErrorRateCalculator implements IErrorRateCalculator{

		@Override
		public double getErrorRate(ConfusionMatrix cm, String from, String to) {
			return cm.getLaplaceNormalizedErrorRate(from, to);
		}
	}

	public static class NormializedErrorRateCalculator implements IErrorRateCalculator{

		@Override
		public double getErrorRate(ConfusionMatrix cm, String from, String to) {
			return cm.getNormalizedErrorRate(from, to);
		}
	}
}
