package com.datascience.scheduler;

import com.datascience.core.nominal.decision.ILabelProbabilityDistributionCostCalculator;
import com.datascience.core.nominal.decision.LabelProbabilityDistributionCostCalculators;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SchedulerFactory<T> {

	protected interface ISchedulerCreator<T> {

		IScheduler<T> create(JsonObject params);
	}

	protected ISchedulerCreator<T> getNormalSchedulerCreator(){
		return new ISchedulerCreator<T>() {
			@Override
			public IScheduler<T> create(JsonObject params) {
				Scheduler<T> scheduler = new Scheduler<T>();
				scheduler.setUpQueue(createPriorityCalculator(params));
				return scheduler;
			}
		};
	}

	protected ISchedulerCreator<T> getCachedSchedulerCreator(){
		return new ISchedulerCreator<T>() {
			@Override
			public IScheduler<T> create(JsonObject params) {
				if (!params.has("pauseDuration"))
					params.addProperty("pauseDuration", 10);
				if (!params.has("pauseUnit"))
					params.addProperty("pauseUnit", "minutes");
				CachedScheduler<T> scheduler = new CachedScheduler<T>();
				scheduler.setUpQueue(createPriorityCalculator(params));
				scheduler.setUpCache(
						params.get("pauseDuration").getAsLong(),
						PAUSE_UNITS.get(params.get("pauseUnit").getAsString().toLowerCase())
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

		IPriorityCalculator<T> create(JsonObject params);
	}

	protected IPriorityCalculatorCreator<T> getPCAssignsCount(){
		return new IPriorityCalculatorCreator<T>() {
			@Override
			public IPriorityCalculator<T> create(JsonObject params) {
				return new AssignCountPriorityCalculator<T>();
			}
		};
	}

	protected IPriorityCalculatorCreator<T> getPCCostBased(){
		return new IPriorityCalculatorCreator<T>() {
			@Override
			public IPriorityCalculator<T> create(JsonObject params) {
				String costFun = params.get("costMethod").getAsString();
				ILabelProbabilityDistributionCostCalculator ilpcc =
						LabelProbabilityDistributionCostCalculators.get(costFun);
				// XXX This is not safe ...
				return (IPriorityCalculator<T>) new CostBasedPriorityCalculator(ilpcc);
			}
		};
	}

	final static Map<String, IPriorityCalculatorCreator> CALCULATORS = new HashMap();
	{
		CALCULATORS.put("countassigns", getPCAssignsCount());
		CALCULATORS.put("costbased", getPCCostBased());
	};

	final static Map<String, TimeUnit> PAUSE_UNITS = new HashMap<String, TimeUnit>();
	{
		PAUSE_UNITS.put("seconds", TimeUnit.SECONDS);
		PAUSE_UNITS.put("minutes", TimeUnit.MINUTES);
		PAUSE_UNITS.put("hours", TimeUnit.HOURS);
	}

	public IScheduler<T> create(JsonObject params) {
		String type = params.get("scheduler").getAsString().toLowerCase();
		ISchedulerCreator<T> creator = SCHEDULER_CREATORS.get(type);
		if (creator == null) {
			throw new IllegalArgumentException("Unknown scheduler type: " + type);
		}
		return creator.create(params);
	}

	public IPriorityCalculator<T> createPriorityCalculator(JsonObject params){
		if (!params.has("calculator")){
			params.addProperty("calculator", "countassigns");
		}
		String pcKind = params.get("calculator").getAsString().toLowerCase();
		IPriorityCalculatorCreator<T> creator = CALCULATORS.get(pcKind);
		if (creator == null){
			throw new IllegalArgumentException("Unknown priority calculator: " + pcKind);
		}
		return creator.create(params);
	}
}