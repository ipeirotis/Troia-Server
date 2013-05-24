package com.datascience.datastoring.jobs;

import com.datascience.core.base.Project;
import com.datascience.serialization.ISerializer;
import com.google.gson.JsonObject;

/**
 * @Author: konrad
 */
public abstract class BaseJobStorage implements IJobStorage {

	protected JobFactory jobFactory;

	public BaseJobStorage(ISerializer serializer, IJobDataLoader jobDataLoader){
		jobFactory = new JobFactory(serializer, jobDataLoader);
	}

	@Override
	public <T extends Project> Job<T>  create(String type, String id, JsonObject settings) throws Exception {
		return jobFactory.create(type, settings, id);
	}

}
