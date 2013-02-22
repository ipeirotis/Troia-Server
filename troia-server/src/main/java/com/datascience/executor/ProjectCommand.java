package com.datascience.executor;

import com.datascience.core.Job;
import com.datascience.core.storages.IJobStorage;

/**
 * T is result type
 * @author konrad
 */
public abstract class ProjectCommand<T> {

	final boolean modifies;
	private T result;
	private Exception exception;
	protected IJobStorage jobStorage;
	protected String jobId;
	
	public ProjectCommand(boolean modifies){
		this.modifies = modifies;
	}
	
	public T getResult(){
		return result;
	}
	
	public Exception getError(){
		return exception;
	}
	
	protected void setResult(T result){
		this.result = result;
	}
	
	public boolean modifies(){
		return modifies;
	}
	
	public void execute(){
		try {
			prepareExecution();
			realExecute();
		} catch (Exception e) {
			exception = e;
		}
	}
	
	protected abstract void prepareExecution() throws Exception;
	
	protected abstract void realExecute() throws Exception;
	
	protected Job getJob() throws Exception {
		if (jobId == null || jobStorage == null){
			throw new IllegalArgumentException("no jobid or jobsmanager");
		}
		Job tmp_job = jobStorage.get(jobId);
		if (tmp_job == null){ // || !expectedClass.isAssignableFrom(tmp_job.getProject().getClass())) {
			throw new IllegalArgumentException("Job with ID " + jobId + " does not exist or is of different kind");
		}
		return tmp_job;
	}
	
	public boolean wasOk(){
		return exception == null;
	}
	
	public void setJobId(String jid){
		jobId = jid;
	}
	
	public void setJobStorage(IJobStorage js){
		jobStorage = js;
	}
}

