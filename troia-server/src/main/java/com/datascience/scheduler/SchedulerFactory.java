package com.datascience.scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SchedulerFactory<T> {

	protected interface ICreator<T> {

		IScheduler<T> create(Map<String, String> params);
	}

	final static Map<String, ICreator> CREATORS = new HashMap<String, ICreator>();
	{
		CREATORS.put(Scheduler.class.getName(), new ICreator<T>() {

			@Override
			public IScheduler<T> create(Map<String, String> params) {
				Scheduler<T> scheduler = new Scheduler<T>();
				scheduler.setUpQueue(CALCULATORS.get(params.get("calculator")));
				return scheduler;
			}
		});
		CREATORS.put(CachedScheduler.class.getName(), new ICreator<T>() {

			@Override
			public IScheduler<T> create(Map<String, String> params) {
				CachedScheduler<T> scheduler = new CachedScheduler<T>();
				scheduler.setUpQueue(CALCULATORS.get(params.get("calculator")));
				scheduler.setUpCache(
						Long.parseLong(params.get("pauseDuration")),
						PAUSE_UNITS.get(params.get("pauseUnit"))
				);
				return scheduler;
			}
		});
	};

	final static Map<String, IPriorityCalculator> CALCULATORS = new HashMap();
	{
		CALCULATORS.put(AssignCountPriorityCalculator.class.getName(), new AssignCountPriorityCalculator<T>());
	};

	final static Map<String, TimeUnit> PAUSE_UNITS = new HashMap<String, TimeUnit>();
	{
		PAUSE_UNITS.put("SECONDS", TimeUnit.SECONDS);
		PAUSE_UNITS.put("MINUTES", TimeUnit.MINUTES);
		PAUSE_UNITS.put("HOURS", TimeUnit.HOURS);
	}

	public IScheduler<T> create(Map<String, String> params) {
		String type = params.get("scheduler");
		ICreator<T> creator = CREATORS.get(type);
		if (creator == null) {
			throw new IllegalArgumentException("Unknown type: " + type);
		}
		return creator.create(params);
	}
}