package com.datascience.scheduler;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.results.INewResultsListener;

/**
 * @Author: konrad
 */
public class SchedulerNotificator<T, U, V> implements INewResultsListener<T, U, V> {

	protected Scheduler<T> scheduler;

	public void setScheduler(Scheduler<T> scheduler){
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
