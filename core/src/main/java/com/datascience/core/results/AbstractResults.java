package com.datascience.core.results;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @Author: konrad & Artur
 * T - object class
 * U - datum results class
 * V - worker results class
 */
public abstract class AbstractResults<T, U, V> implements IResults<T, U, V>{

	protected transient List<INewResultsListener<T, U, V>> newResultsListeners;
	protected transient boolean notifyEnabled;

	protected ResultsFactory.DatumResultCreator<U> datumCreator;
	protected ResultsFactory.WorkerResultCreator<V> workerCreator;

	public AbstractResults(ResultsFactory.DatumResultCreator<U> datumCreator, ResultsFactory.WorkerResultCreator<V> workerCreator){
		this.datumCreator = datumCreator;
		this.workerCreator = workerCreator;
		newResultsListeners = new LinkedList<INewResultsListener<T, U, V>>();
		notifyEnabled = false;
	}

	abstract protected U uncheckedGetDatumResults(LObject<T> obj);
	abstract protected V uncheckedGetWorkerResult(Worker worker);

	public ResultsFactory.WorkerResultCreator<V> getWorkerResultsCreator(){
		return workerCreator;
	}

	@Override
	public Map<Worker, V> getWorkerResults(Collection<Worker> workers){
		Map<Worker, V> results = new HashMap<Worker, V>(workers.size());
		for (Worker worker: workers) {
			results.put(worker, uncheckedGetWorkerResult(worker));
		}
		return results;
	}

	@Override
	public Map<LObject<T>, U> getDatumResults(Collection<LObject<T>> objects){
		Map<LObject<T>, U> results = new HashMap<LObject<T>, U>(objects.size());
		for (LObject<T> object: objects){
			results.put(object, uncheckedGetDatumResults(object));
		}
		return results;
	}

	@Override
	public U getOrCreateDatumResult(LObject<T> obj){
		U ret = uncheckedGetDatumResults(obj);
		if (ret == null){
			ret = datumCreator.create();
		}
		return ret;
	}

	@Override
	public U getDatumResult(LObject<T> obj){
		U ret = uncheckedGetDatumResults(obj);
		checkArgument(ret != null,
				"You have not run compute or there is no object named " + obj.getName());
		return ret;
	}

	@Override
	public V getOrCreateWorkerResult(Worker wor){
		V ret = uncheckedGetWorkerResult(wor);
		if (ret == null){
			ret = workerCreator.create();
		}
		return ret;
	}

	@Override
	public V createEmptyWorkerResult(Worker wor){
		return workerCreator.create();
	}

	@Override
	public V getWorkerResult(Worker worker){
		V ret = uncheckedGetWorkerResult(worker);
		checkArgument(ret != null,
				"You have not run compute or there is no worker named " + worker.getName());
		return ret;
	}

	@Override
	public void addNewResultsListener(INewResultsListener<T, U, V> newResultsListener){
		newResultsListeners.add(newResultsListener);
		notifyEnabled = true;
	}

	@Override
	public void setNotifyEnabled(boolean enabled){
		notifyEnabled = enabled;
	}

	@Override
	public boolean isNotifyEnabled(){
		return notifyEnabled;
	}

	protected void notifyNewWorkerResults(Worker worker, V result){
		if (!notifyEnabled) return;
		for (INewResultsListener<T, U, V> newResultsListener: newResultsListeners){
			newResultsListener.newResultsForWorker(worker, result);
		}
	}

	protected void notifyNewObjectResults(LObject<T> object, U result){
		if (!notifyEnabled) return;
		for (INewResultsListener<T, U, V> newResultsListener: newResultsListeners){
			newResultsListener.newResultsForObject(object, result);
		}
	}

	@Override
	public void notifyAllNewResults(){
		if (!notifyEnabled) return;
		for (INewResultsListener<T, U, V> newResultsListener: newResultsListeners){
			newResultsListener.newResultsForAll();
		}
	}
}
