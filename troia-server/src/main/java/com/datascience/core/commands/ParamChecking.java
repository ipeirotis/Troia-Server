package com.datascience.core.commands;

import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.core.base.Worker;

/**
 *
 * @author konrad
 */
public class ParamChecking {
	
	public static <T> LObject<T> datum(Project project, String datumId){
		LObject<T> d = project.getData().getObject(datumId);
		if (d == null) {
			throw new IllegalArgumentException("No datum with id: " + datumId);
		}
		return d;
	}

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
