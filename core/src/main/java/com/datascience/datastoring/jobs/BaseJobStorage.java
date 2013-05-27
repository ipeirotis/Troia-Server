package com.datascience.datastoring.jobs;

import com.datascience.datastoring.IBackendAdapter;


/**
 * Could use <T extends backendAdapter> but using name backendAdapter would be missleading
 * @Author: konrad
 */
public abstract class BaseJobStorage implements IJobStorage {

	protected IBackendAdapter backendAdapter;

	public BaseJobStorage(IBackendAdapter backendAdapter){
		this.backendAdapter = backendAdapter;
	}

	@Override
	public void test() throws Exception{
		backendAdapter.getBackend().test();
	}

	@Override
	public void stop() throws Exception{
		backendAdapter.getBackend().stop();
	}

	@Override
	public void clear() throws Exception{
		backendAdapter.clear();
	}

	@Override
	public void initialize() throws Exception{
		backendAdapter.rebuild();
	}

}
