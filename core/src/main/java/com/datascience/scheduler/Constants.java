package com.datascience.scheduler;

import static com.datascience.serialization.json.JSONUtils.t;

/**
 * @Author: konrad
 */
public class Constants {

	public static final String SCHEDULER = t("Scheduler");

	//scheduler types
	public static final String SCHEDULER_NORMAL = t("NormalScheduler");
	public static final String SCHEDULER_CACHED = t("CachedScheduler");

	//priortiy calculators
	public static final String PC_ASSIGN_COUNT = t("ByAssigns");
	public static final String PC_ASSIGN_COUNT2 = t("countassigns");
	public static final String PC_BY_COST = t("ByCost");
	public static final String PC_BY_COST2 = t("costbased");

	public static final String FOR_WORKERS_CM_BASED = t("ConfusionMatrixBased");
	public static final String FOR_WORKERS_FIRST_NOT_SEEN = t("FirstNotSeen");

	//possible scheduler params keys
	public static final String COST_METHOD = t("CostMethod");
	public static final String PRIORITY_CALCULATOR = t("PriorityCalculator");
	public static final String SCHEDULER_FOR_WORKERS = t("SchedulerForWorkers");
	public static final String PAUSE_DURATION = t("pauseDuration");
	public static final String PAUSE_UNIT = t("pauseUnit");
}
