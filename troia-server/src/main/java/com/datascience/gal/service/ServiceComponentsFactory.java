package com.datascience.gal.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.datascience.core.storages.CachedWithRegularDumpJobStorage;
import com.datascience.core.storages.DBJobStorage;
import com.datascience.core.storages.IJobStorage;
import com.datascience.core.storages.JobStorageUsingExecutor;
import com.datascience.gal.commands.CommandStatusesContainer;
import com.datascience.gal.commands.SerializedCommandStatusesContainer;
import com.datascience.gal.executor.ProjectCommandExecutor;

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
	
	public IJobStorage loadJobStorage(ISerializer serializer, ProjectCommandExecutor executor)
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
		IJobStorage jobStorage = new JobStorageUsingExecutor(internalJobStorage, executor);
		jobStorage = new CachedWithRegularDumpJobStorage(jobStorage, cachesize, 10, TimeUnit.MINUTES);
		logger.info("Job Storage loaded");
		return jobStorage;
	}

	public CommandStatusesContainer loadCommandStatusesContainer(){
		return new SerializedCommandStatusesContainer(new RandomUniqIDGenerators.Numbers(), new GSONSerializer());
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
}
