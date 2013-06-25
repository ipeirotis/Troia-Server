package com.datascience.core.base;


import com.datascience.core.algorithms.INewDataObserver;
import com.datascience.core.results.IResults;
import com.datascience.scheduler.IScheduler;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * User: artur
 * T - object class
 * U - data class
 * V - datum results class
 * W - worker results class
 */
public abstract class Project<T, U extends IData<T>, V, W> {

	protected Algorithm<T, U, V, W> algorithm;
	protected U data;
	protected IResults<T, V, W> results;
	protected JsonObject initializationData;
	protected IScheduler<T> scheduler;

	public Project(Algorithm<T, U, V, W> alg, U data, IResults<T, V, W> results){
		this.algorithm = alg;
		this.data = data;
		this.results = results;
	}

	public U getData(){
		return data;
	}

	public IResults<T, V, W> getResults(){
		return results;
	}

	public W getWorkerResults(Worker worker){
		return results.getWorkerResult(worker);
	}

	public V getObjectResults(LObject<T> object){
		return results.getDatumResult(object);
	}

	public Algorithm<T, U, V, W> getAlgorithm(){
		return algorithm;
	}

	public void setInitializationData(JsonObject jo){
		initializationData = jo;
	}

	public void setData(U data){
		this.data = data;
		if (algorithm instanceof INewDataObserver) {
			this.data.addNewUpdatableAlgorithm((INewDataObserver) algorithm);
		}
		algorithm.setData(this.data);
	}

	public void setResults(IResults<T, V, W> results){
		this.results = results;
		algorithm.setResults(results);
	}

	public JsonObject getInitializationData(){
		return initializationData;
	}

	public void setScheduler(IScheduler<T> scheduler){
		this.scheduler = scheduler;
		this.scheduler.registerOnProject(this);
		this.scheduler.update();
	}

	public IScheduler<T> getScheduler(){
		return scheduler;
	}

	public abstract String getKind();

	public Map<String, Object> getInfo() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("Number of assigns", data.getAssigns().size());
		ret.put("Number of objects", data.getObjects().size());
		ret.put("Number of gold objects", data.getGoldObjects().size());
		ret.put("Number of workers", data.getWorkers().size());
		ret.put("Initialization data", getInitializationData());
		ret.putAll(getAdditionalInfo());
		return ret;
	}

	protected abstract Map<String, Object> getAdditionalInfo();
}
