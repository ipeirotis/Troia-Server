package com.datascience.scheduler;

import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;

import java.util.PriorityQueue;
import java.util.Queue;

public class Scheduler<T> implements IScheduler<T> {

	protected Queue<LObject<T>> queue;
	private Data<T> data;

	public Scheduler(Data<T> data, IPriorityCalculator<T> calculator) {
		queue = new PriorityQueue<LObject<T>>(10, new ObjectComparator<T>(calculator));
		this.data = data;
		update();
	}

	@Override
	public void update() {
		queue.clear();
		queue.addAll(data.getObjects());
	}

	@Override
	public void update(LObject<T> object) {
		queue.add(object);
	}

	@Override
	public LObject<T> nextObject() {
		LObject<T> object = queue.poll();
		if (object != null) {
			queue.add(object);
		}
		return object;
	}

	@Override
	public LObject<T> nextObject(Worker<T> worker) {
		throw new UnsupportedOperationException();
	}
}