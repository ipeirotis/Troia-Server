package com.datascience.core.base;

import java.util.HashMap;
import java.util.Map;

/**
 * User: artur
 */
public class Results<T, U, V> {
	protected Map<LObject<T>, U> datumResults;
	protected Map<Worker<T>, V> workerResults;

	public Results(){
		datumResults = new HashMap<LObject<T>, U>();
		workerResults = new HashMap<Worker<T>, V>();
	}

	public Map<Worker<T>, V> getWokerResults(){
		return workerResults;
	}

	public Map<LObject<T>, U> getDatumResults(){
		return datumResults;
	}
}
