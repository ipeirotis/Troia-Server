package com.datascience.scheduler;

import com.datascience.core.algorithms.INewDataObserver;
import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.core.base.Worker;

/**
 * @Author: konrad
 */
public class SchedulerNewDataNotificator<T> implements ISchedulerNotificator<T>, INewDataObserver<T> {

	protected IScheduler<T> scheduler;

	public SchedulerNewDataNotificator(){}

	public SchedulerNewDataNotificator(IScheduler<T> scheduler){
		this.scheduler = scheduler;
	}

	@Override
	public void setScheduler(IScheduler<T> scheduler){
		this.scheduler = scheduler;
	}

	@Override
	public void newAssign(AssignedLabel<T> assign) {
		scheduler.update(assign.getLobject());
	}

	@Override
	public void newGoldObject(LObject<T> object) {
		scheduler.update(object);
	}

	@Override
	public void newObject(LObject<T> object) {
		scheduler.update(object);
	}

	@Override
	public void newWorker(Worker<T> worker) {
		// We ignore it
	}

	@Override
	public void registerOnProject(Project project) {
		project.getData().addNewUpdatableAlgorithm(this);
	}
}
