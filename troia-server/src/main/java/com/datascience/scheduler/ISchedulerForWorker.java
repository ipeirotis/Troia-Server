package com.datascience.scheduler;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.core.base.Worker;

import java.util.Iterator;

/**
 * @Author: konrad
 */
public interface ISchedulerForWorker<T> {

	LObject<T> nextObjectForWorker(Iterator<LObject<T>> objects, Worker<T> worker);
	String getId();
	void setProject(Project<T, ?, ?, ?> project);
}
