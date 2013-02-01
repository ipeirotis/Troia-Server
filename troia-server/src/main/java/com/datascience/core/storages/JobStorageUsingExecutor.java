package com.datascience.core.storages;

import com.datascience.core.Job;
import com.datascience.gal.commands.JobStorageCommands;
import com.datascience.executor.ProjectCommandExecutor;

/**
 *
 * @author konrad
 */
public class JobStorageUsingExecutor implements IJobStorage{

	protected IJobStorage internalStorage;
	protected ProjectCommandExecutor executor;
	
	public JobStorageUsingExecutor(IJobStorage internalStorage, ProjectCommandExecutor executor){
		this.internalStorage = internalStorage;
		this.executor = executor;
	}
	
	@Override
	public Job get(String id) throws Exception {
		// I think we don't need to sync it
		return internalStorage.get(id);
	}

	@Override
	public void add(Job job) throws Exception {
		executor.add(new JobStorageCommands.Adder(internalStorage, job));
	}

	@Override
	public void remove(Job job) throws Exception {
		executor.add(new JobStorageCommands.Remover(internalStorage, job));
	}

	@Override
	public void test() throws Exception {
		internalStorage.test();
	}

	@Override
	public void stop() throws Exception {
		internalStorage.stop();
	}
	
	@Override
	public String toString() {
		return internalStorage.toString() + "UsingExecutor";
	}
}
