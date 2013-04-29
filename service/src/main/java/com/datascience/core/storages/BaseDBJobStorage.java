package com.datascience.core.storages;

import com.datascience.core.jobs.IJobStorage;
import com.datascience.core.jobs.JobFactory;
import com.datascience.serialization.ISerializer;
import com.datascience.utils.storage.DBStorage;

import java.sql.SQLException;

/**
 * @Author: artur
 */
public abstract class BaseDBJobStorage<T extends DBStorage> implements IJobStorage {

	protected T helper;
	protected ISerializer serializer;
	protected JobFactory jobFactory;

	public BaseDBJobStorage(T helper, ISerializer serializer) throws SQLException {
		this.helper = helper;
		this.helper.connectDB();
		this.serializer = serializer;
		jobFactory = new JobFactory(serializer, this);
	}

	public T getHelper(){
		return helper;
	}

	@Override
	public void clearAndInitialize() throws SQLException{
		helper.execute();
	}
}
