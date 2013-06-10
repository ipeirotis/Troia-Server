package com.datascience.core.nominal;

/**
 * User: artur
 * Date: 4/9/13
 */
public interface ICategoryPriorCalculator {
	Double getPrior(INominalData data, INominalModel model, String categoryName);
	void initializeModelPriors(INominalData data, INominalModel model);
}
