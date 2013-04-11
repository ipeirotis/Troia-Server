package com.datascience.core.datastoring.memory;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.results.AbstractResults;
import com.datascience.core.results.ResultsFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * User: artur
 * T - object class
 * U - datum results class
 * V - worker results class
 */
public class InMemoryResults<T, U, V> extends AbstractResults<T, U, V> {

	protected Map<LObject<T>, U> datumResults;
	protected Map<Worker<T>, V> workerResults;

	public InMemoryResults(ResultsFactory.DatumResultCreator datumCreator, ResultsFactory.WorkerResultCreator workerCreator){
		super(datumCreator, workerCreator);
		datumResults = new HashMap<LObject<T>, U>();
		workerResults = new HashMap<Worker<T>, V>();
	}

	@Override
	protected U uncheckedGetDatumResults(LObject<T> obj){
		return datumResults.get(obj);
	}

	@Override
	protected V uncheckedGetWorkerResult(Worker<T> worker){
		return workerResults.get(worker);
	}

	@Override
	public boolean hasDatumResult(LObject<T> obj){
		return datumResults.containsKey(obj);
	}

	@Override
	public void addDatumResult(LObject<T> obj, U result){
		datumResults.put(obj, result);
		notifyNewObjectResults(obj, result);
	}

	@Override
	public boolean hasWorkerResult(Worker<T> worker){
		return workerResults.containsKey(worker);
	}

	@Override
	public void addWorkerResult(Worker<T> worker, V result){
		workerResults.put(worker, result);
		notifyNewWorkerResults(worker, result);
	}

}
