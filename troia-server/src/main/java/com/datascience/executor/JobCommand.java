package com.datascience.executor;

import com.datascience.core.Job;
import com.datascience.core.commands.ProjectCommand;
import com.datascience.core.storages.IJobStorage;

/**
 * T is result type
 * @author artur
 */
public abstract class JobCommand<T, U> extends ProjectCommand<T> {

	protected IJobStorage jobStorage;
	protected String jobId;
	protected U project;

	public JobCommand(boolean modifies) {
		super(modifies);
	}
	
	protected Job getJob() throws Exception {
		assertState(jobId != null, "No job ID");
		assertState(jobStorage != null, "No jobStorage set");
		Job tmp_job = jobStorage.get(jobId);
		assertArgument(tmp_job != null, "Job with ID " + jobId + " does not exist or is of different kind");
		// old: ^^^^ || !expectedClass.isAssignableFrom(tmp_job.getProject().getClass())) {
		return tmp_job;
	}
	
	public void setJobId(String jid){
		jobId = jid;
	}
	
	public void setJobStorage(IJobStorage js){
		jobStorage = js;
	}

	protected void prepareExecution() throws Exception{
		project = (U) getJob().getProject();
	}

	protected void assertState(boolean condition, String message){
		if (!condition) throw new IllegalStateException(message);
	}

	protected void assertArgument(boolean condition, String message){
		if (!condition) throw new IllegalArgumentException(message);
	}
}

