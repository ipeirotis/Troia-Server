package com.datascience.core.base;

import com.datascience.core.algorithms.INewDataObserver;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @Author: konrad
 */
public abstract class AbstractData<T> implements IData<T> {

	protected transient List<INewDataObserver<T>> newDataObservers;

	public AbstractData(){
		newDataObservers = new LinkedList<INewDataObserver<T>>();
	}

	@Override
	public Worker<T> getOrCreateWorker(String workerId){
		Worker<T> worker = getWorker(workerId);
		if (worker == null) {
			worker = new Worker<T>(workerId);
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

	abstract protected LObject<T> innerGetGoldObject(String objectId);

	@Override
	public LObject<T> getGoldObject(String objectId){
		LObject<T> object = innerGetGoldObject(objectId);
		checkArgument(object != null && object.isGold(),
				"There is no gold object with id = " + objectId);
		return object;
	}

	abstract protected LObject<T> innerGetEvaluationObject(String objectId);

	@Override
	public LObject<T> getEvaluationObject(String objectId){
		LObject<T> object = innerGetEvaluationObject(objectId);
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

	protected void notifyNewWorker(Worker<T> worker){
		for (INewDataObserver<T> updatableAlgorithm: newDataObservers) {
			updatableAlgorithm.newWorker(worker);
		}
	}
}
