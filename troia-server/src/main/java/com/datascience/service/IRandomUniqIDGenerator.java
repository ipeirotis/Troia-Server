package com.datascience.service;

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
