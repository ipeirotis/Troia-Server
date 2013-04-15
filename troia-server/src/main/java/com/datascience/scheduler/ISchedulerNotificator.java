package com.datascience.scheduler;

import com.datascience.core.base.Project;

/**
 * @Author: konrad
 */
public interface ISchedulerNotificator<T> {

	void registerOnProject(Project project);
	void setScheduler(IScheduler<T> scheduler);
}
