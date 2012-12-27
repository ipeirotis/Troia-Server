package com.datascience.gal.service;

import com.datascience.gal.commands.CommandStatusesContainer;
import com.datascience.gal.commands.CommandStatus;
import com.datascience.gal.executor.IExecutorCommand;
import com.datascience.gal.commands.ProjectCommand;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 *
 * @author konrad
 */
public class RequestExecutorCommand implements IExecutorCommand{

	String commandId;
	ProjectCommand<Object> command;
	Lock lock;
	CommandStatusesContainer statusContainer;
	ReadWriteLock rwLock;
	
	public RequestExecutorCommand(String commandId, ProjectCommand<Object> command,
			ReadWriteLock rwLock, CommandStatusesContainer statusContainer){
		this.commandId = commandId;
		this.command = command;
		this.statusContainer = statusContainer;
		this.rwLock = rwLock;
		adjustLock();
	}
	
	private void adjustLock(){
		if (command.modifies()) {
			lock = rwLock.writeLock();
		} else {
			lock = rwLock.readLock();
		}
	}
	
	@Override
	public boolean canStart() {
		return lock.tryLock();
	}

	@Override
	public void cleanup() {
		lock.unlock();
		CommandStatus status;
		if (command.wasOk()){
			status = CommandStatus.okCommandStatus(command.getResult());
		} else {
			status = CommandStatus.errorCommandStatus(command.getError());
		}
		statusContainer.addCommandStatus(commandId, status);
	}

	@Override
	public void run() {
		command.execute();
	}
}
