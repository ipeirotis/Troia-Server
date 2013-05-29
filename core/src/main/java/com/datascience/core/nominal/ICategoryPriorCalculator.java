package com.datascience.core.nominal;

import java.util.Map;

/**
 * User: artur
 * Date: 4/9/13
 */
public interface ICategoryPriorCalculator {
	Double getPrior(INominalData data, NominalModel model, String categoryName);
	Map<String, Double> getPriors(INominalData data, NominalModel model);
	void initializeModelPriors(INominalData data, NominalModel model);
}
