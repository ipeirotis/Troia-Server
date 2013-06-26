package com.datascience.executor;

import com.datascience.datastoring.adapters.kv.ISafeKVStorage;
import com.datascience.serialization.ISerializer;
import com.datascience.utils.IRandomUniqIDGenerator;

/**
 * User: artur
 * Date: 6/26/13
 */
public class KeyValueCommandStatusesContainer extends CommandStatusesContainerBase {

	private ISafeKVStorage<String> storage;
	private ISerializer serializer;

	public KeyValueCommandStatusesContainer (IRandomUniqIDGenerator idGenerator, ISafeKVStorage<String> storage, ISerializer serializer){
		super(idGenerator);
		this.storage = storage;
		this.serializer = serializer;
	}

	@Override
	public void addCommandStatus(String id, CommandStatus result) {
		storage.put(id, serializer.serialize(result));
	}

	@Override
	public CommandStatus getCommandResult(String id) {
		return serializer.parse(storage.get(id), CommandStatus.class);
	}
}
