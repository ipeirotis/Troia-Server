package com.datascience.core.datastoring.kv;

import com.datascience.core.base.IData;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;

/**
 * User: konrad
 */
public class KVCleaner {

	public void cleanUp(KVData data){
		if (data instanceof KVNominalData){
			cleanUpNominalData((KVNominalData) data);
		} else {
			cleanUpNormalData(data);
		}
	}

	protected void cleanUpNominalData(KVNominalData data){

		cleanUpNormalData(data); // XXX FIXME not sure whether all should be before or after

	}

	protected <T> void cleanUpNormalData(KVData<T> data){
		for (LObject object: data.getObjects()){
			data.objectsAssigns.remove(object.getName());
		}
		for (Worker worker: data.getWorkers()){
			data.workersAssigns.remove(worker.getName());
		}
		data.objects.remove("");
		data.goldObjects.remove("");
		data.evaluationObjects.remove("");
		data.workers.remove("");
	}

	public <T> void cleanUp(KVResults results, IData<T> data){ // Java type system - you suck badly
		for (LObject object: data.getObjects()){
			results.datumKV.remove(object.getName());
		}
		for (Worker worker: data.getWorkers()){
			results.workersKV.remove(worker.getName());
		}
	}
}
