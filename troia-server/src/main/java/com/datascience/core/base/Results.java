package com.datascience.core.base;

import java.util.HashMap;
import java.util.Map;

/**
 * User: artur
 */
public class Results<T, U, V> {

	protected Map<LObject<T>, U> datumResults;
	protected Map<Worker<T>, V> workerResults;
	protected ResultsFactory.IDatumResultCreator creator;

	public Results(ResultsFactory.IDatumResultCreator creator){
		this.creator = creator;
		datumResults = new HashMap<LObject<T>, U>();
		workerResults = new HashMap<Worker<T>, V>();
	}

	public Map<Worker<T>, V> getWorkerResults(){
		return workerResults;
	}

	public Map<LObject<T>, U> getDatumResults(){
		return datumResults;
	}

	public U getOrCreateDatumResult(LObject<T> obj){
		U ret = datumResults.get(obj);
		if (ret == null)
			ret = (U) creator.create(obj);
			datumResults.put(obj, ret);
		return ret;

	}

	public U getDatumResult(LObject<T> obj){
		U ret = datumResults.get(obj);
		if (ret == null)
			throw new IllegalArgumentException("You have not run compute or there is no object named " + obj.getName());
		return ret;
	}

	public void addDatumResult(LObject<T> obj, U result){
		datumResults.put(obj, result);
	}

	public V getWorkerResult(Worker<T> worker){
		V ret = workerResults.get(worker);
		if (ret == null)
			throw new IllegalArgumentException("You have not run compute or there is no worker named " + worker.getName());
		return ret;
	}

	public void addWorkerResult(Worker<T> worker, V result){
		workerResults.put(worker, result);
	}
}
