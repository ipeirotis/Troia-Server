package com.datascience.galc.commands;

import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;

public class ParamChecking {
	
	public static <T> Worker<T> worker(Data<T> data, String workerId){
		Worker<T> w = data.getWorker(workerId);
		if (w == null) {
			throw new IllegalArgumentException("No worker with id: " + workerId);
		}
		return w;
	}
	
	public static <T> LObject<T> object(Data<T> data, String objectId){
		LObject<T> obj = data.getObject(objectId);
		if (obj == null) {
			throw new IllegalArgumentException("No object with id: " + objectId);
		}
		return obj;
	}
}
