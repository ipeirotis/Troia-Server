package com.datascience.core.datastoring.kv;

import com.datascience.core.base.AbstractData;
import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.utils.storage.ISafeKVStorage;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

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

	public KVData(ISafeKVStorage<Collection<AssignedLabel<T>>> workersAssigns,
				  ISafeKVStorage<Collection<AssignedLabel<T>>> objectsAssigns,
				  ISafeKVStorage<Collection<LObject<T>>> objects,
				  ISafeKVStorage<Collection<LObject<T>>> goldObjects,
				  ISafeKVStorage<Collection<LObject<T>>> evaluationObjects,
				  ISafeKVStorage<Collection<Worker<T>>> workers){
		this.workersAssigns = workersAssigns;
		this.objectsAssigns = objectsAssigns;
		this.objects = objects;
		this.goldObjects = goldObjects;
		this.evaluationObjects = evaluationObjects;
		this.workers = workers;
		initializeEmptyRows(Arrays.asList(new ISafeKVStorage[]{objects, goldObjects, evaluationObjects, workers}));
	}

	protected void initializeEmptyRows(Collection<ISafeKVStorage> storages){
		for (ISafeKVStorage storage : storages){
			if (!storage.contains("")){
				storage.put("", new LinkedList());
			}
		}
	}

	@Override
	protected LObject<T> uncheckedGetGoldObject(String objectId) {
		return getObject(objectId);
	}

	@Override
	protected LObject<T> uncheckedGetEvaluationObject(String objectId) {
		return getObject(objectId);
	}

	@Override
	public void addWorker(Worker<T> worker) {
		Collection<Worker<T>> oldWorkers = workers.get("");
		if (!oldWorkers.contains(worker)){
			oldWorkers.add(worker);
			workers.put("", oldWorkers);
			notifyNewWorker(worker);
			workersAssigns.put(worker.getName(), new LinkedList());
		}
	}

	@Override
	public Worker<T> getWorker(String workerId) {
		for (Worker<T> w : workers.get(""))
			if (w.getName().equals(workerId))
				return w;
		return null;
	}

	@Override
	public Collection<Worker<T>> getWorkers() {
		return workers.get("");
	}

	@Override
	public void addObject(LObject<T> object) {
		Collection<LObject<T>> oldObjects = objects.get("");
		if (!oldObjects.contains(object)){
			oldObjects.add(object);
			objects.put("", oldObjects);
			notifyNewObject(object);
			objectsAssigns.put(object.getName(), new LinkedList());
			if (object.isGold())
				addGoldObject(object);
			if (object.isEvaluation())
				addEvaluationObject(object);
		} else {
			LObject<T> oldObject = getObject(object.getName());
			if (object.isGold() && !oldObject.isGold()) {
				oldObject.setGoldLabel(object.getGoldLabel());
				addGoldObject(oldObject);
			}
			if (object.isEvaluation() && !oldObject.isEvaluation()) {
				oldObject.setEvaluationLabel(object.getEvaluationLabel());
				addEvaluationObject(oldObject);
			}
		}
	}

	private void addGoldObject(LObject<T> object){
		//assumption: goldObjects collection doesnt contain object
		Collection<LObject<T>> oldGoldObjects = goldObjects.get("");
		oldGoldObjects.add(object);
		goldObjects.put("", oldGoldObjects);
		notifyNewGoldObject(object);
	}

	private void addEvaluationObject(LObject<T> object){
		//assumption: evaluationObjects collection doesnt contain object
		Collection<LObject<T>> oldEvaluationObjects = evaluationObjects.get("");
		oldEvaluationObjects.add(object);
		evaluationObjects.put("", oldEvaluationObjects);
	}

	@Override
	public LObject<T> getObject(String objectId) {
		for (LObject<T> o : objects.get(""))
			if (o.getName().equals(objectId))
				return  o;
		return null;
	}

	@Override
	public Collection<LObject<T>> getObjects() {
		return objects.get("");
	}

	@Override
	public Collection<LObject<T>> getGoldObjects() {
		return goldObjects.get("");
	}

	@Override
	public void markObjectAsGold(LObject<T> object, T label) {
		object.setGoldLabel(label);
		Collection<LObject<T>> oldObjects = objects.get("");
		oldObjects.add(object);
		objects.put("", oldObjects);

		oldObjects = goldObjects.get("");
		oldObjects.add(object);
		goldObjects.put("", oldObjects);
	}

	@Override
	public Collection<LObject<T>> getEvaluationObjects() {
		return evaluationObjects.get("");
	}

	@Override
	public void addAssign(AssignedLabel<T> assign) {
		addWorker(assign.getWorker());
		forceAddAssign(assign, workersAssigns, assign.getWorker().getName());
		addObject(assign.getLobject());
		forceAddAssign(assign, objectsAssigns, assign.getLobject().getName());
		notifyNewAssign(assign);
	}

	private void forceAddAssign(AssignedLabel<T> assign, ISafeKVStorage<Collection<AssignedLabel<T>>> storage, String key){
		Collection<AssignedLabel<T>> assigns = storage.get(key);
		if (!assigns.add(assign)) {
			assigns.remove(assign);
			assigns.add(assign);
		}
		storage.put(key, assigns);
	}

	@Override
	public Collection<AssignedLabel<T>> getAssigns() {
		Collection<AssignedLabel<T>> ret = new LinkedList<AssignedLabel<T>>();
		for (Worker<T> w : getWorkers()){
			ret.addAll(workersAssigns.get(w.getName()));
		}
		return ret;
	}

	@Override
	public boolean hasAssign(LObject<T> object, Worker<T> worker) {
		if (objectsAssigns.contains(object.getName()))
			for (AssignedLabel<T> al : objectsAssigns.get(object.getName())){
				if (al.getWorker().equals(worker))
					return true;
			}
		return false;
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
