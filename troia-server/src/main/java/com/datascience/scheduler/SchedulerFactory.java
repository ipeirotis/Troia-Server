package com.datascience.scheduler;

import com.datascience.core.nominal.decision.ILabelProbabilityDistributionCostCalculator;
import com.datascience.core.nominal.decision.LabelProbabilityDistributionCostCalculators;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;

public class SchedulerFactory<T> {

	protected interface Creator<U> {
		U create(JsonObject params);
	}

	protected interface ISchedulerCreator<T> extends Creator<IScheduler<T>>{}
	protected interface IPriorityCalculatorCreator<T> extends Creator<IPriorityCalculator<T>>{}
	protected interface ISchedulerForWorkerCreator<T> extends Creator<ISchedulerForWorker<T>>{}

	protected Map<String, ISchedulerCreator<T>> SCHEDULER_CREATORS = new HashMap<String, ISchedulerCreator<T>>();
	protected Map<String, IPriorityCalculatorCreator<T>> CALCULATORS = new HashMap<String, IPriorityCalculatorCreator<T>>();
	protected Map<String, ISchedulerForWorkerCreator<T>> SCHEDULERS_FOR_WORKERS = new HashMap<String, ISchedulerForWorkerCreator<T>>();

	{
		SCHEDULER_CREATORS.put(Constants.SCHEDULER_NORMAL, getNormalSchedulerCreator());
		SCHEDULER_CREATORS.put(Constants.SCHEDULER_CACHED, getCachedSchedulerCreator());

		CALCULATORS.put(Constants.PC_ASSIGN_COUNT, getPCAssignsCount());
		CALCULATORS.put(Constants.PC_BY_COST, getPCCostBased());

		SCHEDULERS_FOR_WORKERS.put(Constants.FOR_WORKERS_FIRST_NOT_SEEN, getFirstNotSeen());
		SCHEDULERS_FOR_WORKERS.put(Constants.FOR_WORKERS_CM_BASED, getCostMatrixBased());
	};

	protected String getID(JsonObject params, String arg){
		return Constants.t(params.get(arg).getAsString());
	}

	protected void ensureDefault(JsonObject params, String paramName, String paramValue){
		if (!params.has(paramName)){
			params.addProperty(paramName, paramValue);
		}
	}


	protected ISchedulerCreator<T> getNormalSchedulerCreator(){
		return new ISchedulerCreator<T>() {
			@Override
			public IScheduler<T> create(JsonObject params) {
				return new Scheduler<T>();
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
				scheduler.setUpCache(
						params.get("pauseDuration").getAsLong(),
						PAUSE_UNITS.get(params.get("pauseUnit").getAsString().toLowerCase())
				);
				return scheduler;
			}
		};
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
				String costFun = getID(params, Constants.COST_METHOD);
				ILabelProbabilityDistributionCostCalculator ilpcc =
						LabelProbabilityDistributionCostCalculators.get(costFun);
				// XXX This is not safe ...
				return (IPriorityCalculator<T>) new CostBasedPriorityCalculator(ilpcc);
			}
		};
	}


	protected ISchedulerForWorkerCreator<T> getCostMatrixBased(){
		return new ISchedulerForWorkerCreator<T>() {

			@Override
			public ISchedulerForWorker<T> create(JsonObject params) {
				return (ISchedulerForWorker<T>) new SchedulersForWorker.ConfusionMatrixBased();
			}
		};
	}

	protected ISchedulerForWorkerCreator<T> getFirstNotSeen(){
		return new ISchedulerForWorkerCreator<T>() {

			@Override
			public ISchedulerForWorker<T> create(JsonObject params) {
				return new SchedulersForWorker.FirstNotSeen<T>();
			}
		};
	}


	final static Map<String, TimeUnit> PAUSE_UNITS = new HashMap<String, TimeUnit>();
	{
		PAUSE_UNITS.put("seconds", TimeUnit.SECONDS);
		PAUSE_UNITS.put("minutes", TimeUnit.MINUTES);
		PAUSE_UNITS.put("hours", TimeUnit.HOURS);
	}


	public IScheduler<T> create(JsonObject params) {
		String type = getID(params, Constants.SCHEDULER);
		ISchedulerCreator<T> creator = SCHEDULER_CREATORS.get(type);
		if (creator == null) {
			throw new IllegalArgumentException("Unknown scheduler type: " + type);
		}
		IScheduler<T> scheduler = creator.create(params);
		scheduler.setUpQueue(createPriorityCalculator(params));
		scheduler.setSchedulerForWorker(createSchedulerForWorker(params));
		return scheduler;
	}

	protected  <U> U createWithDefault(JsonObject params, String paramName, String defaultValue, Map<String, ? extends Creator<U>> creators, String name){
		ensureDefault(params, paramName, defaultValue);
		String kind = getID(params, paramName);
		Creator<U> creator = creators.get(kind);
		checkArgument(creator != null, "Unknown %s: %s", name, kind);
		return creator.create(params);
	}

	public IPriorityCalculator<T> createPriorityCalculator(JsonObject params){
		return createWithDefault(params, Constants.PRIORITY_CALCULATOR, Constants.PC_ASSIGN_COUNT,
				CALCULATORS, "priority calculator");
	}

	public ISchedulerForWorker<T> createSchedulerForWorker(JsonObject params){
		return createWithDefault(params, Constants.SCHEDULER_FOR_WORKERS, Constants.FOR_WORKERS_FIRST_NOT_SEEN,
				SCHEDULERS_FOR_WORKERS, "scheduler for workers");
	}



}