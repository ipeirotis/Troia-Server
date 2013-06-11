package com.datascience.core.base;

import com.datascience.core.algorithms.INewDataObserver;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @Author: konrad
 */
public abstract class AbstractData<T> implements IData<T> {

	static protected final int WORKER_NAME_LIMIT = 100;
	static protected final int OBJECT_NAME_LIMIT = 100;

	protected transient List<INewDataObserver<T>> newDataObservers;

	public AbstractData(){
		newDataObservers = new LinkedList<INewDataObserver<T>>();
	}

	@Override
	public Worker getOrCreateWorker(String workerId){
		Worker worker = getWorker(workerId);
		if (worker == null) {
			worker = new Worker(workerId);
		}
		return worker;
	}

	@Override
	public LObject<T> getOrCreateObject(String objectId){
		LObject<T> object = getObject(objectId);
		if (object == null) {
			object = new LObject<T>(objectId);
		}
		return object;
	}

	@Override
	public void addWorker(Worker worker) {
		checkArgument(worker.getName().length() < WORKER_NAME_LIMIT,
				"Worker name should be shorter than " + WORKER_NAME_LIMIT + " chars");
		uncheckedAddWorker(worker);
	}

	abstract protected void uncheckedAddWorker(Worker worker);

	@Override
	public void addObject(LObject<T> object) {
		checkArgument(object.getName().length() < OBJECT_NAME_LIMIT,
				"Object name should be shorter than " + OBJECT_NAME_LIMIT + " chars");
		uncheckedAddObject(object);
	}

	abstract protected void uncheckedAddObject(LObject<T> object);

	abstract protected LObject<T> uncheckedGetGoldObject(String objectId);

	@Override
	public LObject<T> getGoldObject(String objectId){
		LObject<T> object = uncheckedGetGoldObject(objectId);
		checkArgument(object != null && object.isGold(),
				"There is no gold object with id = " + objectId);
		return object;
	}

	abstract protected LObject<T> uncheckedGetEvaluationObject(String objectId);

	@Override
	public LObject<T> getEvaluationObject(String objectId){
		LObject<T> object = uncheckedGetEvaluationObject(objectId);
		checkArgument(object != null && object.isEvaluation(),
				"There is no evaluation object with id = " + objectId);
		return object;
	}

	@Override
	public void addNewUpdatableAlgorithm(INewDataObserver<T> updatableAlgorithm){
		newDataObservers.add(updatableAlgorithm);
	}

	protected void notifyNewAssign(AssignedLabel<T> assign){
		for (INewDataObserver<T> updatableAlgorithm: newDataObservers) {
			updatableAlgorithm.newAssign(assign);
		}
	}

	protected void notifyNewGoldObject(LObject<T> object){
		for (INewDataObserver<T> updatableAlgorithm: newDataObservers) {
			updatableAlgorithm.newGoldObject(object);
		}
	}

	protected void notifyNewObject(LObject<T> object){
		for (INewDataObserver<T> updatableAlgorithm: newDataObservers) {
			updatableAlgorithm.newObject(object);
		}
	}

	protected void notifyNewWorker(Worker worker){
		for (INewDataObserver<T> updatableAlgorithm: newDataObservers) {
			updatableAlgorithm.newWorker(worker);
		}
	}

	@Override
	public Collection<AssignedLabel<T>> getWorkerAssigns(Worker worker) {
		Collection<AssignedLabel<T>> ret = uncheckedGetWorkerAssigns(worker);
		if (ret == null)
			return new LinkedList<AssignedLabel<T>>();
		return ret;
	}

	public abstract Collection<AssignedLabel<T>> uncheckedGetWorkerAssigns(Worker worker);

	@Override
	public Collection<AssignedLabel<T>> getAssignsForObject(LObject<T> lObject) {
		Collection<AssignedLabel<T>> ret = uncheckedGetAssignsForObject(lObject);
		if (ret == null)
			return new LinkedList<AssignedLabel<T>>();
		return ret;
	}

	public abstract Collection<AssignedLabel<T>> uncheckedGetAssignsForObject(LObject<T> lObject);

}
