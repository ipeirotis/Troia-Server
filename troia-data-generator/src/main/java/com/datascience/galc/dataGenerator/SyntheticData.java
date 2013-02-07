package com.datascience.galc.dataGenerator;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.galc.DatumContResults;
import com.datascience.galc.WorkerContResults;
import java.util.HashSet;
import java.util.Set;

public class SyntheticData extends Data<ContValue> {
	
	private Set<WorkerContResults> workerContResults = new HashSet<WorkerContResults>();
	private Set<DatumContResults> objectContResults = new HashSet<DatumContResults>();

	@Override
	public void addWorker(Worker<ContValue> worker) {
		super.addWorker(worker);
		workerContResults.add(new WorkerContResults(worker));
	}
	
	public Set<WorkerContResults> getWorkerContResults() {
		return workerContResults;
	}
	
	@Override
	public void addObject(LObject<ContValue> lObject) {
		super.addObject(lObject);
		objectContResults.add(new DatumContResults(lObject));
	}
	
	public Set<DatumContResults> getObjectContResults() {
		return objectContResults;
	}
	
}
