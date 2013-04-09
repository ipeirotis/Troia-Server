package com.datascience.core.stats;

import com.datascience.core.nominal.NominalData;
import com.datascience.core.nominal.NominalModel;

/**
 * User: artur
 * Date: 4/9/13
 */
public interface ICategoryPriorCalculator {
	Double getPrior(NominalData data, NominalModel model, String categoryName);
	void initializeModelPriors(NominalData data, NominalModel model);
}
