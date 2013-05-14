package com.datascience.executor;

import java.util.concurrent.TimeUnit;

import com.datascience.utils.IRandomUniqIDGenerator;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Remember that this must be synchronized
 * @author konrad
 */
public class CachedCommandStatusesContainer extends CommandStatusesContainerBase{
	
	Cache<String, CommandStatus> commandsResults;
	
	public CachedCommandStatusesContainer(IRandomUniqIDGenerator idGenerator, int cacheSize, int expirationTime){
		super(idGenerator);
		commandsResults = CacheBuilder.newBuilder()
			.maximumSize(cacheSize)
			.expireAfterWrite(expirationTime, TimeUnit.SECONDS)
			.build();
	}

	@Override
	public void addCommandStatus(String id, CommandStatus result){
		commandsResults.put(id, result);
	}

	@Override
	public CommandStatus getCommandResult(String id){
		return commandsResults.getIfPresent(id);
	}
}
