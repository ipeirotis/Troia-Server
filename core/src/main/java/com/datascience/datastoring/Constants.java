package com.datascience.datastoring;

/**
 *
 * @author konrad
 */
public class Constants {

	public static final String t(String constant){
		return constant.toUpperCase();
	}
	
	// from config file
	public static final String DB_NAME = "DB_NAME";
	public static final String DB_URL = "DB_URL";
	public static final String DB_DRIVER_CLASS = "DB_DRIVER_CLASS";
	public static final String MEMCACHE_URL = "MEMCACHE_URL";
	public static final String MEMCACHE_PORT = "MEMCACHE_PORT";
	public static final String MEMCACHE_EXPIRATION_TIME = "MEMCACHE_EXPIRATION_TIME";
}
