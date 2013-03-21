package com.datascience.scheduler;

import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.core.base.Worker;

import java.util.PriorityQueue;
import java.util.Queue;

public class Scheduler<T> implements IScheduler<T> {

	protected static final int INITIAL_QUEUE_SIZE = 10;

	protected Queue<LObject<T>> queue;
	protected Data<T> data;
	private IPriorityCalculator<T> calculator;

	public Scheduler() { }

	public Scheduler(Project<T, ?, ?, ?> project, IPriorityCalculator<T> calculator) {
		setUpQueue(calculator);
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
		return queue.peek();
	}

	@Override
	public LObject<T> nextObject(Worker<T> worker) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setUpQueue(IPriorityCalculator<T> calculator) {
		queue = new PriorityQueue<LObject<T>>(INITIAL_QUEUE_SIZE, new ObjectComparator<T>(calculator));
		this.calculator = calculator;
	}

	@Override
	public <V, W> void registerOnProject(Project<T, ?, V, W> project) {
		data = project.getData();
		calculator.registerOnProject(project);
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