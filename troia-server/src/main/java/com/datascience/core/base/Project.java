package com.datascience.core.base;


import com.datascience.core.results.Results;

import java.util.HashMap;
import java.util.Map;

/**
 * User: artur
 */
public abstract class Project<T, U extends Data<T>, V, W> {

	protected Algorithm<T, U, V, W> algorithm;
	protected U data;
	protected Results<T, V, W> results;


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

	public Map<String, String> getInfo() {
		Map<String, String> ret = new HashMap<String, String>();
		ret.put("Number of assigns", String.valueOf(data.getAssigns().size()));
		ret.put("Number of objects", String.valueOf(data.getObjects().size()));
		ret.put("Number of gold objects", String.valueOf(data.getGoldObjects().size()));
		ret.put("Number of workers", String.valueOf(data.getWorkers().size()));
		return ret;
	}
}
