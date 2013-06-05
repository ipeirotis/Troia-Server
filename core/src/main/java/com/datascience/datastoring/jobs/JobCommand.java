package com.datascience.datastoring.jobs;

import com.datascience.core.commands.ProjectCommand;

/**
 * T is result type
 * @author artur
 */
public abstract class JobCommand<T, U> extends ProjectCommand<T> {

	protected JobsManager jobsManager;
	protected String jobId;
	protected Job job;

	protected U project;

	public JobCommand(boolean modifies) {
		super(modifies);
	}
	
	protected Job getJob() throws Exception {
		assertState(jobId != null, "No job ID");
		assertState(jobsManager != null, "No jobStorage set");
		job = jobsManager.get(jobId);
		assertArgument(job != null, "Job with ID " + jobId + " does not exist or is of different kind");
		// old: ^^^^ || !expectedClass.isAssignableFrom(tmp_job.getProject().getClass())) {
		return job;
	}
	
	public void setJobId(String jid){
		jobId = jid;
	}
	
	public void setJobsManager(JobsManager jobsManager){
		this.jobsManager = jobsManager;
	}

	@Override
	protected void prepareExecution() throws Exception{
		project = (U) getJob().getProject();
	}

	@Override
	protected void afterExecution() throws Exception {
		if (modifies())
			jobsManager.update(job);
	}

	protected void assertState(boolean condition, String message){
		if (!condition) throw new IllegalStateException(message);
	}

	protected void assertArgument(boolean condition, String message){
		if (!condition) throw new IllegalArgumentException(message);
	}
}

