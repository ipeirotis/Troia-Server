package com.datascience.galc;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.IData;
import com.datascience.core.base.Project;
import com.datascience.core.results.DatumContResults;
import com.datascience.core.results.IResults;
import com.datascience.core.results.WorkerContResults;

/**
 * @Author: konrad
 */
public class ContinuousProject extends Project<ContValue, IData<ContValue>, DatumContResults, WorkerContResults> {

	public static final String kind = "CONTINUOUS";

	public ContinuousProject(ContinuousIpeirotis ci,
							 IData<ContValue> data,
							 IResults<ContValue, DatumContResults, WorkerContResults> results){
		super(ci, data, results);
		ci.setData(data);
		ci.setResults(results);
	}

	@Override
	public String getKind(){
		return kind;
	}
}
