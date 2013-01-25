package com.datascience.gal.service;

import com.datascience.gal.commands.CommandStatusesContainer;
import com.datascience.gal.commands.CommandStatus;
import com.datascience.gal.commands.ProjectCommand;
import com.datascience.gal.executor.SynchronizedJobCommand;
import java.util.concurrent.locks.ReadWriteLock;

/**
 *
 * @author konrad
 */
public class RequestExecutorCommand extends SynchronizedJobCommand{

	String commandId;
	ProjectCommand<Object> command;
	CommandStatusesContainer statusContainer;
	
	public RequestExecutorCommand(String commandId, ProjectCommand<Object> command,
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
