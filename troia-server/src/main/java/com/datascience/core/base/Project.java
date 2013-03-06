package com.datascience.core.base;

import java.util.HashMap;
import java.util.Map;

/**
 * User: artur
 */
public abstract class Project<T, U extends Data<T>, V, W> {

	protected Algorithm<T, U, V, W> algorithm;
	protected U data;
	protected int id;

	public Project(){
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
