package com.datascience.scheduler;

import com.datascience.core.base.Data;
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
	LObject<T> nextObject(Worker<T> worker);

	void setUpQueue(IPriorityCalculator<T> calculator);
	void setSchedulerForWorker(ISchedulerForWorker<T> schedulerForWorker);
	<V, W> void registerOnProject(Project<T, ?, V, W> project);
	Data<T> getData();
	IPriorityCalculator<T> getCalculator();

	String getId();
}
