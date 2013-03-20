package com.datascience.core.base;


import com.datascience.core.results.Results;
import com.datascience.scheduler.IScheduler;
import com.datascience.scheduler.SchedulerNotificator;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * User: artur
 */
public abstract class Project<T, U extends Data<T>, V, W> {

	protected Algorithm<T, U, V, W> algorithm;
	protected U data;
	protected Results<T, V, W> results;
	protected JsonObject initializationData;
	protected IScheduler<T> scheduler;


	public Project(Algorithm<T, U, V, W> alg){
		this.algorithm = alg;
	}

	public U getData(){
		return data;
	}

	public Results<T, V, W> getResults(){
		return results;
	}

	public W getWorkerResults(Worker<T> worker){
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
	}

	public void setResults(Results<T, V, W> results){
		this.results = results;
	}

	public JsonObject getInitializationData(){
		return initializationData;
	}

	public void setScheduler(IScheduler<T> scheduler){
		this.scheduler = scheduler;
		results.addNewResultsListener(new SchedulerNotificator<T, V, W>(this.scheduler));
		assert data != null;
		this.scheduler.setProject(this);
	}

	public IScheduler<T> getScheduler(){
		return scheduler;
	}

	public Map<String, String> getInfo() {
		Map<String, String> ret = new HashMap<String, String>();
		ret.put("Number of assigns", String.valueOf(data.getAssigns().size()));
		ret.put("Number of objects", String.valueOf(data.getObjects().size()));
		ret.put("Number of gold objects", String.valueOf(data.getGoldObjects().size()));
		ret.put("Number of workers", String.valueOf(data.getWorkers().size()));
		return ret;
	}
}
