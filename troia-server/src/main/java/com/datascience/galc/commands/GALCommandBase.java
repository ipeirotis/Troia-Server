package com.datascience.galc.commands;

import com.datascience.core.Job;
import com.datascience.executor.ProjectCommand;
import com.datascience.galc.ContinuousProject;
import com.datascience.service.JobsManager;

/**
 * @Author: konrad
 */
public abstract class GALCommandBase<T> extends ProjectCommand<T> {

	protected ContinuousProject project;

	public GALCommandBase(boolean modifies){
		super(modifies);
	}
	
	@Override
	public void prepareExecution() throws Exception{
		Job job = getJob();
		project = (ContinuousProject) job.getProject();
	}
}
