package com.datascience.datastoring.jobs;

import com.datascience.core.base.Project;
import com.google.gson.JsonObject;

import static com.google.common.base.Preconditions.checkArgument;


public class JobsManager {

	protected IJobStorage jobStorage;
	protected JobFactory jobFactory;

	public JobsManager(IJobStorage jobStorage, JobFactory jobFactory){
		this.jobStorage = jobStorage;
		jobStorage.setJobFactory(jobFactory);
		this.jobFactory = jobFactory;
	}

	public synchronized <T extends Project> Job<T> get(String id) throws Exception {
		return jobStorage.get(id);
	}

	public synchronized <T extends Project> Job<T> createAndAdd(String type, String id, JsonObject settings)
			throws Exception {
		Job<T> job = jobStorage.get(id);
		checkArgument(job == null, "Job with id = " + id + " already exists!");
		job = jobFactory.create(type, settings, id);
		jobStorage.add(job);
		return job;
	}

	public synchronized void remove(Job job) throws Exception {
		jobStorage.remove(job);
	}

	public synchronized void test() throws Exception {
		jobStorage.test();
	}

	public synchronized void stop() throws Exception {
		jobStorage.stop();
	}

	public synchronized void rebuild() throws Exception {
		try {
			jobStorage.clear();
		} catch (Exception ex){}
		jobStorage.initialize();
	}

	@Override
	public String toString(){
		return "JM:" + jobStorage;
	}
}
