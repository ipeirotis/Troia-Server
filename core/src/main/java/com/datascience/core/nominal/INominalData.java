package com.datascience.core.nominal;

import com.datascience.core.base.IData;
import com.datascience.utils.CostMatrix;

import java.util.Collection;
import java.util.Map;

/**
 * @Author: konrad
 */
public interface INominalData extends IData<String> {

	Collection<String> getCategories();
	boolean arePriorsFixed();
	double getCategoryPrior(String name);
	Map<String, Double> getCategoryPriors();
	CostMatrix<String> getCostMatrix();
	void initialize(Collection<String> categories,
					Collection<CategoryValue> priors, CostMatrix<String> costMatrix);
}
