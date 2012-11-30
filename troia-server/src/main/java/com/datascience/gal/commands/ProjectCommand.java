package com.datascience.gal.commands;

import com.datascience.gal.AbstractDawidSkene;

/**
 * T is result type
 * @author konrad
 */
public abstract class ProjectCommand<T> {
	
	final boolean modifies;
	protected AbstractDawidSkene ads;
	private T result;
	private Exception exception;
	
	public ProjectCommand(AbstractDawidSkene ads, boolean modifies){
		this.ads = ads;
		this.modifies = modifies;
	}
	
	public T getResult(){
		return result;
	}
	
	public Exception getError(){
		return exception;
	}
	
	void setResult(T result){
		this.result = result;
	}
	
	public boolean modifies(){
		return modifies;
	}
	
	public void execute(){
		try {
			realExecute();
		} catch (Exception e) {
			exception = e;
		}
	}
	
	abstract void realExecute();
	
	public boolean wasOk(){
		return exception == null;
	}
}
