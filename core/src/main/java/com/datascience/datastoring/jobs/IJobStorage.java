package com.datascience.datastoring.jobs;

import com.datascience.core.base.Project;
import com.google.gson.JsonObject;

/**
 *
 * @author konrad
 */
public interface IJobStorage {

	<T extends Project> Job<T>  get(String id) throws Exception;
	<T extends Project> Job<T> create(String type, String id, JsonObject settings) throws Exception;
	void remove(Job job) throws Exception;

	void test() throws Exception;
	void stop() throws Exception;

	void clear() throws Exception;
	void initialize() throws Exception;

}
