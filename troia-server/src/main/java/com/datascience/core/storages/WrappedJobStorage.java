package com.datascience.core.storages;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.IData;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.results.*;
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
	public <T> IData<T> getData(String id) {
		return wrappedJobStorage.getData(id);
	}

	@Override
	public INominalData getNominalData(String id) {
		return wrappedJobStorage.getNominalData(id);
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
