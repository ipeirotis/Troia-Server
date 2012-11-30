package com.datascience.gal.service;

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
