package com.datascience.service;

/**
 *
 * @author konrad
 */
public class Constants {

	public static String t(String s){
		return s.toUpperCase();
	}

	public static final String DEPLOY_TIME = "DEPLOY_TIME";
	public static final String JOBS_STORAGE = "JOBS_STORAGE";
	public static final String SERIALIZER = "SERIALIZER";
	public static final String RESPONSER = "RESPONSER";
	public static final String COMMAND_EXECUTOR = "COMMAND_EXECUTOR";
	public static final String COMMAND_STATUSES_CONTAINER = "COMMNAD_STATUSES_CONTAINER";
	public static final String ID_GENERATOR = "ID_GENERATOR";
	public static final String JOBS_MANAGER = "JOBS_MANAGER";
	public static final String DOWNLOADS_PATH = "DOWNLOADS_PATH";
	public static final String PROPERTIES = "PROPERTIES";
	public static final String IS_INITIALIZED = "IS_INITIALIZED";
	public static final String IS_FREEZED = "IS_FREEZED";

	// from config file
	public static final String DB_USER = "DB_USER";
	public static final String DB_PASSWORD = "DB_PASSWORD";
	public static final String CACHE_SIZE = "CACHE_SIZE";
	public static final String CACHE_DUMP_TIME = "CACHE_DUMP_TIME";
	public static final String RESPONSES_CACHE_SIZE = "RESPONSES_CACHE_SIZE";
	public static final String RESPONSES_DUMP_TIME = "RESPONSES_DUMP_TIME";
	public static final String EXECUTOR_THREADS_NUM = "EXECUTOR_THREADS_NUM";
	public static final String FREEZE_CONFIGURATION_AT_START = "FREEZE_CONFIGURATION_AT_START";

}
