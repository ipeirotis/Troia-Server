package com.datascience.datastoring.storages;

import com.datascience.datastoring.jobs.IJobStorage;
import com.datascience.executor.ProjectCommandExecutor;
import com.datascience.datastoring.jobs.JobsManager;

/**
 *
 * @author konrad
 */
public class JobStorageUsingExecutorTest extends ProxyLikeJobStorageTest{
	
	public JobStorageUsingExecutorTest(){
		timeout = 100;
	}
	
	@Override
	public IJobStorage getProxy(IJobStorage jobStorage){
		return new JobStorageUsingExecutor(jobStorage, new ProjectCommandExecutor(1), new JobsManager());
	}
	
}
