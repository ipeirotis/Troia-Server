package com.datascience.datastoring.jobs;

import com.datascience.core.base.Project;
import com.datascience.datastoring.IBackendAdapter;
import com.datascience.serialization.ISerializer;
import com.google.gson.JsonObject;

/**
 * Could use <T extends backendAdapter> but using name backendAdapter would be missleading
 * It would be even better to turn it into wrapper but than we couldn't implement more (interface etc.)
 * @Author: konrad
 */
public abstract class BaseJobStorage implements IJobStorage {

	protected IBackendAdapter backendAdapter;
	protected JobFactory jobFactory;

	public BaseJobStorage(IBackendAdapter backendAdapter, ISerializer serializer){
		this(backendAdapter);
		jobFactory = new JobFactory(serializer, this);
	}

	protected <T extends Project> Job<T> createJob(String type, String id, JsonObject settings){
		return jobFactory.create(type, settings, id);
	}

	@Override
	public void setJobFactory(JobFactory jobFactory){
		this.jobFactory = jobFactory;
	}
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