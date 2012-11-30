package com.datascience.core.storages;

import com.datascience.core.Job;

/**
 *
 * @author konrad
 */
public interface IJobStorage {

	Job get(String id) throws Exception;
	void add(Job job) throws Exception;
	void remove(String id) throws Exception;
	void test() throws Exception;
	void stop() throws Exception;
}
