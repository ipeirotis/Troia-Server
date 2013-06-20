package com.datascience.core.results;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;

import java.util.Collection;
import java.util.Map;

/**
 * @Author: konrad
 * T - object class
 * U - datum results class
 * V - worker results class
 */
public interface IResults<T, U, V> {

	Map<Worker, V> getWorkerResults(Collection<Worker> workers);
	Map<LObject<T>, U> getDatumResults(Collection<LObject<T>> objects);

	U getOrCreateDatumResult(LObject<T> obj);
	U uncheckedGetDatumResults(LObject<T> obj);
	U getDatumResult(LObject<T> obj);
	boolean hasDatumResult(LObject<T> obj);
	void addDatumResult(LObject<T> obj, U result);

	V getOrCreateWorkerResult(Worker wor);
	V uncheckedGetWorkerResult(Worker worker);
	V createEmptyWorkerResult(Worker wor);
	V getWorkerResult(Worker worker);
	boolean hasWorkerResult(Worker worker);
	void addWorkerResult(Worker worker, V result);

	void setNotifyEnabled(boolean enabled);
	boolean isNotifyEnabled();
	void addNewResultsListener(INewResultsListener<T, U, V> newResultsListener);
	void notifyAllNewResults();
}
