package com.datascience.gal;

/**
 * User: artur
 */
public class WorkerResultBatch extends WorkerResult {

	@Override
	public double getErrorRate(String categoryFrom, String categoryTo) {
		return cm.getErrorRateBatch(categoryFrom, categoryTo);
	}

}
