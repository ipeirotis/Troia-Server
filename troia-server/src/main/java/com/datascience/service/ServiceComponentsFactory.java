package com.datascience.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.datascience.core.JobsManager;
import com.datascience.core.storages.*;
import com.datascience.executor.CachedCommandStatusesContainer;
import com.datascience.executor.SerializedCachedCommandStatusesContainer;
import com.datascience.serialization.ISerializer;
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
		String user = properties.getProperty("DB_USER");
		String password = properties.getProperty("DB_PASSWORD");
		String db = properties.getProperty("DB_NAME");
		String url = properties.getProperty("DB_URL");
		int cacheSize = Integer.parseInt(properties.getProperty("CACHE_SIZE"));
		int cacheDumpTime = Integer.parseInt(properties.getProperty("CACHE_DUMP_TIME"));

		IJobStorage internalJobStorage = new DBJobStorage(user, password, db, url, serializer);
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
				Integer.valueOf(properties.getProperty("RESPONSES_CACHE_SIZE")),
				Integer.valueOf(properties.getProperty("RESPONSES_DUMP_TIME")));
	}

	public ProjectCommandExecutor loadProjectCommandExecutor(){
		return new ProjectCommandExecutor(Integer.valueOf(properties.getProperty("EXECUTOR_THREADS_NUM")));
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
