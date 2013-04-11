package com.datascience.galc;

import java.util.Map;

import com.datascience.core.base.*;
import com.datascience.core.datastoring.memory.InMemoryData;
import com.datascience.core.datastoring.memory.InMemoryResults;
import com.datascience.core.results.DatumContResults;
import com.datascience.core.results.ResultsFactory;
import com.datascience.core.results.WorkerContResults;

/**
 * @Author: konrad
 */
public class ContinuousProject extends Project<ContValue, IData<ContValue>, DatumContResults, WorkerContResults> {

	public ContinuousProject(ContinuousIpeirotis ci){
		super(ci);
		results = new InMemoryResults<ContValue, DatumContResults, WorkerContResults>(
				new ResultsFactory.DatumContResultFactory(),
				new ResultsFactory.WorkerContResultFactory());
		data = new InMemoryData<ContValue>();
		// TODO XXX FIXME  ^^^^ results and data needs to be reworked - passed from upstairs
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

	@Override
	public String getKind(){
		return "CONTINUOUS";
	}
}
