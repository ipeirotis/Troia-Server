package com.datascience.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.datascience.core.jobs.JobsManager;
import com.datascience.core.storages.*;
import com.datascience.executor.CachedCommandStatusesContainer;
import com.datascience.executor.SerializedCachedCommandStatusesContainer;
import com.datascience.serialization.ISerializer;
import com.datascience.serialization.json.GSONSerializer;
import com.datascience.utils.DBKVHelper;
import com.datascience.utils.IRandomUniqIDGenerator;
import com.datascience.utils.RandomUniqIDGenerators;
import org.apache.log4j.Logger;

import com.datascience.executor.ProjectCommandExecutor;

/**
 *
 * @author konrad
 */
public class ServiceComponentsFactory {
	
	private static Logger logger = Logger.getLogger(ServiceComponentsFactory.class);
	
	protected Properties properties;
	
	public ServiceComponentsFactory(Properties properties){
		this.properties = properties;
	}
	
	public IJobStorage loadJobStorage(ISerializer serializer, ProjectCommandExecutor executor, JobsManager jobsManager)
			throws IOException, ClassNotFoundException, SQLException {
		String user = properties.getProperty(Constants.DB_USER);
		String password = properties.getProperty(Constants.DB_PASSWORD);
		String db = properties.getProperty(Constants.DB_NAME);
		String url = properties.getProperty(Constants.DB_URL);
		String driverClass = properties.getProperty(Constants.DB_DRIVER_CLASS);
		Properties connectionProperties = new Properties();
		connectionProperties.put("user", user);
		connectionProperties.put("password", password);
		int cacheSize = Integer.parseInt(properties.getProperty(Constants.CACHE_SIZE));
		int cacheDumpTime = Integer.parseInt(properties.getProperty(Constants.CACHE_DUMP_TIME));

//		IJobStorage internalJobStorage = new DBJobStorage(user, password, db, url, serializer);
		DBKVHelper helper = new DBKVHelper(url, driverClass, connectionProperties, db);
		IJobStorage internalJobStorage = new DBKVJobStorage(helper, serializer);
		IJobStorage jobStorage = new JobStorageUsingExecutor(internalJobStorage, executor, jobsManager);
		jobStorage = new CachedWithRegularDumpJobStorage(jobStorage, cacheSize, cacheDumpTime, TimeUnit.SECONDS);
		logger.info("Job Storage loaded");
		return jobStorage;
	}
	
	public IRandomUniqIDGenerator loadIdGenerator(){
		return new RandomUniqIDGenerators.PrefixAdderDecorator("RANDOM__", new RandomUniqIDGenerators.NumberAndDate());
	}

	public CachedCommandStatusesContainer loadCommandStatusesContainer(ISerializer serializer){
		return new SerializedCachedCommandStatusesContainer(new RandomUniqIDGenerators.Numbers(), serializer,
				Integer.valueOf(properties.getProperty(Constants.RESPONSES_CACHE_SIZE)),
				Integer.valueOf(properties.getProperty(Constants.RESPONSES_DUMP_TIME)));
	}

	public ProjectCommandExecutor loadProjectCommandExecutor(){
		return new ProjectCommandExecutor(Integer.valueOf(properties.getProperty(Constants.EXECUTOR_THREADS_NUM)));
	}

	public ISerializer loadSerializer() {
		// TODO - put in properties what serializer we would like to use as default one
		return new GSONSerializer();
	}
	
	public ResponseBuilder loadResponser(ISerializer serializer) {
		return new ResponseBuilder(serializer);
	}
	
	public JobsManager loadJobsManager() {
		return new JobsManager();
	}
}
