package com.datascience.core.datastoring.memory;

import com.datascience.core.algorithms.INewDataObserver;
import com.datascience.core.base.AbstractData;
import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;

import java.util.*;

/**
 * Observable - notifies updatable algorithms about new data
 * Also it is factory for new objects and workers
 * @Author: konrad
 */
public class InMemoryData<T> extends AbstractData<T> {

	protected Set<AssignedLabel<T>> assigns;
	protected Set<Worker<T>> workers;
	protected Map<String, Worker<T>> mapWorkers;
	protected Map<String, LObject<T>> mapObjects;
	protected Set<LObject<T>> objects;
	protected Set<LObject<T>> goldObjects;
	protected Set<LObject<T>> evaluationObjects;
	protected Map<LObject<T>, Set<AssignedLabel<T>>> datums;
	protected Map<Worker<T>, Set<AssignedLabel<T>>> workersAssigns;

	public InMemoryData(){
		assigns = new HashSet<AssignedLabel<T>>();
		workers = new HashSet<Worker<T>>();
		objects = new HashSet<LObject<T>>();
		goldObjects = new HashSet<LObject<T>>();
		evaluationObjects = new HashSet<LObject<T>>();
		datums = new HashMap<LObject<T>, Set<AssignedLabel<T>>>();
		workersAssigns = new HashMap<Worker<T>, Set<AssignedLabel<T>>>();
		mapWorkers = new HashMap<String, Worker<T>>();
		mapObjects = new HashMap<String, LObject<T>>();
		newDataObservers = new LinkedList<INewDataObserver<T>>();
	}

	@Override
	public void addWorker(Worker<T> worker){
		if (!workers.contains(worker)){
			workers.add(worker);
			mapWorkers.put(worker.getName(), worker);
			notifyNewWorker(worker);
			workersAssigns.put(worker, new HashSet<AssignedLabel<T>>());
		}
	}

	@Override
	public Worker<T> getWorker(String workerId){
		return mapWorkers.get(workerId);
	}

	@Override
	public Set<Worker<T>> getWorkers() {
		return workers;
	}

	@Override
	public void addObject(LObject<T> object){
		if (objects.contains(object)) {
			LObject oldObject = getObject(object.getName());
			if (object.isGold() && !oldObject.isGold()) {
				oldObject.setGoldLabel(object.getGoldLabel());
				addGoldObject(oldObject);
			}
			if (object.isEvaluation() && !oldObject.isEvaluation()) {
				oldObject.setEvaluationLabel(object.getEvaluationLabel());
				addEvaluationObject(oldObject);
			}
			return;
		}

		objects.add(object);
		mapObjects.put(object.getName(), object);
		datums.put(object, new HashSet<AssignedLabel<T>>());
		notifyNewObject(object);

		if (object.isGold()) {
			addGoldObject(object);
		}
		if (object.isEvaluation()) {
			addEvaluationObject(object);
		}
	}

	@Override
	public LObject<T> getObject(String objectId){
		return mapObjects.get(objectId);
	}

	@Override
	public Set<LObject<T>> getObjects(){
		return objects;
	}

	private void addGoldObject(LObject<T> object){
		goldObjects.add(object);
		notifyNewGoldObject(object);
	}

	@Override
	protected LObject<T> uncheckedGetGoldObject(String objectId){
		return mapObjects.get(objectId);
	}

	@Override
	public Set<LObject<T>> getGoldObjects(){
		return goldObjects;
	}

	@Override
	public void markObjectAsGold(LObject<T> object, T label){
		if (!objects.contains(object)) {
			throw new IllegalArgumentException("Object %s is not in this Data".format(object.getName()));
		}
		object.setGoldLabel(label);
		if (!goldObjects.contains(object))
			addGoldObject(object);
	}

	private void addEvaluationObject(LObject<T> object){
		evaluationObjects.add(object);
	}

	@Override
	public Set<LObject<T>> getEvaluationObjects(){
		return evaluationObjects;
	}

	@Override
	protected LObject<T> uncheckedGetEvaluationObject(String objectId){
		return mapObjects.get(objectId);
	}

	@Override
	public Collection<AssignedLabel<T>> getWorkerAssigns(Worker<T> worker){
		return workersAssigns.get(worker);
	}

	@Override
	public void addAssign(AssignedLabel<T> assign){
		forceAddAssign(assign, assigns);
		LObject<T> object = assign.getLobject();
		addObject(object);
		forceAddAssign(assign, datums.get(object));
		Worker<T> worker = assign.getWorker();
		addWorker(worker);
		forceAddAssign(assign, workersAssigns.get(worker));
		notifyNewAssign(assign);
	}

	/**
	 * This assumes that assigns are compared only on object and worker
	 */
	@Override
	public boolean hasAssign(LObject<T> object, Worker<T> worker){
		AssignedLabel<T> assign = new AssignedLabel<T>(worker, object, null);
		return assigns.contains(assign);
	}

	@Override
	public Set<AssignedLabel<T>> getAssignsForObject(LObject<T> lObject){
		Set<AssignedLabel<T>> ret = datums.get(lObject);
		if (ret != null)
			return ret;
		return new HashSet<AssignedLabel<T>>();
	}

	@Override
	public Set<AssignedLabel<T>> getAssigns(){
		return assigns;
	}

	private void forceAddAssign(AssignedLabel<T> assign, Set<AssignedLabel<T>> assigns) {
		if (!assigns.add(assign)) {
			assigns.remove(assign);
			assigns.add(assign);
		}
	}
}
