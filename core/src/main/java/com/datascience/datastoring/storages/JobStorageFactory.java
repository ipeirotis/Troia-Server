package com.datascience.datastoring.storages;

import com.datascience.datastoring.adapters.db.DBFullAdapter;
import com.datascience.datastoring.adapters.memory.MemoryKVFactory;
import com.datascience.datastoring.adapters.mixed.DBKVsFactory;
import com.datascience.datastoring.backends.db.DBBackend;
import com.datascience.datastoring.datamodels.full.DBJobStorage;
import com.datascience.datastoring.datamodels.full.MemoryJobStorage;
import com.datascience.datastoring.datamodels.kv.KVJobStorage;
import com.datascience.datastoring.datamodels.kv.TransformingKVsProvider;
import com.datascience.datastoring.jobs.IJobStorage;
import com.datascience.datastoring.transforms.*;
import com.datascience.serialization.ISerializer;

import java.sql.SQLException;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * User: artur
 * Date: 4/16/13
 */
public class JobStorageFactory {

	public static class TransformationFactory {
		public static ICoreTransformsFactory<String> createStringTransformationFactory(String type, ISerializer serializer){
			if (type.equals("JSON"))
				return new SingletonsCoreTransformsFactory<String>(new SerializerBasedCoreTransformsFactory(serializer));
			if (type.equals("SIMPLE"))
				return new SingletonsCoreTransformsFactory<String>(new SimpleStringCoreTransformsFactory());
			//TODO: avro, thirft
			return null;
		}

		public static ICoreTransformsFactory<Object> createObjectTransformationFactory(){
			return new SingletonsCoreTransformsFactory<Object>(new CastingCoreTransformsFactory());
		}
	}

	public static IJobStorage create(String fullType, Properties connectionProperties, Properties properties, ISerializer serializer) throws SQLException, ClassNotFoundException{
		String[] storageParams = fullType.split("_");
		checkArgument(storageParams.length >= 2, "Unknown storage model: " + fullType);
		String type = (storageParams[0] + "_" + storageParams[1]).toUpperCase();
		if (type.equals("MEMORY_FULL")){
			return new MemoryJobStorage();
		}
		if (type.equals("MEMORY_KV")){
			if (fullType.equals(type))
				return new KVJobStorage(new TransformingKVsProvider<Object>(
						new MemoryKVFactory<Object>(), TransformationFactory.createObjectTransformationFactory()
				));
			else
				return new KVJobStorage(new TransformingKVsProvider<String>(
						new MemoryKVFactory<String>(), TransformationFactory.createStringTransformationFactory(storageParams[2], serializer)
				));
		}
		if (type.equals("DB_FULL")){
			return new DBJobStorage(new DBFullAdapter(new DBBackend(connectionProperties, properties, true)), serializer);
		}
		if (type.equals("DB_KV")){
			checkArgument(storageParams.length >= 3, "Unknown storage model: " + fullType);
			return new KVJobStorage(new TransformingKVsProvider<String>(
					new DBKVsFactory<String>(new DBBackend(connectionProperties, properties, true)), TransformationFactory.createStringTransformationFactory(storageParams[2], serializer)
			));
		}
		throw new IllegalArgumentException("Unknown storage model: " + fullType);
	}
}
