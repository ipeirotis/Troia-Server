package com.datascience.core.nominal;

import java.util.Map;

/**
 * User: artur
 * Date: 4/9/13
 */
public interface ICategoryPriorCalculator {
	Double getPrior(INominalData data, INominalModel model, String categoryName);
	Map<String, Double> getPriors(INominalData data, INominalModel model);
	void initializeModelPriors(INominalData data, INominalModel model);
}
