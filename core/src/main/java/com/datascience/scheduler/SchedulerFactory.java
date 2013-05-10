package com.datascience.scheduler;

import com.datascience.core.nominal.decision.ILabelProbabilityDistributionCostCalculator;
import com.datascience.core.nominal.decision.LabelProbabilityDistributionCostCalculators;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.datascience.serialization.json.JSONUtils.*;
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
	final static Map<String, TimeUnit> PAUSE_UNITS = new HashMap<String, TimeUnit>();

	{
		SCHEDULER_CREATORS.put(Constants.SCHEDULER_NORMAL, getNormalSchedulerCreator());
		SCHEDULER_CREATORS.put(Constants.SCHEDULER_CACHED, getCachedSchedulerCreator());

		CALCULATORS.put(Constants.PC_ASSIGN_COUNT, getPCAssignsCount());
		CALCULATORS.put(Constants.PC_ASSIGN_COUNT2, getPCAssignsCount());
		CALCULATORS.put(Constants.PC_BY_COST, getPCCostBased());
		CALCULATORS.put(Constants.PC_BY_COST2, getPCCostBased());

		SCHEDULERS_FOR_WORKERS.put(Constants.FOR_WORKERS_FIRST_NOT_SEEN, getFirstNotSeen());
		SCHEDULERS_FOR_WORKERS.put(Constants.FOR_WORKERS_CM_BASED, getCostMatrixBased());

		PAUSE_UNITS.put(t("seconds"), TimeUnit.SECONDS);
		PAUSE_UNITS.put(t("minutes"), TimeUnit.MINUTES);
		PAUSE_UNITS.put(t("hours"), TimeUnit.HOURS);
	};

	protected String getID(JsonObject params, String arg){
		return t(params.get(arg).getAsString());
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
				ensureDefaultNumber(params, Constants.PAUSE_DURATION, 10);
				ensureDefaultString(params, Constants.PAUSE_UNIT, "minutes");
				CachedScheduler<T> scheduler = new CachedScheduler<T>();
				scheduler.setUpCache(
						params.get(Constants.PAUSE_DURATION).getAsLong(),
						PAUSE_UNITS.get(t(params.get(Constants.PAUSE_UNIT).getAsString()))
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
				String costFun = getDefaultString(params, Constants.COST_METHOD, null);
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

	public IScheduler<T> create(JsonObject params) {
		String type = getID(params, Constants.SCHEDULER);
		checkArgument(SCHEDULER_CREATORS.containsKey(type), "Unknown scheduler type: " + type);
		IScheduler<T> scheduler = SCHEDULER_CREATORS.get(type).create(params);
		scheduler.setUpQueue(createPriorityCalculator(params));
		scheduler.setSchedulerForWorker(createSchedulerForWorker(params));
		return scheduler;
	}

	protected  <U> U createWithDefault(JsonObject params, String paramName, String defaultValue, Map<String, ? extends Creator<U>> creators, String name){
		ensureDefaultString(params, paramName, defaultValue);
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