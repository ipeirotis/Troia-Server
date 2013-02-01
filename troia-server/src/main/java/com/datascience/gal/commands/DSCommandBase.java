package com.datascience.gal.commands;

import com.datascience.executor.ProjectCommand;
import com.datascience.gal.AbstractDawidSkene;

/**
 * T is result type
 * @author konrad
 */
public abstract class DSCommandBase<T> extends ProjectCommand<T> {

	protected AbstractDawidSkene ads;
	
	public DSCommandBase(AbstractDawidSkene ads, boolean modifies){
		super(modifies);
		this.ads = ads;
	}
}
