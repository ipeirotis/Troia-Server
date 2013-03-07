package com.datascience.gal;

import java.util.Map;

/**
 * User: artur
 */
public class DatumResult {
	protected Map<String, Double> categoryProbabilites;

	public Map<String, Double> getCategoryProbabilites(){
		return categoryProbabilites;
	}

	public Double getCategoryProbability(String categoryName){
		return categoryProbabilites.get(categoryName);
	}

	public void setCategoryProbabilites(Map<String, Double> cp){
		categoryProbabilites = cp;
	}

}
