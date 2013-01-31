package com.datascience.executor;

import java.util.concurrent.TimeUnit;

import com.datascience.service.IRandomUniqIDGenerator;
import com.datascience.service.ISerializer;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class SerializedCommandStatusesContainer extends
		CommandStatusesContainer {

	ISerializer serializer;
	Cache<String, String> commandsResults;
	
	public SerializedCommandStatusesContainer(IRandomUniqIDGenerator idGenerator, ISerializer serializer) {
		super(idGenerator);
		commandsResults = CacheBuilder.newBuilder()
			.maximumSize(200)
			.expireAfterWrite(1, TimeUnit.DAYS)
			.build();
		this.serializer = serializer;
	}
	
	@Override
	public void addCommandStatus(String id, CommandStatus result){
		commandsResults.put(id, serializer.serialize(result));
	}

	@Override
	public CommandStatus getCommandResult(String id) {
		return serializer.parse(commandsResults.getIfPresent(id), CommandStatus.class);
	}
}
