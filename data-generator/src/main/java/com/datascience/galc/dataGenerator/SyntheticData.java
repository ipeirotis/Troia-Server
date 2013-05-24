package com.datascience.galc.dataGenerator;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.datastoring.datamodels.memory.InMemoryData;
import com.datascience.core.results.DatumContResults;
import com.datascience.core.results.WorkerContResults;

import java.util.HashMap;
import java.util.Map;

public class SyntheticData extends InMemoryData<ContValue> {
	
	private Map<Worker<ContValue>, WorkerContResults> workerContResults = new HashMap<Worker<ContValue>, WorkerContResults>();
	private Map<LObject<ContValue>, DatumContResults> objectContResults = new HashMap<LObject<ContValue>, DatumContResults>();


	public void addWorkerResult(Worker<ContValue> worker, WorkerContResults wcr){
		workerContResults.put(worker, wcr);
	}

	public Map<Worker<ContValue>, WorkerContResults> getWorkerContResults() {
		return workerContResults;
	}

	public void addObjectResult(LObject<ContValue> object, DatumContResults dcr){
		objectContResults.put(object, dcr);
	}
	
	public Map<LObject<ContValue>, DatumContResults> getObjectContResults() {
		return objectContResults;
	}
	
}
