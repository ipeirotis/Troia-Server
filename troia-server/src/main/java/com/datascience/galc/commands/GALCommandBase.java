package com.datascience.galc.commands;

import com.datascience.core.base.Data;
import com.datascience.executor.ProjectCommand;

/**
 * @Author: konrad
 */
public abstract class GALCommandBase<T> extends ProjectCommand<T> {

	protected Data<Double> data;

	public GALCommandBase(Data<Double> data, boolean modifies){
		super(modifies);
		this.data = data;
	}
}
