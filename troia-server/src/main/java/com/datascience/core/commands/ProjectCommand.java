package com.datascience.core.commands;


/**
 * T is result type
 * @author konrad
 */
public abstract class ProjectCommand<T> {

	final boolean modifies;
	private T result;
	private Exception exception;
	
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
	
	public boolean wasOk(){
		return exception == null;
	}
}

