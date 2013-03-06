package com.datascience.scheduler;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;

/**
 * @Author: konrad
 */
public interface IScheduler<T> {

	void update();
	void update(LObject<T> object);

	LObject<T> nextObject();
	LObject<T> nextObject(Worker<T> worker);

}
