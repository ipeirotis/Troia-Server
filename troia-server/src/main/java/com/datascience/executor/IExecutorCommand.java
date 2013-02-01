package com.datascience.executor;

/**
 *
 * @author konrad
 */
public interface IExecutorCommand extends Runnable{
	
	/**
	 * For example try acquire all locks here
	 * @return 
	 */
	boolean canStart();
	
	/**
	 * release those locks
	 */
	void cleanup();
	
}
