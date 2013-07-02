package com.datascience.executor;

/**
 *
 * @author konrad
 */
public class CommandStatus<T> {

	public enum CommandStatusType {
		OK, ERROR, NOT_READY
	}
	private CommandStatusType status;
	private T data;
	private String exception;
	private Double executionTimeInSeconds;
	
	protected CommandStatus(CommandStatusType status, T data, Exception exception, Double executionTimeInSeconds){
		this.status = status;
		this.data = data;
		this.exception = exception != null ? exception.getLocalizedMessage() : "";
		this.executionTimeInSeconds = executionTimeInSeconds;
	}
	
	public T getData(){
		return data;
	}
	
	public CommandStatusType getStatus(){
		return status;
	}
	
	public Exception getError(){
		return new Exception(exception);
	}

	public Double getExecutionTime(){
		return executionTimeInSeconds;
	}
	
	public static<T> CommandStatus<T> errorCommandStatus(Exception ex, Double executionTimeInSeconds){
		return new CommandStatus<T>(CommandStatusType.ERROR, null, ex, executionTimeInSeconds);
	}
	
	public static<T> CommandStatus<T> okCommandStatus(T data, Double executionTimeInSeconds){
		return new CommandStatus<T>(CommandStatusType.OK, data, null, executionTimeInSeconds);
	}
	
	public static<T> CommandStatus<T> notReadyCommandStatus(){
		return new CommandStatus<T>(CommandStatusType.NOT_READY, null, null, 0.);
	}
}
