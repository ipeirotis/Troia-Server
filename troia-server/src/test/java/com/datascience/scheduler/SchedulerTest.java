package com.datascience.scheduler;

import com.datascience.core.base.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class SchedulerTest {

	@Test
	public void test() throws InterruptedException {

		Data<ContValue> data = new Data<ContValue>();

		data.addObject(new LObject<ContValue>("object0"));
		data.addObject(new LObject<ContValue>("object1"));
		data.addObject(new LObject<ContValue>("object2"));

		Scheduler<ContValue> scheduler = new Scheduler(data, new PriorityCalculator(data));

		data.addAssign(
				new AssignedLabel<ContValue>(
						new Worker<ContValue>("worker0"),
						new LObject<ContValue>("object0"),
						new ContValue(0.)
				)
		);

		data.addAssign(
				new AssignedLabel<ContValue>(
						new Worker<ContValue>("worker1"),
						new LObject<ContValue>("object0"),
						new ContValue(0.)
				)
		);

		data.addAssign(
				new AssignedLabel<ContValue>(
						new Worker<ContValue>("worker1"),
						new LObject<ContValue>("object1"),
						new ContValue(0.)
				)
		);

		data.addAssign(
				new AssignedLabel<ContValue>(
						new Worker<ContValue>("worker0"),
						new LObject<ContValue>("object2"),
						new ContValue(0.)
				)
		);

		assertEquals(data.getObject("object2"), scheduler.nextObject());
		assertEquals(data.getObject("object1"), scheduler.nextObject());
		assertEquals(data.getObject("object0"), scheduler.nextObject());
	}

	public static class PriorityCalculator implements IPriorityCalculator<ContValue> {

		private Data<ContValue> data;

		public PriorityCalculator(Data<ContValue> data) {
			this.data = data;
		}

		@Override
		public double getPriority(LObject<ContValue> object) {
			return data.getAssignsForObject(object).size();
		}
	}

}
