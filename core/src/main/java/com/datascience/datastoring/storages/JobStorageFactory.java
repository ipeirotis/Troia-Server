package com.datascience.datastoring.storages;

import com.datascience.datastoring.adapters.memory.MemoryKVFactory;
import com.datascience.datastoring.datamodels.full.DBJobStorage;
import com.datascience.datastoring.datamodels.full.MemoryJobStorage;
import com.datascience.datastoring.datamodels.kv.KVJobStorage;
import com.datascience.datastoring.datamodels.kv.TransformingKVsProvider;
import com.datascience.datastoring.jobs.IJobStorage;
import com.datascience.datastoring.transforms.CastingCoreTransformsFactory;
import com.datascience.datastoring.transforms.SingletonsCoreTransformsFactory;
import com.datascience.serialization.ISerializer;
import com.datascience.datastoring.backends.db.DBHelper;

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
			return new KVJobStorage(new TransformingKVsProvider<Object>(
					new MemoryKVFactory(), new SingletonsCoreTransformsFactory<Object>(new CastingCoreTransformsFactory())
			));
			// TODO FIXME XXX separate factory for KVs that gives IKVsProvider
		}
//		if (type.equals("DB_FULL")){
//			return new DBJobStorage(new DBHelper(connectionProperties, properties), serializer);
//		}
		if (type.equals("DB_KV")){
			checkArgument(storageParams.length >= 3, "Unknown storage model: " + fullType);
			return new KVJobStorage(null);
//			return new DBKVsFactory(
//				new DBKVHelper(connectionProperties, properties, storageParams.length == 4 && storageParams[2].toUpperCase().equals("MEMCACHE")),
//				serializer,
//				storageParams[storageParams.length-1]);
		}
		throw new IllegalArgumentException("Unknown storage model: " + fullType);
	}
}
