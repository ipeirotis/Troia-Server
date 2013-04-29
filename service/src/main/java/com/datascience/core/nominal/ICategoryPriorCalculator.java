package com.datascience.core.nominal;

/**
 * User: artur
 * Date: 4/9/13
 */
public interface ICategoryPriorCalculator {
	Double getPrior(INominalData data, NominalModel model, String categoryName);
	void initializeModelPriors(INominalData data, NominalModel model);
}
