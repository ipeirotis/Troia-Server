package com.datascience.scheduler;

import com.datascience.core.base.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class SchedulerTest {

	private Data<ContValue> populate(int objectsCount) {
		Data<ContValue> data = new Data<ContValue>();
		data.addObject(new LObject<ContValue>("object0"));
		for (int i = 0; i < objectsCount; i++) {
			for (int j = 0; j < i; j++) {
				data.addAssign(
						new AssignedLabel<ContValue>(
								new Worker<ContValue>("worker" + j),
								new LObject<ContValue>("object" + i),
								new ContValue(0.)
						)
				);
			}
		}
		return data;
	}

	@Test
	public void schedulerTest() {
		final int objectsCount = 5;
		Data<ContValue> data = populate(objectsCount);
		Scheduler<ContValue> scheduler = new Scheduler(data, new DummyPriorityCalculator(data));
		for (int i = 0; i < objectsCount; i++) {
			assertEquals(data.getObject("object0"), scheduler.nextObject());
		}
	}

	@Test
	public void cachedSchedulerTest() {

		final int objectsCount = 5;
		Data<ContValue> data = populate(objectsCount);
		DummyCachedScheduler<ContValue> scheduler = new DummyCachedScheduler(data, new DummyPriorityCalculator(data));
		for (int i = 0; i < objectsCount; i++) {
			assertEquals(data.getObject("object" + i), scheduler.nextObject());
		}
		assertEquals(null, scheduler.nextObject());
		scheduler.revert(data.getObject("object0"));
		assertEquals(data.getObject("object0"), scheduler.nextObject());
		scheduler.revert(data.getObject("object3"));
		assertEquals(data.getObject("object3"), scheduler.nextObject());
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

	public static class DummyCachedScheduler<T> extends CachedScheduler<T> {

		public DummyCachedScheduler(Data<T> data, IPriorityCalculator<T> calculator) {
			super(data, calculator);
		}

		public void revert(LObject<T> object) {
			polled.invalidate(object.getName());
		}
	}
}
