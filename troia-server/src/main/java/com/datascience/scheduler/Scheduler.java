package com.datascience.scheduler;

import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.core.base.Worker;

import java.util.NavigableSet;
import java.util.TreeSet;

public class Scheduler<T> implements IScheduler<T> {

	protected NavigableSet<LObject<T>> queue;
	protected Data<T> data;
	protected IPriorityCalculator<T> calculator;
	protected ISchedulerForWorker<T> workerScheduler;

	public Scheduler() { }

	public Scheduler(Project<T, ?, ?, ?> project, IPriorityCalculator<T> calculator) {
		setUpQueue(calculator);
		setSchedulerForWorker(new SchedulersForWorker.FirstNotSeen<T>());
		registerOnProject(project);
	}

	@Override
	public void update() {
		queue.clear();
		queue.addAll(data.getObjects());
	}

	@Override
	public void update(LObject<T> object) {
		queue.remove(object);
		queue.add(object);
	}

	@Override
	public LObject<T> nextObject() {
		return queue.first();
	}

	@Override
	public LObject<T> nextObject(Worker<T> worker) {
		return workerScheduler.nextObjectForWorker(queue.iterator(), worker);
	}

	@Override
	public void setUpQueue(IPriorityCalculator<T> calculator) {
		queue = new TreeSet<LObject<T>>(new ObjectComparator<T>(calculator));
		this.calculator = calculator;
	}

	@Override
	public void setSchedulerForWorker(ISchedulerForWorker<T> schedulerForWorker){
		workerScheduler = schedulerForWorker;
	}

	@Override
	public <V, W> void registerOnProject(Project<T, ?, V, W> project) {
		data = project.getData();
		calculator.registerOnProject(project);
		workerScheduler.setProject(project);
		ISchedulerNotificator<T> notificator = calculator.getSchedulerNotificator(project);
		notificator.registerOnProject(project);
		notificator.setScheduler(this);
	}

	@Override
	public Data<T> getData(){
		return data;
	}

	@Override
	public IPriorityCalculator<T> getCalculator() {
		return calculator;
	}

	@Override
	public String getId(){
		return "scheduler";
	}
}