package com.datascience.datastoring.datamodels.kv;

import com.datascience.core.base.AbstractData;
import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.datastoring.adapters.kv.ISafeKVStorage;

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
	protected ISafeKVStorage<Collection<Worker>> workers; //SINGLE ROW!

	public KVData(ISafeKVStorage<Collection<AssignedLabel<T>>> workersAssigns,
				  ISafeKVStorage<Collection<AssignedLabel<T>>> objectsAssigns,
				  ISafeKVStorage<Collection<LObject<T>>> objects,
				  ISafeKVStorage<Collection<LObject<T>>> goldObjects,
				  ISafeKVStorage<Collection<LObject<T>>> evaluationObjects,
				  ISafeKVStorage<Collection<Worker>> workers){
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
		return getObjectFromCollection(objectId, goldObjects.get(""));
	}

	@Override
	protected LObject<T> uncheckedGetEvaluationObject(String objectId) {
		return getObjectFromCollection(objectId, evaluationObjects.get(""));
	}

	@Override
	protected void uncheckedAddWorker(Worker worker) {
		Collection<Worker> oldWorkers = workers.get("");
		if (!oldWorkers.contains(worker)){
			oldWorkers.add(worker);
			workers.put("", oldWorkers);
			notifyNewWorker(worker);
			workersAssigns.put(worker.getName(), new LinkedList());
		}
	}

	@Override
	public Worker getWorker(String workerId) {
		for (Worker w : workers.get(""))
			if (w.getName().equals(workerId))
				return w;
		return null;
	}

	@Override
	public Collection<Worker> getWorkers() {
		return workers.get("");
	}

	@Override
	protected void uncheckedAddObject(LObject<T> object) {
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
				oldObjects.remove(oldObject);
				oldObject.setGoldLabel(object.getGoldLabel());
				oldObjects.add(oldObject);
				objects.put("", oldObjects);
				addGoldObject(oldObject);
			}
			if (object.isEvaluation() && !oldObject.isEvaluation()) {
				oldObjects.remove(oldObject);
				oldObject.setEvaluationLabel(object.getEvaluationLabel());
				oldObjects.add(oldObject);
				objects.put("", oldObjects);
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
		return getObjectFromCollection(objectId, objects.get(""));
	}

	private LObject<T> getObjectFromCollection(String objectId, Collection<LObject<T>> collection){
		for (LObject<T> o : collection)
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
		for (AssignedLabel<T> al : assigns){
			if (al.equals(assign)){
				assigns.remove(al);
				break;
			}
		}
		assigns.add(assign);
		storage.put(key, assigns);
	}

	@Override
	public Collection<AssignedLabel<T>> getAssigns() {
		Collection<AssignedLabel<T>> ret = new LinkedList<AssignedLabel<T>>();
		for (Worker w : getWorkers()){
			ret.addAll(workersAssigns.get(w.getName()));
		}
		return ret;
	}

	@Override
	public boolean hasAssign(LObject<T> object, Worker worker) {
		if (objectsAssigns.contains(object.getName()))
			for (AssignedLabel<T> al : objectsAssigns.get(object.getName())){
				if (al.getWorker().equals(worker))
					return true;
			}
		return false;
	}

	@Override
	public Collection<AssignedLabel<T>> uncheckedGetWorkerAssigns(Worker worker) {
		return workersAssigns.get(worker.getName());
	}

	@Override
	public Collection<AssignedLabel<T>> uncheckedGetAssignsForObject(LObject<T> lObject) {
		return objectsAssigns.get(lObject.getName());
	}
}
