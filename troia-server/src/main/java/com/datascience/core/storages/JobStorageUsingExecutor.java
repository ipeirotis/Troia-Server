package com.datascience.core.storages;

import com.datascience.core.Job;
import com.datascience.core.base.IData;
import com.datascience.core.base.Project;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.results.IResults;
import com.datascience.executor.ProjectCommandExecutor;
import com.datascience.core.JobsManager;

/**
 *
 * @author konrad
 */
public class JobStorageUsingExecutor implements IJobStorage{

	protected IJobStorage internalStorage;
	protected ProjectCommandExecutor executor;
	protected JobsManager jobsManager;
	
	public JobStorageUsingExecutor(IJobStorage internalStorage, 
			ProjectCommandExecutor executor,
			JobsManager jobsManager){
		this.internalStorage = internalStorage;
		this.executor = executor;
		this.jobsManager = jobsManager;
	}
	
	@Override
	public <T extends Project> Job<T> get(String id) throws Exception {
		// I think we don't need to sync it
		return internalStorage.get(id);
	}

	@Override
	public void add(Job job) throws Exception {
		executor.add(new JobStorageCommands.Adder(internalStorage, jobsManager, job));
	}

	@Override
	public void remove(Job job) throws Exception {
		executor.add(new JobStorageCommands.Remover(internalStorage, jobsManager, job));
	}

	@Override
	public void test() throws Exception {
		internalStorage.test();
	}

	@Override
	public void stop() throws Exception {
		executor.stop();
		internalStorage.stop();
	}

	@Override
	public <T> IData<T> getData(String id) {
		return internalStorage.getData(id);
	}

	@Override
	public INominalData getNominalData(String id) {
		return internalStorage.getNominalData(id);
	}

	@Override
	public IResults getResults(String id) {
		return internalStorage.getResults(id);
	}

	@Override
	public String toString() {
		return internalStorage.toString() + "UsingExecutor";
	}
}
