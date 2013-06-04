package com.datascience.core.base;

import com.datascience.core.algorithms.INewDataObserver;

import java.util.Collection;

/**
 * @Author: konrad
 */
public interface IData<T> {

	void addWorker(Worker worker);
	Worker getWorker(String workerId);
	Worker getOrCreateWorker(String workerId);
	Collection<Worker> getWorkers();

	/** Should check whether object is gold or evaluation one */
	void addObject(LObject<T> object);
	LObject<T> getObject(String objectId);
	LObject<T> getOrCreateObject(String objectId);
	Collection<LObject<T>> getObjects();

	LObject<T> getGoldObject(String objectId);
	Collection<LObject<T>> getGoldObjects();

	void markObjectAsGold(LObject<T> object, T label);

	LObject<T> getEvaluationObject(String objectId);
	Collection<LObject<T>> getEvaluationObjects();

	void addAssign(AssignedLabel<T> assign);
	Collection<AssignedLabel<T>> getAssigns();

	/** This assumes that assigns are compared only on object and worker */
	boolean hasAssign(LObject<T> object, Worker worker);
	Collection<AssignedLabel<T>> getWorkerAssigns(Worker worker);

	Collection<AssignedLabel<T>> getAssignsForObject(LObject<T> lObject);

	void addNewUpdatableAlgorithm(INewDataObserver<T> updatableAlgorithm);

}
