package com.datascience.scheduler;

import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;

import java.util.PriorityQueue;
import java.util.Queue;

public class Scheduler<T> implements IScheduler<T> {

	public static int INITIAL_QUEUE_SIZE = 10;

	protected Queue<LObject<T>> queue;
	private Data<T> data;

	public Scheduler(Data<T> data, IPriorityCalculator<T> calculator) {
		queue = new PriorityQueue<LObject<T>>(INITIAL_QUEUE_SIZE, new ObjectComparator<T>(calculator));
		this.data = data;
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
}