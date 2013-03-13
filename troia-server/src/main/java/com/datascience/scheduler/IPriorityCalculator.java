package com.datascience.scheduler;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;

/**
 * @Author: konrad
 */
public interface IPriorityCalculator<T> {

	double getPriority(LObject<T> object);

	void setProject(Project<T, ?, ?, ?> project);
}
