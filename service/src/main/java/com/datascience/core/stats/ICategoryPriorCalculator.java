package com.datascience.core.stats;

import com.datascience.core.nominal.INominalData;
import com.datascience.core.nominal.NominalModel;

/**
 * User: artur
 * Date: 4/9/13
 */
public interface ICategoryPriorCalculator {
	Double getPrior(INominalData data, NominalModel model, String categoryName);
	void initializeModelPriors(INominalData data, NominalModel model);
}
