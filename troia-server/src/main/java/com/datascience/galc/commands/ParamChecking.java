package com.datascience.galc.commands;

import com.datascience.core.base.Data;
import com.datascience.core.base.Worker;

public class ParamChecking {
	
	public static Worker worker(Data data, String workerId){
		Worker w = data.getWorker(workerId);
		if (w == null) {
			throw new IllegalArgumentException("No worker with id: " + workerId);
		}
		return w;
	}
}
