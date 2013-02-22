package com.datascience.gal.commands;

import com.datascience.core.Job;
import com.datascience.executor.JobCommand;
import com.datascience.gal.AbstractDawidSkene;

/**
 * T is result type
 * @author konrad
 */
public abstract class DSCommandBase<T> extends JobCommand<T> {

	protected AbstractDawidSkene ads;
	
	public DSCommandBase(boolean modifies){
		super(modifies);
	}
	
	@Override
	public void prepareExecution() throws Exception{
		Job job = getJob();
		ads = (AbstractDawidSkene) job.getProject();
	}
}
