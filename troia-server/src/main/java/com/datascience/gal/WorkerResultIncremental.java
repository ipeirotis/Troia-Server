package com.datascience.gal;

/**
 * User: artur
 */
public class WorkerResultIncremental extends  WorkerResult {

	@Override
	public double getErrorRate(String categoryFrom, String categoryTo){
		return cm.getNormalizedErrorRate(categoryFrom, categoryTo);
	}
}
