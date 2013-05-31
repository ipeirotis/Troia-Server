package com.datascience.utils.transformations.simple;

import com.datascience.core.base.Worker;
import com.datascience.utils.ITransformation;

/**
* User: artur
* Date: 5/7/13
*/
public class WorkerTransform<T> implements ITransformation<Worker<T>, String> {

	@Override
	public String transform(Worker<T> w) {
		return  w.getName();
	}

	@Override
	public Worker<T> inverse(String object) {
		return new Worker<T>(object);
	}
}
