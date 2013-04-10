package com.datascience.core.base;

import com.datascience.core.algorithms.INewDataObserver;

import java.util.*;

/**
 * @Author: konrad
 */
public abstract class AbstractData<T> implements IData<T> {

	protected transient List<INewDataObserver<T>> newDataObservers;

	public AbstractData(){
		newDataObservers = new LinkedList<INewDataObserver<T>>();
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
