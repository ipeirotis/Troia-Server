package com.datascience.executor;

import com.datascience.core.Job;
import com.datascience.core.storages.IJobStorage;

/**
 * T is result type
 * @author artur
 */
public abstract class JobCommand<T> extends ProjectCommand<T> {

	protected IJobStorage jobStorage;
	protected String jobId;

	public JobCommand(boolean modifies) {
		super(modifies);
	}
	
	protected Job getJob() throws Exception {
		if (jobId == null)
			throw new IllegalStateException("No jobid");
		if (jobStorage == null)
			throw new IllegalStateException("No jobStorage");
		Job tmp_job = jobStorage.get(jobId);
		if (tmp_job == null){ // || !expectedClass.isAssignableFrom(tmp_job.getProject().getClass())) {
			throw new IllegalArgumentException("Job with ID " + jobId + " does not exist or is of different kind");
		}
		return tmp_job;
	}
	
	public void setJobId(String jid){
		jobId = jid;
	}
	
	public void setJobStorage(IJobStorage js){
		jobStorage = js;
	}
}

