package com.datascience.scheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SchedulerFactory<T> {

	protected interface ISchedulerCreator<T> {

		IScheduler<T> create(Map<String, String> params);
	}

	protected ISchedulerCreator<T> getNormalSchedulerCreator(){
		return new ISchedulerCreator<T>() {
			@Override
			public IScheduler<T> create(Map<String, String> params) {
				Scheduler<T> scheduler = new Scheduler<T>();
				scheduler.setUpQueue(createPriorityCalculator(params));
				return scheduler;
			}
		};
	}

	protected ISchedulerCreator<T> getCachedSchedulerCreator(){
		return new ISchedulerCreator<T>() {
			@Override
			public IScheduler<T> create(Map<String, String> params) {
				CachedScheduler<T> scheduler = new CachedScheduler<T>();
				scheduler.setUpQueue(createPriorityCalculator(params));
				scheduler.setUpCache(
						Long.parseLong(params.get("pauseDuration")),
						PAUSE_UNITS.get(params.get("pauseUnit").toLowerCase())
				);
				return scheduler;
			}
		};
	}

	final static Map<String, ISchedulerCreator> SCHEDULER_CREATORS = new HashMap<String, ISchedulerCreator>();
	{
		SCHEDULER_CREATORS.put("scheduler", getNormalSchedulerCreator());
		SCHEDULER_CREATORS.put("cachedscheduler", getCachedSchedulerCreator());
	};


	protected interface IPriorityCalculatorCreator<T> {

		IPriorityCalculator<T> create(Map<String, String> params);
	}

	protected IPriorityCalculatorCreator<T> getPCAssignsCount(){
		return new IPriorityCalculatorCreator<T>() {
			@Override
			public IPriorityCalculator<T> create(Map<String, String> params) {
				return new AssignCountPriorityCalculator<T>();
			}
		};
	}

	final static Map<String, IPriorityCalculatorCreator> CALCULATORS = new HashMap();
	{
		CALCULATORS.put("countassigns", getPCAssignsCount());
	};

	final static Map<String, TimeUnit> PAUSE_UNITS = new HashMap<String, TimeUnit>();
	{
		PAUSE_UNITS.put("seconds", TimeUnit.SECONDS);
		PAUSE_UNITS.put("minutes", TimeUnit.MINUTES);
		PAUSE_UNITS.put("hours", TimeUnit.HOURS);
	}

	public IScheduler<T> create(Map<String, String> params) {
		String type = params.get("scheduler").toLowerCase();
		ISchedulerCreator<T> creator = SCHEDULER_CREATORS.get(type);
		if (creator == null) {
			throw new IllegalArgumentException("Unknown scheduler type: " + type);
		}
		return creator.create(params);
	}

	public IPriorityCalculator<T> createPriorityCalculator(Map<String, String> params){
		String pcKind = params.get("calculator").toLowerCase();
		IPriorityCalculatorCreator<T> creator = CALCULATORS.get(pcKind);
		if (creator == null){
			throw new IllegalArgumentException("Unknown priority calculator: " + pcKind);
		}
		return creator.create(params);
	}
}