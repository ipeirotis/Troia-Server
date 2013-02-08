package com.datascience.core.base;

import java.util.*;

/**
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


	public Data(){
		assigns = new HashSet<AssignedLabel<T>>();
		workers = new HashSet<Worker<T>>();
		objects = new HashSet<LObject<T>>();
		goldObjects = new HashSet<LObject<T>>();
		evaluationObjects = new HashSet<LObject<T>>();
		datums = new HashMap<LObject<T>, Set<AssignedLabel<T>>>();
		mapWorkers = new HashMap<String, Worker<T>>();
		mapObjects = new HashMap<String, LObject<T>>();
	}

	public void addWorker(Worker<T> worker){
		workers.add(worker);
		mapWorkers.put(worker.getName(), worker);
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
		objects.add(object);
		mapObjects.put(object.getName(), object);
		if (!datums.containsKey(object)) {
			datums.put(object, new HashSet<AssignedLabel<T>>());
		}
	}

	public LObject<T> getObject(String objectId){
		return mapObjects.get(objectId);
	}

	public LObject<T> getOrCreateObject(String objectId){
		LObject<T> object = getObject(objectId);
		if (object == null) {
			object = new LObject<T>(objectId);
		}
		return object;
	}

	public Set<LObject<T>> getObjects(){
		return objects;
	}

	public void addGoldObject(LObject<T> object){
		goldObjects.add(object);
		addObject(object);
	}

	public LObject<T> getGoldObject(String objectId){
		for (LObject<T> obj : goldObjects){
			if (obj.getName().equals(objectId))
				return obj;
		}
		return null;
	}

	public Set<LObject<T>> getGoldObjects(){
		return goldObjects;
	}

	public void addEvaluationObject(LObject<T> object){
		evaluationObjects.add(object);
		addObject(object);
	}

	public Set<LObject<T>> getEvaluationObjects(){
		return evaluationObjects;
	}

	public void addAssign(AssignedLabel<T> assign){
		assigns.add(assign);
		LObject<T> object = assign.getLobject();
		addObject(object);
		datums.get(object).add(assign);
		Worker<T> worker = assign.getWorker();
		if (!workers.contains(worker)) {
			workers.add(worker);
		}
		assign.getWorker().addAssign(assign);
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

}
