package com.datascience.core.results;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * User: artur
 * T - object class
 * U - datum results class
 * V - worker results class
 */
public class Results<T, U, V> {

	protected Map<LObject<T>, U> datumResults;
	protected Map<Worker<T>, V> workerResults;
	protected ResultsFactory.DatumResultCreator datumCreator;
	protected ResultsFactory.WorkerResultCreator workerCreator;

	protected transient List<INewResultsListener<T, U, V>> newResultsListeners;
	protected transient boolean notifyEnabled;

	public Results(ResultsFactory.DatumResultCreator datumCreator, ResultsFactory.WorkerResultCreator workerCreator){
		this.datumCreator = datumCreator;
		this.workerCreator = workerCreator;
		datumResults = new HashMap<LObject<T>, U>();
		workerResults = new HashMap<Worker<T>, V>();

		newResultsListeners = new LinkedList<INewResultsListener<T, U, V>>();
		notifyEnabled = false;
	}

	public ResultsFactory.DatumResultCreator getDatumCreator(){
		return datumCreator;
	}

	public ResultsFactory.WorkerResultCreator getWorkerCreator(){
		return workerCreator;
	}

	public Map<Worker<T>, V> getWorkerResults(){
		return workerResults;
	}

	public Map<LObject<T>, U> getDatumResults(){
		return datumResults;
	}

	public U getOrCreateDatumResult(LObject<T> obj){
		U ret = datumResults.get(obj);
		if (ret == null){
			ret = (U) datumCreator.create(obj);
			datumResults.put(obj, ret);
		}
		return ret;

	}

	public U getDatumResult(LObject<T> obj){
		U ret = datumResults.get(obj);
		if (ret == null)
			throw new IllegalArgumentException("You have not run compute or there is no object named " + obj.getName());
		return ret;
	}

	public boolean hasDatumResult(LObject<T> obj){
		return datumResults.containsKey(obj);
	}

	public void addDatumResult(LObject<T> obj, U result){
		datumResults.put(obj, result);
		if (notifyEnabled) notifyNewObjectResults(obj, result);
	}

	public V getOrCreateWorkerResult(Worker<T> wor){
		V ret = workerResults.get(wor);
		if (ret == null){
			ret = (V) workerCreator.create(wor);
			workerResults.put(wor, ret);
		}
		return ret;
	}

	public V getWorkerResult(Worker<T> worker){
		V ret = workerResults.get(worker);
		if (ret == null)
			throw new IllegalArgumentException("You have not run compute or there is no worker named " + worker.getName());
		return ret;
	}

	public boolean hasWorkerResult(Worker<T> worker){
		return workerResults.containsKey(worker);
	}

	public void addWorkerResult(Worker<T> worker, V result){
		workerResults.put(worker, result);
		if (notifyEnabled) notifyNewWorkerResults(worker, result);
	}

	public void addNewResultsListener(INewResultsListener<T, U, V> newResultsListener){
		newResultsListeners.add(newResultsListener);
		notifyEnabled = true;
	}

	public void setNotifyEnabled(boolean enabled){
		notifyEnabled = enabled;
	}

	public boolean isNotifyEnabled(){
		return notifyEnabled;
	}

	protected void notifyNewWorkerResults(Worker<T> worker, V result){
		for (INewResultsListener<T, U, V> newResultsListener: newResultsListeners){
			newResultsListener.newResultsForWorker(worker, result);
		}
	}

	protected void notifyNewObjectResults(LObject<T> object, U result){
		for (INewResultsListener<T, U, V> newResultsListener: newResultsListeners){
			newResultsListener.newResultsForObject(object, result);
		}
	}

	public void notifyAllNewResults(){
		if (!notifyEnabled) return;
		for (INewResultsListener<T, U, V> newResultsListener: newResultsListeners){
			newResultsListener.newResultsForAll();
		}
	}
}
