package com.datascience.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.datascience.core.JobsManager;
import com.datascience.core.storages.*;
import com.datascience.serialization.ISerializer;
import com.datascience.utils.IRandomUniqIDGenerator;
import com.datascience.utils.RandomUniqIDGenerators;
import org.apache.log4j.Logger;

import com.datascience.executor.CommandStatusesContainer;
import com.datascience.executor.SerializedCommandStatusesContainer;
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
		String user = properties.getProperty("USER");
		String password = properties.getProperty("PASSWORD");
		String db = properties.getProperty("DB");
		String url = properties.getProperty("URL");
		int cachesize;
		if (properties.containsKey("cacheSize")) {
			cachesize = Integer.parseInt(properties.getProperty("cacheSize"));
		} else {
			cachesize = 15;
		}
		IJobStorage internalJobStorage = new DBJobStorage(user,
			password, db, url, serializer);
		IJobStorage jobStorage = new JobStorageUsingExecutor(internalJobStorage, executor, jobsManager);
		jobStorage = new CachedWithRegularDumpJobStorage(jobStorage, cachesize, 10, TimeUnit.MINUTES);
		logger.info("Job Storage loaded");
		return jobStorage;
	}
	
	public IRandomUniqIDGenerator loadIdGenerator(){
		return new RandomUniqIDGenerators.PrefixAdderDecorator("RANDOM__", new RandomUniqIDGenerators.NumberAndDate());
	}

	public CommandStatusesContainer loadCommandStatusesContainer(ISerializer serializer){
		return new SerializedCommandStatusesContainer(new RandomUniqIDGenerators.Numbers(), serializer);
	}

	public ProjectCommandExecutor loadProjectCommandExecutor(){
		return new ProjectCommandExecutor();
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
