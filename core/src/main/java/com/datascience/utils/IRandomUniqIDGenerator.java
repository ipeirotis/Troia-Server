package com.datascience.utils;

/**
 *
 * @author konrad
 */
public interface IRandomUniqIDGenerator {
	
	/**
	 * Must be thread-safe
	 * @return 
	 */
	String getID();
}
