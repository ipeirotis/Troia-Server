package com.datascience.scheduler;

import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;

/**
 * @Author: konrad
 */
public interface IPriorityCalculator<T> {

	double getPriority(LObject<T> object);

	<V, W> void registerOnProject(Project<T, ?, V, W> project);

	public String getId();

	<U extends Data<T>, V, W> ISchedulerNotificator<T>
			getSchedulerNotificator(Project<T, U, V, W> project);
}
