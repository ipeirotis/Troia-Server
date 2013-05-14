package com.datascience.galc;

import com.datascience.core.results.WorkerContResults;

public class WorkerContQuality {

	String worker;
	WorkerContResults quality;

	public WorkerContQuality(String worker, WorkerContResults result){
		this.worker = worker;
		quality = result;
	}
}
