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

	protected ResultsFactory.DatumResultCreator<T, U> datumCreator;
	protected ResultsFactory.WorkerResultCreator<T, V> workerCreator;

	public AbstractResults(ResultsFactory.DatumResultCreator<T, U> datumCreator, ResultsFactory.WorkerResultCreator<T, V> workerCreator){
		this.datumCreator = datumCreator;
		this.workerCreator = workerCreator;
		newResultsListeners = new LinkedList<INewResultsListener<T, U, V>>();
		notifyEnabled = false;
	}

	abstract protected U uncheckedGetDatumResults(LObject<T> obj);
	abstract protected V uncheckedGetWorkerResult(Worker<T> worker);

	@Override
	public Map<Worker<T>, V> getWorkerResults(Collection<Worker<T>> workers){
		Map<Worker<T>, V> results = new HashMap<Worker<T>, V>(workers.size());
		for (Worker<T> worker: workers) {
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
	public V getOrCreateWorkerResult(Worker<T> wor){
		V ret = uncheckedGetWorkerResult(wor);
		if (ret == null){
			ret = workerCreator.create(wor);
		}
		return ret;
	}

	@Override
	public V getWorkerResult(Worker<T> worker){
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

	protected void notifyNewWorkerResults(Worker<T> worker, V result){
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
