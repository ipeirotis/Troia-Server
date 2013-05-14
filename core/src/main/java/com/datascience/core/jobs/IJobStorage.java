package com.datascience.core.jobs;

import com.datascience.core.jobs.Job;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.IData;
import com.datascience.core.base.Project;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.results.*;

import java.sql.SQLException;
import java.util.Collection;

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

	void clearAndInitialize() throws SQLException;

	IData<ContValue> getContData(String id);
	INominalData getNominalData(String id);
	IResults<ContValue, DatumContResults, WorkerContResults> getContResults(String id);
	IResults<String, DatumResult, WorkerResult> getNominalResults(String id, Collection<String> categories);
}
