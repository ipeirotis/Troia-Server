package com.datascience.core.storages;

import com.datascience.executor.ProjectCommandExecutor;
import com.datascience.service.JobsManager;

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
