package com.datascience.scheduler;

/**
 * @Author: konrad
 */
public class Constants {

	public static String t(String def){
		return def.toLowerCase();
	}

	public static String SCHEDULER = t("Scheduler");
	public static String PRIORITY_CALCULATOR = t("PriorityCalculator");
	public static String SCHEDULER_FOR_WORKERS = t("SchedulerForWorkers");

	public static String SCHEDULER_NORMAL = t("NormalScheduler");
	public static String SCHEDULER_CACHED = t("CachedScheduler");

	public static String PC_ASSIGN_COUNT = t("ByAssigns");
	public static String PC_BY_COST = t("ByCost");

	public static String FOR_WORKERS_CM_BASED = t("ConfusionMatrixBased");
	public static String FOR_WORKERS_FIRST_NOT_SEEN = t("FirstNotSeen");

	public static String COST_METHOD = t("CostMethod");

}
