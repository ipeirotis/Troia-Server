package com.datascience.scheduler;

import com.datascience.core.base.IData;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.core.base.Worker;

/**
 * @Author: konrad
 */
public interface IScheduler<T> {

	void update();
	void update(LObject<T> object);

	LObject<T> nextObject();
	LObject<T> nextObject(Worker worker);

	void setUpQueue(IPriorityCalculator<T> calculator);
	void setSchedulerForWorker(ISchedulerForWorker schedulerForWorker);
	<V, W> void registerOnProject(Project<T, ?, V, W> project);
	IData<T> getData();
	IPriorityCalculator<T> getCalculator();

	String getId();
}
