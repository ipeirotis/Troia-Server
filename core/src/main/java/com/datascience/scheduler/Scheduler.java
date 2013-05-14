package com.datascience.scheduler;

import com.datascience.core.base.IData;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.core.base.Worker;

import java.util.Comparator;

public class Scheduler<T> implements IScheduler<T> {

	protected IIterablePriorityQueue<LObject<T>> queue;
	protected IData<T> data;
	protected IPriorityCalculator<T> calculator;
	protected ISchedulerForWorker<T> workerScheduler;

	public Scheduler() {
		queue = new IterablePriorityQueue<LObject<T>>(getObjectComparator());
	}

	public Scheduler(Project<T, ?, ?, ?> project, IPriorityCalculator<T> calculator) {
		this();
		setUpQueue(calculator);
		setSchedulerForWorker(new SchedulersForWorker.FirstNotSeen<T>());
		registerOnProject(project);
	}

	protected Comparator<LObject<T>> getObjectComparator(){
		return new Comparator<LObject<T>>() {
			@Override
			public int compare(LObject<T> object, LObject<T> object2) {
				return object.getName().compareTo(object2.getName());
			}
		};
	}

	private double getPriority(LObject<T> object){
		return calculator.getPriority(object);
	}

	@Override
	public void update() {
		queue.clear();
		for (LObject<T> object: data.getObjects()){
			queue.addReplacing(object, getPriority(object));
		}
	}

	@Override
	public void update(LObject<T> object) {
		queue.addReplacing(object, getPriority(object));
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
	public IData<T> getData(){
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