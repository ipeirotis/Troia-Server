package com.datascience.core.storages;

import com.datascience.core.jobs.IJobStorage;
import com.datascience.serialization.ISerializer;
import com.datascience.utils.DBHelper;
import com.datascience.utils.DBKVHelper;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * User: artur
 * Date: 4/16/13
 */
public class JobStorageFactory {

	protected interface JobStorageCreator{
		IJobStorage create(Properties connectionProperties, Properties properties, ISerializer serializer) throws SQLException, ClassNotFoundException;
	}

	final static Map<String, JobStorageCreator> JOB_STORAGE_FACTORY = new HashMap();
	static {
		JOB_STORAGE_FACTORY.put("MEMORY", new JobStorageCreator(){
			@Override
			public IJobStorage create(Properties connectionProperties, Properties properties, ISerializer serializer){
				return new MemoryJobStorage();
			}
		});

		JOB_STORAGE_FACTORY.put("DB", new JobStorageCreator(){
			@Override
			public IJobStorage create(Properties connectionProperties, Properties properties, ISerializer serializer)
					throws SQLException, ClassNotFoundException{
				return new DBJobStorage(new DBHelper(connectionProperties, properties), serializer);
			}
		});

		JOB_STORAGE_FACTORY.put("KV", new JobStorageCreator(){
			@Override
			public IJobStorage create(Properties connectionProperties, Properties properties, ISerializer serializer)
					throws SQLException, ClassNotFoundException{
				return new DBKVJobStorage(new DBKVHelper(connectionProperties, properties), serializer);
			}
		});
	}

	public static IJobStorage create(String type, Properties connectionProperties, Properties properties, ISerializer serializer) throws SQLException, ClassNotFoundException{
		JobStorageCreator jsc = JOB_STORAGE_FACTORY.get(type.toUpperCase());
		checkArgument(jsc != null, "Unknown storage model: " + type);
		return jsc.create(connectionProperties, properties,  serializer);
	}
}
