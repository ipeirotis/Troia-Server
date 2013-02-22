package com.datascience.gal.commands;

import com.datascience.core.Job;
import com.datascience.executor.ProjectCommand;
import com.datascience.gal.AbstractDawidSkene;

/**
 * T is result type
 * @author konrad
 */
public abstract class DSCommandBase<T> extends ProjectCommand<T> {

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
