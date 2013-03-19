package com.datascience.scheduler;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.results.INewResultsListener;

/**
 * @Author: konrad
 */
public class SchedulerNotificator<T, U, V> implements INewResultsListener<T, U, V> {

	protected IScheduler<T> scheduler;

	public SchedulerNotificator(IScheduler<T> scheduler){
		this.scheduler = scheduler;
	}

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
}
