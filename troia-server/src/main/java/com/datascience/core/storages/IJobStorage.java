package com.datascience.core.storages;

import com.datascience.core.Job;

/**
 *
 * @author konrad
 */
public interface IJobStorage {

	<T> Job<T>  get(String id) throws Exception;
	void add(Job job) throws Exception;
	void remove(Job job) throws Exception;
	void test() throws Exception;
	void stop() throws Exception;
}
