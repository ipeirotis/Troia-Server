package com.datascience.datastoring.jobs;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.IData;
import com.datascience.core.base.Project;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.results.*;
import com.google.gson.JsonObject;

import java.util.Collection;

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

	IData<ContValue> getContData(String id);
	INominalData getNominalData(String id);
	IResults<ContValue, DatumContResults, WorkerContResults> getContResults(String id);
	IResults<String, DatumResult, WorkerResult> getNominalResults(String id, Collection<String> categories);

}
