package com.datascience.core.datastoring.kv;

import com.datascience.core.base.AbstractData;
import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.utils.storage.ISafeKVStorage;

import java.util.Collection;

/**
 * @Author: konrad
 */
public class KVData<T> extends AbstractData<T> {

	protected ISafeKVStorage<Collection<AssignedLabel<T>>> workersAssigns;
	protected ISafeKVStorage<Collection<AssignedLabel<T>>> objectsAssigns;

	protected ISafeKVStorage<Collection<LObject<T>>> objects;// SINGLE ROW!
	protected ISafeKVStorage<Collection<LObject<T>>> goldObjects;// SINGLE ROW
	protected ISafeKVStorage<Collection<LObject<T>>> evaluationObjects;// SINGLE ROW
	protected ISafeKVStorage<Collection<Worker<T>>> workers; //SINGLE ROW!


	@Override
	protected LObject<T> uncheckedGetGoldObject(String objectId) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	protected LObject<T> uncheckedGetEvaluationObject(String objectId) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void addWorker(Worker<T> worker) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Worker<T> getWorker(String workerId) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Collection<Worker<T>> getWorkers() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void addObject(LObject<T> object) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public LObject<T> getObject(String objectId) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Collection<LObject<T>> getObjects() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Collection<LObject<T>> getGoldObjects() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void markObjectAsGold(LObject<T> object, T label) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Collection<LObject<T>> getEvaluationObjects() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void addAssign(AssignedLabel<T> assign) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Collection<AssignedLabel<T>> getAssigns() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean hasAssign(LObject<T> object, Worker<T> worker) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Collection<AssignedLabel<T>> getWorkerAssigns(Worker<T> worker) {
		return workersAssigns.get(worker.getName());
	}

	@Override
	public Collection<AssignedLabel<T>> getAssignsForObject(LObject<T> lObject) {
		return objectsAssigns.get(lObject.getName());
	}
}
