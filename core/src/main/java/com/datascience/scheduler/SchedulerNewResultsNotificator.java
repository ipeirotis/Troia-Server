package com.datascience.scheduler;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.core.base.Worker;
import com.datascience.core.results.INewResultsListener;

/**
 * @Author: konrad
 */
public class SchedulerNewResultsNotificator<T, U, V> implements ISchedulerNotificator<T>, INewResultsListener<T, U, V> {

	protected IScheduler<T> scheduler;

	public SchedulerNewResultsNotificator(){};

	public SchedulerNewResultsNotificator(IScheduler<T> scheduler){
		this.scheduler = scheduler;
	}

	@Override
	public void setScheduler(IScheduler<T> scheduler){
		this.scheduler = scheduler;
	}

	@Override
	public void newResultsForAll() {
		scheduler.update();
	}

	@Override
	public void newResultsForObject(LObject<T> object, U results) {
		scheduler.update(object);
	}

	@Override
	public void newResultsForWorker(Worker<T> worker, V results) {
		// We don't care
	}

	@Override
	public void registerOnProject(Project project) {
		project.getResults().addNewResultsListener(this);
	}
}
