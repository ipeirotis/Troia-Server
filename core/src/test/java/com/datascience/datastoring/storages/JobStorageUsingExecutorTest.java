package com.datascience.datastoring.storages;

import com.datascience.core.jobs.IJobStorage;
import com.datascience.executor.ProjectCommandExecutor;
import com.datascience.core.jobs.JobsManager;

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
