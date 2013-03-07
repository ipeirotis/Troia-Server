package com.datascience.scheduler;

import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;

import java.util.PriorityQueue;
import java.util.Queue;

public class Scheduler<T> implements IScheduler<T> {

	public static final int INITIAL_SIZE = 10;
	private Queue<LObject<T>> queue;
	private Data<T> data;

	public Scheduler(Data<T> data, IPriorityCalculator<T> calculator) {
		queue = new PriorityQueue<LObject<T>>(INITIAL_SIZE, new ObjectComparator<T>(calculator));
		this.data = data;
		update();
	}

	@Override
	public void update() {
		queue.addAll(data.getObjects());
	}

	@Override
	public void update(LObject<T> object) {
		queue.add(object);
	}

	@Override
	public LObject<T> nextObject() {
		// XXX the queue doesn't recompute itself when polling, while objects can be changed after insertion.
		// It is the simplest workaround.
		LObject<T> object = queue.poll();
		queue.add(object);
		return queue.poll();
	}

	@Override
	public LObject<T> nextObject(Worker<T> worker) {
		throw new UnsupportedOperationException();
	}
}