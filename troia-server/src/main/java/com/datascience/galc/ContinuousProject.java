package com.datascience.galc;

import com.datascience.core.base.Data;

/**
 * @Author: konrad
 */
public class ContinuousProject {

	protected Data<Double> data;

	public ContinuousProject(){
		data = new Data<Double>();
	}

	public Data<Double> getData(){
		return data;
	}

}
