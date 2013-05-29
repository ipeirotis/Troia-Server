package com.datascience.datastoring.storages;

import com.datascience.datastoring.jobs.IJobStorage;
import com.datascience.datastoring.jobs.Job;
import com.datascience.core.base.Project;
import com.datascience.executor.ProjectCommandExecutor;
import com.datascience.datastoring.jobs.JobsLocksManager;

/**
 *
 * @author konrad
 */
public class JobStorageUsingExecutor extends WrappedJobStorage{

	protected ProjectCommandExecutor executor;
	protected JobsLocksManager jobsLocksManager;
	
	public JobStorageUsingExecutor(IJobStorage internalStorage,
			ProjectCommandExecutor executor,
			JobsLocksManager jobsLocksManager){
		super(internalStorage);
		this.executor = executor;
		this.jobsLocksManager = jobsLocksManager;
	}
	
	@Override
	public <T extends Project> Job<T> get(String id) throws Exception {
		// I think we don't need to sync it
		return wrappedJobStorage.get(id);
	}

	@Override
	public void add(Job job) throws Exception {
		executor.add(new JobStorageCommands.Adder(wrappedJobStorage, jobsLocksManager, job));
	}

	@Override
	public void remove(Job job) throws Exception {
		executor.add(new JobStorageCommands.Remover(wrappedJobStorage, jobsLocksManager, job));
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
