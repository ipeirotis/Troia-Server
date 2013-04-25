package com.datascience.core.nominal;

import com.datascience.core.base.IData;
import com.datascience.utils.CostMatrix;

import java.util.Collection;
import java.util.Map;

/**
 * @Author: konrad
 */
public interface INominalData extends IData<String> {

	void setCategories(Collection<String> categories);
	Collection<String> getCategories();

	void setPriorFixed(boolean fixedPriors);
	boolean arePriorsFixed();

	double getCategoryPrior(String name);
	Map<String, Double> getCategoryPriors();
	void setCategoryPriors(Collection<CategoryValue> priors);

	CostMatrix<String> getCostMatrix();
	void setCostMatrix(CostMatrix<String> cm);

	void initialize(Collection<String> categories,
					Collection<CategoryValue> priors, CostMatrix<String> costMatrix);
}
