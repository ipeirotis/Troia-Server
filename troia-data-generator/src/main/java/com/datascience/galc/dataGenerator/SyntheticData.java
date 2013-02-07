package com.datascience.galc.dataGenerator;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.Data;
import com.datascience.galc.DatumContResults;
import com.datascience.galc.WorkerContResults;
import java.util.HashSet;
import java.util.Set;

public class SyntheticData extends Data<ContValue> {
	
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
