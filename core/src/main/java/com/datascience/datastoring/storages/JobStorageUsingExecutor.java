package com.datascience.datastoring.storages;

import com.datascience.core.jobs.IJobStorage;
import com.datascience.core.jobs.Job;
import com.datascience.core.base.Project;
import com.datascience.executor.ProjectCommandExecutor;
import com.datascience.core.jobs.JobsManager;

/**
 *
 * @author konrad
 */
public class JobStorageUsingExecutor extends WrappedJobStorage{

	protected ProjectCommandExecutor executor;
	protected JobsManager jobsManager;
	
	public JobStorageUsingExecutor(IJobStorage internalStorage,
			ProjectCommandExecutor executor,
			JobsManager jobsManager){
		super(internalStorage);
		this.executor = executor;
		this.jobsManager = jobsManager;
	}
	
	@Override
	public <T extends Project> Job<T> get(String id) throws Exception {
		// I think we don't need to sync it
		return wrappedJobStorage.get(id);
	}

	@Override
	public void add(Job job) throws Exception {
		executor.add(new JobStorageCommands.Adder(wrappedJobStorage, jobsManager, job));
	}

	@Override
	public void remove(Job job) throws Exception {
		executor.add(new JobStorageCommands.Remover(wrappedJobStorage, jobsManager, job));
	}

	@Override
	public void test() throws Exception {
		wrappedJobStorage.test();
	}

	@Override
	public void stop() throws Exception {
		executor.stop();
		wrappedJobStorage.stop();
	}

	@Override
	public String toString() {
		return wrappedJobStorage.toString() + "UsingExecutor";
	}
}
