package com.datascience.datastoring.storages;

import com.datascience.datastoring.jobs.IJobStorage;
import com.datascience.serialization.ISerializer;
import com.datascience.utils.DBHelper;
import com.datascience.utils.DBKVHelper;

import java.sql.SQLException;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * User: artur
 * Date: 4/16/13
 */
public class JobStorageFactory {

	public static IJobStorage create(String fullType, Properties connectionProperties, Properties properties, ISerializer serializer) throws SQLException, ClassNotFoundException{
		String[] storageParams = fullType.split("_");
		checkArgument(storageParams.length >= 2, "Unknown storage model: " + fullType);
		String type = (storageParams[0] + "_" + storageParams[1]).toUpperCase();
		if (type.equals("MEMORY_FULL")){
			return new MemoryJobStorage();
		}
		if (type.equals("MEMORY_KV")){
			return new MemoryKVJobStorage(serializer);
		}
		if (type.equals("DB_FULL")){
			return new DBJobStorage(new DBHelper(connectionProperties, properties), serializer);
		}
		if (type.equals("DB_KV")){
			checkArgument(storageParams.length >= 3, "Unknown storage model: " + fullType);
			return new DBKVJobStorage(
				new DBKVHelper(connectionProperties, properties, storageParams.length == 4 && storageParams[2].toUpperCase().equals("MEMCACHE")),
				serializer,
				storageParams[storageParams.length-1]);
		}
		throw new IllegalArgumentException("Unknown storage model: " + fullType);
	}
}
