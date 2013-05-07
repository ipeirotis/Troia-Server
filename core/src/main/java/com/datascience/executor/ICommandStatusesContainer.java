package com.datascience.executor;

/**
 * @Author: konrad
 */
public interface ICommandStatusesContainer {

	void addCommandStatus(String id, CommandStatus result);

	CommandStatus getCommandResult(String id);

	String initNewStatus();
}
