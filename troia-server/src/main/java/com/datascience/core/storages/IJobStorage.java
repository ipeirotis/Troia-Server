package com.datascience.core.storages;

import com.datascience.core.Job;
import com.datascience.core.base.IData;
import com.datascience.core.base.Project;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.results.IResults;

/**
 *
 * @author konrad
 */
public interface IJobStorage {

	<T extends Project> Job<T>  get(String id) throws Exception;
	void add(Job job) throws Exception;
	void remove(Job job) throws Exception;
	void test() throws Exception;
	void stop() throws Exception;

	<T> IData<T> getData(String id);
	INominalData getNominalData(String id);
	IResults getResults(String id);
}
