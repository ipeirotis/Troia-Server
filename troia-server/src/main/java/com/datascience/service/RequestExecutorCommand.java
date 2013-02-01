package com.datascience.service;

import com.datascience.executor.CommandStatusesContainer;
import com.datascience.executor.CommandStatus;
import com.datascience.gal.commands.DSCommandBase;
import com.datascience.executor.SynchronizedCommand;
import java.util.concurrent.locks.ReadWriteLock;

/**
 *
 * @author konrad
 */
public class RequestExecutorCommand extends SynchronizedCommand{

	String commandId;
	DSCommandBase<Object> command;
	CommandStatusesContainer statusContainer;
	
	public RequestExecutorCommand(String commandId, DSCommandBase<Object> command,
			ReadWriteLock rwLock, CommandStatusesContainer statusContainer){
		super(rwLock, command.modifies());
		this.commandId = commandId;
		this.command = command;
		this.statusContainer = statusContainer;
	}

	@Override
	public void cleanup() {
		super.cleanup();
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
