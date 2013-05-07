package com.datascience.core.nominal;

import java.util.HashMap;
import java.util.Map;

/**
 * User: artur
 */
public class NominalModel {

	public NominalModel(){
		categoryPriors = new HashMap<String, Double>();
	}

	public Map<String, Double> categoryPriors;
}
