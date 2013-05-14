package com.datascience.galc;

import com.datascience.core.results.DatumContResults;

public class DatumContPrediction {

	String object;
	DatumContResults prediction;

	public DatumContPrediction(String object, DatumContResults result){
		this.object = object;
		prediction = result;
	}
}
