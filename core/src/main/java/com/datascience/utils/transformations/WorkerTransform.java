package com.datascience.utils.transformations;

import com.datascience.core.base.Worker;
import com.datascience.utils.ITransformation;

/**
* User: artur
* Date: 5/7/13
*/
public class WorkerTransform implements ITransformation<Worker, String> {

	@Override
	public String transform(Worker w) {
		return  w.getName();
	}

	@Override
	public Worker inverse(String object) {
		return new Worker(object);
	}
}
