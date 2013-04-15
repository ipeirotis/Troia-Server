package com.datascience.gal;

public class WorkerValue<T> {

	String workerName;
	T value;
	
	public WorkerValue(String worker, T v){
		workerName = worker;
		value = v;
	}
}
