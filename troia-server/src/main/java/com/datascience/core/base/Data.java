package com.datascience.core.base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @Author: konrad
 */
public class Data <T>{

	protected Set<AssignedLabel<T>> assigns;
	protected Set<Worker<T>> workers;
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
	}

	public void addWorker(Worker<T> worker){
		workers.add(worker);
	}

	public void addObject(LObject<T> object){
		objects.add(object);
	}

	public void addGoldObject(LObject<T> object){
		goldObjects.add(object);
		addObject(object);
	}

	public void addEvaluationObject(LObject<T> object){
		evaluationObjects.add(object);
		addObject(object);
	}

	public void addAssign(AssignedLabel<T> assign){
		assigns.add(assign);
		LObject<T> object = assign.getLobject();
		if (!datums.containsKey(object)) {
			datums.put(object, new HashSet<AssignedLabel<T>>());
		}
		datums.get(object).add(assign);
		assign.getWorker().addAssign(assign);
	}


	public Set<AssignedLabel<T>> getAssignsForObject(LObject<T> lObject){
		return datums.get(lObject);
	}
	
	public Set<AssignedLabel<T>> getAssigns(){
		return assigns;
	}

}
