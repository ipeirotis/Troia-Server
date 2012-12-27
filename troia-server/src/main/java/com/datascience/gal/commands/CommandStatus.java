package com.datascience.gal.commands;

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
	private Exception exception;
	
	protected CommandStatus(CommandStatusType status, T data, Exception exception){
		this.status = status;
		this.data = data;
		this.exception = exception;
	}
	
	public T getData(){
		return data;
	}
	
	public CommandStatusType getStatus(){
		return status;
	}
	
	public Exception getError(){
		return exception;
	}
	
	public static<T> CommandStatus<T> errorCommandStatus(Exception ex){
		return new CommandStatus<T>(CommandStatusType.ERROR, null, ex);
	}
	
	public static<T> CommandStatus<T> okCommandStatus(T data){
		return new CommandStatus<T>(CommandStatusType.OK, data, null);
	}
	
	public static<T> CommandStatus<T> notReadyCommandStatus(){
		return new CommandStatus<T>(CommandStatusType.NOT_READY, null, null);
	}
}
