package com.datascience.galc;

import java.util.Map;

import com.datascience.core.base.*;
import com.datascience.core.results.DatumContResults;
import com.datascience.core.results.Results;
import com.datascience.core.results.ResultsFactory;
import com.datascience.core.results.WorkerContResults;

/**
 * @Author: konrad
 */
public class ContinuousProject extends Project<ContValue, Data<ContValue>, DatumContResults, WorkerContResults> {

	public ContinuousProject(ContinuousIpeirotis ci){
		super(ci);
		results = new Results<ContValue, DatumContResults, WorkerContResults>(
				new ResultsFactory.DatumContResultFactory(),
				new ResultsFactory.WorkerContResultFactory());
		data = new Data<ContValue>();
		ci.setData(data);
		ci.setResults(results);
	}

	protected void checkComputed(){
		if (algorithm == null) {
			throw new IllegalStateException("Run compute first!");
		}
	}

	public Map<LObject<ContValue>, DatumContResults> getDataPrediction(){
		checkComputed();
		return ((ContinuousIpeirotis)algorithm).getObjectsResults();
	}

	public Map<Worker<ContValue>, WorkerContResults> getWorkerPrediction(){
		checkComputed();
		return ((ContinuousIpeirotis)algorithm).getWorkersResults();
	}


}
