package com.datascience.core.base;

import com.datascience.core.algorithms.INewDataObserver;

import java.util.*;

/**
 * Observable - notifies updatable algorithms about new data
 * Also it is factory for new objects and workers
 * @Author: konrad
 */
public class Data <T>{

	protected Set<AssignedLabel<T>> assigns;
	protected Set<Worker<T>> workers;
	protected Map<String, Worker<T>> mapWorkers;
	protected Map<String, LObject<T>> mapObjects;
	protected Set<LObject<T>> objects;
	protected Set<LObject<T>> goldObjects;
	protected Set<LObject<T>> evaluationObjects;
	protected Map<LObject<T>, Set<AssignedLabel<T>>> datums;

	protected transient List<INewDataObserver<T>> newDataObservers;

	public Data(){
		assigns = new HashSet<AssignedLabel<T>>();
		workers = new HashSet<Worker<T>>();
		objects = new HashSet<LObject<T>>();
		goldObjects = new HashSet<LObject<T>>();
		evaluationObjects = new HashSet<LObject<T>>();
		datums = new HashMap<LObject<T>, Set<AssignedLabel<T>>>();
		mapWorkers = new HashMap<String, Worker<T>>();
		mapObjects = new HashMap<String, LObject<T>>();
		newDataObservers = new LinkedList<INewDataObserver<T>>();
	}

	public void addWorker(Worker<T> worker){
		if (!workers.contains(worker)){
			workers.add(worker);
			mapWorkers.put(worker.getName(), worker);
			notifyNewWorker(worker);
		}
	}

	public Worker<T> getWorker(String workerId){
		return mapWorkers.get(workerId);
	}

	public Worker<T> getOrCreateWorker(String workerId){
		Worker<T> worker = getWorker(workerId);
		if (worker == null) {
			worker = new Worker<T>(workerId);
		}
		return worker;
	}

	public Set<Worker<T>> getWorkers() {
		return workers;
	}

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

	public LObject<T> getObject(String objectId){
		return mapObjects.get(objectId);
	}

	public LObject<T> getOrCreateObject(String objectId){
		LObject<T> object = getObject(objectId);
		if (object == null) {
			object = new LObject<T>(objectId);
			//addObject(object);
		}
		return object;
	}

	public Set<LObject<T>> getObjects(){
		return objects;
	}

	private void addGoldObject(LObject<T> object){
		goldObjects.add(object);
		notifyNewGoldObject(object);
	}

	public LObject<T> getGoldObject(String objectId){
		LObject<T> object = mapObjects.get(objectId);
		if (object == null || !object.isGold()){
			throw new IllegalArgumentException("There is no gold object with id = " + objectId);
		}
		return object;
	}

	public Set<LObject<T>> getGoldObjects(){
		return goldObjects;
	}

	public void markObjectAsGold(LObject<T> object, T label){
		if (!objects.contains(object)) {
			throw new IllegalArgumentException("Object %s is not in this Data".format(object.getName()));
		}
		object.setGoldLabel(label);
	}

	private void addEvaluationObject(LObject<T> object){
		evaluationObjects.add(object);
	}

	public Set<LObject<T>> getEvaluationObjects(){
		return evaluationObjects;
	}

	public LObject<T> getEvaluationObject(String objectId){
		LObject<T> object = mapObjects.get(objectId);
		if (object == null || !object.isEvaluation()){
			throw new IllegalArgumentException("There is no evaluation object with id = " + objectId);
		}
		return object;
	}

	public void addAssign(AssignedLabel<T> assign){
		forceAddAssign(assign, assigns);
		LObject<T> object = assign.getLobject();
		addObject(object);
		forceAddAssign(assign, datums.get(object));
		Worker<T> worker = assign.getWorker();
		addWorker(worker);
		forceAddAssign(assign, worker.getAssigns());
		notifyNewAssign(assign);
	}

	/**
	 * This assumes that assigns are compared only on object and worker
	 */
	public boolean hasAssign(LObject<T> object, Worker<T> worker){
		AssignedLabel<T> assign = new AssignedLabel<T>(worker, object, null);
		return assigns.contains(assign);
	}

	public Set<AssignedLabel<T>> getAssignsForObject(LObject<T> lObject){
		Set<AssignedLabel<T>> ret = datums.get(lObject);
		if (ret != null)
			return ret;
		return new HashSet<AssignedLabel<T>>();
	}

	public Set<AssignedLabel<T>> getAssigns(){
		return assigns;
	}

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

	private void forceAddAssign(AssignedLabel<T> assign, Set<AssignedLabel<T>> assigns) {
		if (!assigns.add(assign)) {
			assigns.remove(assign);
			assigns.add(assign);
		}
	}
}
