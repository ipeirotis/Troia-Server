package com.datascience.datastoring.datamodels.memory;

import com.datascience.core.nominal.INominalModel;

import java.util.HashMap;
import java.util.Map;

/**
 * User: artur
 */
public class NominalModel implements INominalModel{

	private Map<String, Double> categoryPriors;

	public NominalModel(){
		categoryPriors = new HashMap<String, Double>();
	}

	@Override
	public void setCategoryPriors(Map<String, Double> map){
		categoryPriors = map;
	}

	@Override
	public Map<String, Double> getCategoryPriors(){
		return categoryPriors;
	}
}
