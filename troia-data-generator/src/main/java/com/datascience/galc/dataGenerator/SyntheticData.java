package com.datascience.galc.dataGenerator;

import com.datascience.core.base.ContValue;
import com.datascience.core.datastoring.memory.InMemoryData;
import com.datascience.core.results.DatumContResults;
import com.datascience.core.results.WorkerContResults;
import java.util.HashSet;
import java.util.Set;

public class SyntheticData extends InMemoryData<ContValue> {
	
	private Set<WorkerContResults> workerContResults = new HashSet<WorkerContResults>();
	private Set<DatumContResults> objectContResults = new HashSet<DatumContResults>();

	public void addWorkerContResults(WorkerContResults wcr) {
		addWorker(wcr.getWorker());
		workerContResults.add(wcr);
	}
	
	public Set<WorkerContResults> getWorkerContResults() {
		return workerContResults;
	}
	
	public void addObjectContResults(DatumContResults dcr) {
		super.addObject(dcr.getObject());
		objectContResults.add(dcr);
	}
	
	public Set<DatumContResults> getObjectContResults() {
		return objectContResults;
	}
	
}
