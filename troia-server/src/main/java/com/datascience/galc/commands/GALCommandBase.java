package com.datascience.galc.commands;

import com.datascience.executor.ProjectCommand;
import com.datascience.galc.ContinuousProject;

/**
 * @Author: konrad
 */
public abstract class GALCommandBase<T> extends ProjectCommand<T> {

	protected ContinuousProject project;

	public GALCommandBase(ContinuousProject project, boolean modifies){
		super(modifies);
		this.project = project;
	}
}
