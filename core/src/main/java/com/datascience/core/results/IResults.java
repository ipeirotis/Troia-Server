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

	Map<Worker<T>, V> getWorkerResults(Collection<Worker<T>> workers);
	Map<LObject<T>, U> getDatumResults(Collection<LObject<T>> objects);

	U getOrCreateDatumResult(LObject<T> obj);
	U getDatumResult(LObject<T> obj);
	boolean hasDatumResult(LObject<T> obj);
	void addDatumResult(LObject<T> obj, U result);

	V getOrCreateWorkerResult(Worker<T> wor);
	V createEmptyWorkerResult(Worker<T> wor);
	V getWorkerResult(Worker<T> worker);
	boolean hasWorkerResult(Worker<T> worker);
	void addWorkerResult(Worker<T> worker, V result);

	void setNotifyEnabled(boolean enabled);
	boolean isNotifyEnabled();
	void addNewResultsListener(INewResultsListener<T, U, V> newResultsListener);
	void notifyAllNewResults();
}
