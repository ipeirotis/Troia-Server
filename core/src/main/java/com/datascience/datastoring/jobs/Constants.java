package com.datascience.datastoring.jobs;

import static com.datascience.serialization.json.JSONUtils.t;

/**
 * @Author: artur
 */
public class Constants {

	//possible job creation keys
	public static final String ALGORITM = t("algorithm");
	public static final String CATEGORIES = t("categories");
	public static final String CATEGORY_PRIORS = t("categoryPriors");
	public static final String COST_MATRIX = t("costMatrix");
	public static final String ITERATIONS = t("iterations");
	public static final String EPSILON = t("epsilon");
	public static final String SCHEDULER = t("scheduler");

	//possible algorithm types
	public static final String BDS = t("BDS");
	public static final String BLOCKING_EM = t("blockingEM");
	public static final String IDS = t("IDS");
	public static final String ONLINE_EM = t("onlineEM");
	public static final String BMV = t("BMV");
	public static final String BLOCKING_MV = t("blockingMV");
	public static final String IMV = t("IMV");
	public static final String ONLINE_MV = t("onlineMV");

	//possible job types
	public static final String NOMINAL = t("nominal");
	public static final String CONTINUOUS = t("continuous");
	public static final String GALC = t("GALC");
}
