package com.datascience.datastoring.storages;

import com.datascience.datastoring.jobs.IJobStorage;
import com.datascience.datastoring.jobs.JobFactory;
import com.datascience.serialization.ISerializer;
import com.datascience.utils.storage.DBStorage;
import org.apache.log4j.Logger;

import java.sql.SQLException;

/**
 * @Author: artur
 */
public abstract class BaseDBJobStorage<T extends DBStorage> implements IJobStorage {

	private static Logger logger = Logger.getLogger(BaseDBJobStorage.class);
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

	@Override
	public void stop() throws Exception {
		helper.stop();
	}

	public void close() throws SQLException {
		logger.info("closing db connections");
		helper.close();
	}
}