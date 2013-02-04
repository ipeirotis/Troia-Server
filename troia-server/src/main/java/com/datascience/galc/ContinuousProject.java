package com.datascience.galc;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.Data;

/**
 * @Author: konrad
 */
public class ContinuousProject {

	protected Data<ContValue> data;

	public ContinuousProject(){
		data = new Data<ContValue>();
	}

	public Data<ContValue> getData(){
		return data;
	}

}
