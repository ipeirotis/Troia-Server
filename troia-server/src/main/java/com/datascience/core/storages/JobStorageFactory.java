package com.datascience.core.storages;

import com.datascience.serialization.ISerializer;
import com.datascience.utils.DBHelper;
import com.datascience.utils.DBKVHelper;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * User: artur
 * Date: 4/16/13
 */
public class JobStorageFactory {

	protected interface JobStorageCreator{
		IJobStorage create(String dbUrl, String dbName, String driverClass,
						   Properties connectionProperties, ISerializer serializer) throws SQLException, ClassNotFoundException;
	}

	final static Map<String, JobStorageCreator> JOB_STORAGE_FACTORY = new HashMap();
	static {
		JOB_STORAGE_FACTORY.put("MEMORY", new JobStorageCreator(){
			@Override
			public IJobStorage create(String dbUrl, String dbName, String driverClass,
									  Properties connectionProperties, ISerializer serializer){
				return new MemoryJobStorage();
			}
		});

		JOB_STORAGE_FACTORY.put("DB", new JobStorageCreator(){
			@Override
			public IJobStorage create(String dbUrl, String dbName, String driverClass,
									  Properties connectionProperties, ISerializer serializer)
					throws SQLException, ClassNotFoundException{
				return new DBJobStorage(new DBHelper(dbUrl, driverClass, connectionProperties, dbName), serializer);
			}
		});

		JOB_STORAGE_FACTORY.put("KV", new JobStorageCreator(){
			@Override
			public IJobStorage create(String dbUrl, String dbName, String driverClass,
									  Properties connectionProperties, ISerializer serializer)
					throws SQLException, ClassNotFoundException{
				return new DBKVJobStorage(new DBKVHelper(dbUrl, driverClass, connectionProperties, dbName), serializer);
			}
		});
	}

	public static IJobStorage create(String type, String dbUrl, String dbName, String driverClass,
									 Properties connectionProperties, ISerializer serializer) throws SQLException, ClassNotFoundException{
		return JOB_STORAGE_FACTORY.get(type.toUpperCase()).create(dbUrl, dbName, driverClass, connectionProperties, serializer);
	}
}
