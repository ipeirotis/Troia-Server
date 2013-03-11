package com.datascience.scheduler;

import com.datascience.core.base.*;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;


public class SchedulerTest {

	private void addAssign(Data<ContValue> data, String worker, String object) {
		data.addAssign(
				new AssignedLabel<ContValue>(
						new Worker<ContValue>(worker),
						new LObject<ContValue>(object),
						new ContValue(0.)
				)
		);
	}

	private Data<ContValue> populate(int objectsCount) {
		Data<ContValue> data = new Data<ContValue>();
		data.addObject(new LObject<ContValue>("object0"));
		for (int i = 0; i < objectsCount; i++) {
			for (int j = 0; j < i; j++) {
				addAssign(data, "worker" + j, "object" + i);
			}
		}
		return data;
	}

	@Test
	public void schedulerTest() {
		final int objectsCount = 5;
		Data<ContValue> data = populate(objectsCount);
		Scheduler<ContValue> scheduler = new Scheduler(data, new DummyPriorityCalculator(data));
		scheduler.update();
		for (int i = 0; i < objectsCount; i++) {
			assertEquals(data.getObject("object0"), scheduler.nextObject());
		}
	}

	@Test
	public void cachedSchedulerOrderTest() {
		final int objectsCount = 5;
		Data<ContValue> data = populate(objectsCount);
		CachedScheduler<ContValue> scheduler = new CachedScheduler(data, new DummyPriorityCalculator(data), 10,
				TimeUnit.MINUTES);
		scheduler.update();
		// Check if objects are returned in proper order.
		for (int i = 0; i < objectsCount; i++) {
			assertEquals(data.getObject("object" + i), scheduler.nextObject());
		}
		assertEquals(null, scheduler.nextObject());
	}

	@Test
	public void cachedSchedulerExpirationTest() {
		final int objectsCount = 5;
		Data<ContValue> data = populate(objectsCount);
		CachedScheduler<ContValue> scheduler = new CachedScheduler(data, new DummyPriorityCalculator(data), 10,
				TimeUnit.MINUTES);
		scheduler.update();
		// Cache all objects.
		for (int i = 0; i < objectsCount; i++) {
			scheduler.nextObject();
		}
		// Simulate cache expiration.
		scheduler.update(data.getObject("object0"));
		assertEquals(data.getObject("object0"), scheduler.nextObject());
		scheduler.update(data.getObject("object3"));
		assertEquals(data.getObject("object3"), scheduler.nextObject());
	}

	@Test
	public void cachedSchedulerUpdateTest() {
		final int objectsCount = 5;
		Data<ContValue> data = populate(objectsCount);
		CachedScheduler<ContValue> scheduler = new CachedScheduler(data, new DummyPriorityCalculator(data), 10,
				TimeUnit.MINUTES);
		scheduler.update();
		// Cache all objects.
		for (int i = 0; i < objectsCount; i++) {
			scheduler.nextObject();
		}
		// Simulate voting.
		for (int i = 0; i < 6; i++) {
			addAssign(data, "worker" + i, "object0");
			scheduler.update(data.getObject("object0"));
		}
		addAssign(data, "worker0", "object1");
		scheduler.update(data.getObject("object1"));
		assertEquals(data.getObject("object1"), scheduler.nextObject());
		assertEquals(data.getObject("object0"), scheduler.nextObject());
	}


	public static class DummyPriorityCalculator<T> implements IPriorityCalculator<T> {

		private Data<T> data;

		public DummyPriorityCalculator(Data<T> data) {
			this.data = data;
		}

		@Override
		public double getPriority(LObject<T> object) {
			return data.getAssignsForObject(object).size();
		}
	}
}
