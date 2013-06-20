package com.datascience.core.stats;

import com.datascience.core.base.Worker;
import com.datascience.core.nominal.NominalProject;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.Map;

/**
 * @Author: konrad
 */
public class QSPCalculators {

	public static class Linear extends QualitySensitivePaymentsCalculator {

		public Linear(NominalProject project, Worker worker){
			super(project, worker);
		}

		@Override
		public Double getWorkerWage(double qualifiedWage, double costThreshold, Map<String, Double> priors) {
			int m = 0;
			double cost;
			do {
				m++;
				cost = getWorkerCost(m, priors, 1000);
				// If the worker is worth less than 1/100 of a qualified worker, we return a 0 payment.
				if (m > 40) return 0.0;
			} while (cost > costThreshold);

			return qualifiedWage / m;
		}
	}

	public static class RegressionBased extends QualitySensitivePaymentsCalculator {

		public RegressionBased(NominalProject project, Worker worker){
			super(project, worker);
		}

		@Override
		public Double getWorkerWage(double qualifiedWage, double costThreshold, Map<String, Double> priors) {
			SimpleRegression regression = new SimpleRegression();

			for (int m = 1; m <= 41; m += 4) {

				double cost = getWorkerCost(m, priors, 1000);
				if (cost == 0) break;
				regression.addData(Math.log(cost), m);
			}

			double d = regression.predict(Math.log(costThreshold));
			return qualifiedWage / d;

		}
	}
}
