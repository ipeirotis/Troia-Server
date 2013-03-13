package com.datascience.service;

import com.datascience.executor.CommandStatusesContainer;
import com.datascience.executor.CommandStatus;
import com.datascience.core.commands.ProjectCommand;
import com.datascience.executor.SynchronizedCommand;
import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;

/**
 *
 * @author konrad
 */
public class RequestExecutorCommand extends SynchronizedCommand{

	String commandId;
	ProjectCommand command;
	CommandStatusesContainer statusContainer;
	Double executionTimeInSeconds;
	
	public RequestExecutorCommand(String commandId, ProjectCommand command,
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
			status = CommandStatus.okCommandStatus(command.getResult(), executionTimeInSeconds);
		} else {
			status = CommandStatus.errorCommandStatus(command.getError(), executionTimeInSeconds);
		}
		statusContainer.addCommandStatus(commandId, status);
	}

	@Override
	public void run() {
		Stopwatch stopwatch = new Stopwatch().start();
		command.execute();
		executionTimeInSeconds = stopwatch.elapsedTime(TimeUnit.MILLISECONDS) / 1000.;
	}
}
