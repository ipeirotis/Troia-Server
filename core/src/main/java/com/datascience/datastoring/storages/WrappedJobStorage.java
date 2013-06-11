package com.datascience.datastoring.storages;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.IData;
import com.datascience.core.nominal.INominalModel;
import com.datascience.datastoring.jobs.IJobStorage;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.results.*;
import com.datascience.datastoring.jobs.JobFactory;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 *
 * @author artur
 */
public abstract class WrappedJobStorage implements IJobStorage {

	protected IJobStorage wrappedJobStorage;

	public WrappedJobStorage(final IJobStorage wrappedJobStorage){
		this.wrappedJobStorage = wrappedJobStorage;
	}

	@Override
	public void setJobFactory(JobFactory jobFactory){
		wrappedJobStorage.setJobFactory(jobFactory);
	}

	@Override
	public void clear() throws Exception {
		wrappedJobStorage.clear();
	}

	@Override
	public void initialize() throws Exception {
		wrappedJobStorage.initialize();
	}

	@Override
	public IData<ContValue> getContData(String id) {
		return wrappedJobStorage.getContData(id);
	}

	@Override
	public INominalData getNominalData(String id) {
		return wrappedJobStorage.getNominalData(id);
	}

	@Override
	public INominalModel getNominalModel(String id, Type t) {
		return wrappedJobStorage.getNominalModel(id, t);
	}

	@Override
	public IResults<ContValue, DatumContResults, WorkerContResults> getContResults(String id) {
		return wrappedJobStorage.getContResults(id);
	}

	@Override
	public IResults<String, DatumResult, WorkerResult> getNominalResults(String id, Collection<String> categories) {
		return wrappedJobStorage.getNominalResults(id, categories);
	}
}
