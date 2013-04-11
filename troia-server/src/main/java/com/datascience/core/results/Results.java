package com.datascience.core.results;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;

import java.util.HashMap;
import java.util.Map;

/**
 * User: artur
 * T - object class
 * U - datum results class
 * V - worker results class
 */
public class Results<T, U, V> extends AbstractResults<T, U, V>{

	protected Map<LObject<T>, U> datumResults;
	protected Map<Worker<T>, V> workerResults;

	public Results(ResultsFactory.DatumResultCreator datumCreator, ResultsFactory.WorkerResultCreator workerCreator){
		super(datumCreator, workerCreator);
		datumResults = new HashMap<LObject<T>, U>();
		workerResults = new HashMap<Worker<T>, V>();
	}

	public Map<Worker<T>, V> getWorkerResults(){
		return workerResults;
	}

	public Map<LObject<T>, U> getDatumResults(){
		return datumResults;
	}

	protected U uncheckedGetDatumResults(LObject<T> obj){
		return datumResults.get(obj);
	}

	protected V uncheckedGetWorkerResult(Worker<T> worker){
		return workerResults.get(worker);
	}

	public boolean hasDatumResult(LObject<T> obj){
		return datumResults.containsKey(obj);
	}

	public void addDatumResult(LObject<T> obj, U result){
		datumResults.put(obj, result);
		if (notifyEnabled) notifyNewObjectResults(obj, result);
	}


	public boolean hasWorkerResult(Worker<T> worker){
		return workerResults.containsKey(worker);
	}

	public void addWorkerResult(Worker<T> worker, V result){
		workerResults.put(worker, result);
		if (notifyEnabled) notifyNewWorkerResults(worker, result);
	}

}
