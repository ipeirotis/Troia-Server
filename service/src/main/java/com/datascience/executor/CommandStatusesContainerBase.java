package com.datascience.executor;

import com.datascience.utils.IRandomUniqIDGenerator;

/**
 * @Author: konrad
 */
public abstract class CommandStatusesContainerBase implements ICommandStatusesContainer {

	IRandomUniqIDGenerator idGenerator;

	public CommandStatusesContainerBase(IRandomUniqIDGenerator idGenerator){
		this.idGenerator = idGenerator;
	}

	public String getNextResultId(){
		return idGenerator.getID();
	}

	public abstract void addCommandStatus(String id, CommandStatus result);

	public abstract CommandStatus getCommandResult(String id);

	public String initNewStatus(){
		String id = getNextResultId();
		addCommandStatus(id, CommandStatus.notReadyCommandStatus());
		return id;
	}
}
