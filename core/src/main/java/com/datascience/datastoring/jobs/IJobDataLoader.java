package com.datascience.datastoring.jobs;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.IData;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.results.*;

import java.util.Collection;

/**
 * @Author: konrad
 */
public interface IJobDataLoader {

	IData<ContValue> getContData(String id);
	INominalData getNominalData(String id);
	IResults<ContValue, DatumContResults, WorkerContResults> getContResults(String id);
	IResults<String, DatumResult, WorkerResult> getNominalResults(String id, Collection<String> categories);
}
