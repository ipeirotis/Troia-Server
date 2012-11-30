package com.datascience.gal.commands;

import com.datascience.gal.service.IRandomUniqIDGenerator;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Remember that this must be synchronized
 * @author konrad
 */
public class CommandStatusesContainer {
	
	Cache<String, CommandStatus> commandsResults;
	IRandomUniqIDGenerator idGenerator;
	
	public CommandStatusesContainer(IRandomUniqIDGenerator idGenerator){
		commandsResults = CacheBuilder.newBuilder()
			.maximumSize(200)
			.expireAfterWrite(1, TimeUnit.DAYS)
			.build();
		this.idGenerator = idGenerator;
	}
	
	public String getNextResultId(){
		return idGenerator.getID();
	}
	
	public void addCommandStatus(String id, CommandStatus result){
		commandsResults.put(id, result);
	}
	
	public CommandStatus getCommandResult(String id){
		return commandsResults.getIfPresent(id);
	}
	
	public String initNewStatus(){
		String id = getNextResultId();
		addCommandStatus(id, CommandStatus.notReadyCommandStatus());
		return id;
	}
}
