package com.datascience.scheduler;

import com.datascience.core.base.*;
import com.datascience.core.datastoring.memory.InMemoryData;
import com.datascience.galc.ContinuousIpeirotis;
import com.datascience.galc.ContinuousProject;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;


public class SchedulerTest {

	private void addAssign(IData<ContValue> data, String worker, String object) {
		data.addAssign(
				new AssignedLabel<ContValue>(
						new Worker<ContValue>(worker),
						new LObject<ContValue>(object),
						new ContValue(0.)
				)
		);
	}

	private ContinuousProject populate(int objectsCount) {
		InMemoryData<ContValue> data = new InMemoryData<ContValue>();
		data.addObject(new LObject<ContValue>("object0"));
		for (int i = 0; i < objectsCount; i++) {
			for (int j = 0; j < i; j++) {
				addAssign(data, "worker" + j, "object" + i);
			}
		}
		ContinuousProject cp = new ContinuousProject(new ContinuousIpeirotis(), new InMemoryData<ContValue>());
		cp.setData(data);
		return cp;
	}

	@Test
	public void schedulerTest() {
		final int objectsCount = 5;
		ContinuousProject cp = populate(objectsCount);
		IData<ContValue> data = cp.getData();
		Scheduler<ContValue> scheduler = new Scheduler(cp, new DummyPriorityCalculator(data));
		scheduler.update();
		for (int i = 0; i < objectsCount; i++) {
			assertEquals(data.getObject("object0"), scheduler.nextObject());
		}
	}

	@Test
	public void schedulerTestOnSmallUpdates() {
		final int objectsCount = 5;
		ContinuousProject cp = new ContinuousProject(new ContinuousIpeirotis(), new InMemoryData<ContValue>());
		InMemoryData<ContValue> data = new InMemoryData<ContValue>();
		cp.setData(data);
		Scheduler<ContValue> scheduler =
				new CachedScheduler<ContValue>(cp, new DummyPriorityCalculator(data), 10, TimeUnit.DAYS);

		for (int i = 0; i < objectsCount; i++) {
			LObject<ContValue> obj = new LObject<ContValue>("object" + i);
			for (int j = 0; j <= i; j++) {
				data.addAssign(
						new AssignedLabel<ContValue>(
								new Worker<ContValue>("worker" + j),
								obj,
								new ContValue(0.)
						)
				);
				scheduler.update(obj);
			}
		}

		for (int i = 0; i < objectsCount; i++) {
			assertEquals(data.getObject("object"+i), scheduler.nextObject());
		}
	}

	@Test
	public void cachedSchedulerOrderTest() {
		final int objectsCount = 5;
		ContinuousProject cp = populate(objectsCount);
		IData<ContValue> data = cp.getData();
		CachedScheduler<ContValue> scheduler = new CachedScheduler(cp, new DummyPriorityCalculator(data), 10,
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
		ContinuousProject cp = populate(objectsCount);
		IData<ContValue> data = cp.getData();
		CachedScheduler<ContValue> scheduler = new CachedScheduler(cp, new DummyPriorityCalculator(data), 10,
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
		ContinuousProject cp = populate(objectsCount);
		IData<ContValue> data = cp.getData();
		CachedScheduler<ContValue> scheduler = new CachedScheduler(cp, new DummyPriorityCalculator(data), 10,
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

	public static class DummyPriorityCalculator<T> extends AssignCountPriorityCalculator<T> {

		public DummyPriorityCalculator(IData<T> data) {
			this.data = data;
		}
	}
}
