package com.datascience.core.nominal;

import java.util.Map;

/**
 * User: artur
 * Date: 6/7/13
 */
public interface INominalModel {
	void setCategoryPriors(Map<String, Double> map);
	Map<String, Double> getCategoryPriors();
}
