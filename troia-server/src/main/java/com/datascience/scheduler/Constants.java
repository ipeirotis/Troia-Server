package com.datascience.scheduler;

/**
 * @Author: konrad
 */
public class Constants {

	public static String t(String def){
		return def.toLowerCase();
	}

	public static final String SCHEDULER = t("Scheduler");
	public static final String PRIORITY_CALCULATOR = t("PriorityCalculator");
	public static final String SCHEDULER_FOR_WORKERS = t("SchedulerForWorkers");

	public static final String SCHEDULER_NORMAL = t("NormalScheduler");
	public static final String SCHEDULER_CACHED = t("CachedScheduler");

	public static final String PC_ASSIGN_COUNT = t("ByAssigns");
	public static final String PC_BY_COST = t("ByCost");

	public static final String FOR_WORKERS_CM_BASED = t("ConfusionMatrixBased");
	public static final String FOR_WORKERS_FIRST_NOT_SEEN = t("FirstNotSeen");

	public static final String COST_METHOD = t("CostMethod");

}
