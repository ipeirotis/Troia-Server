package com.datascience.gal.service;

import com.datascience.core.JobFactory;
import com.datascience.core.storages.CachedJobStorage;
import com.datascience.core.storages.CachedWithRegularDumpJobStorage;
import com.datascience.core.storages.DBJobStorage;
import com.datascience.core.storages.IJobStorage;
import com.datascience.core.storages.JobStorageUsingExecutor;
import com.datascience.gal.executor.ProjectCommandExecutor;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

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
		IJobStorage jobStorage =
			new CachedJobStorage(internalJobStorage, cachesize);
//			new CachedWithRegularDumpJobStorage(internalJobStorage, cachesize, 10, TimeUnit.MINUTES);
		jobStorage = new JobStorageUsingExecutor(jobStorage, executor);
		logger.info("Job Storage loaded");
		return jobStorage;
	}

	public StatusEntry loadStatusEntry(ResponseBuilder responser, IJobStorage jobStorage){
		return new StatusEntry(jobStorage, responser, DateTime.now());
	}

	public JobsEntry loadJobsEntry(ResponseBuilder responser, IJobStorage jobStorage,
			ProjectCommandExecutor executor){
		return new JobsEntry(responser, new JobFactory(), executor, jobStorage);
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
