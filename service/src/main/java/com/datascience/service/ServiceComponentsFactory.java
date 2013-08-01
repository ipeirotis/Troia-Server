package com.datascience.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import com.datascience.datastoring.adapters.kv.DefaultSafeKVStorage;
import com.datascience.datastoring.adapters.mixed.DBKVsFactory;
import com.datascience.datastoring.backends.db.DBBackend;
import com.datascience.datastoring.jobs.IJobStorage;
import com.datascience.datastoring.jobs.JobFactory;
import com.datascience.datastoring.jobs.JobsManager;
import com.datascience.datastoring.jobs.JobsLocksManager;
import com.datascience.datastoring.storages.*;
import com.datascience.executor.*;
import com.datascience.serialization.ISerializer;
import com.datascience.serialization.json.GSONSerializer;
import com.datascience.utils.IRandomUniqIDGenerator;
import com.datascience.utils.RandomUniqIDGenerators;
import org.apache.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

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
	
	public IJobStorage loadJobStorage(String type, ISerializer serializer, ProjectCommandExecutor executor, JobsLocksManager jobsLocksManager)
			throws IOException, ClassNotFoundException, SQLException {
		logger.info("Loading " + type + " job storage");
		int cacheSize = Integer.parseInt(properties.getProperty(Constants.CACHE_SIZE));
		int cacheDumpTime = Integer.parseInt(properties.getProperty(Constants.CACHE_DUMP_TIME));

		IJobStorage jobStorage = JobStorageFactory.create(type, getConnectionProperties(), properties, serializer);
//		IJobStorage jobStorage = new JobStorageUsingExecutor(internalJobStorage, executor, jobsLocksManager);
//		jobStorage = new CachedWithRegularDumpJobStorage(jobStorage, cacheSize, cacheDumpTime, TimeUnit.SECONDS);
		logger.info("Job Storage loaded: " + jobStorage.toString());
		return jobStorage;
	}

	public DataSource getConnectionPool() {
		PoolProperties poolProperties = new PoolProperties();
		poolProperties.setUrl(properties.getProperty(Constants.DB_URL));
	}

	public JobsManager loadJobManager(IJobStorage jobStorage, ISerializer serializer) {
		JobFactory jobFactory = new JobFactory(serializer, jobStorage);
		return new JobsManager(jobStorage, jobFactory);
	}
	
	public IRandomUniqIDGenerator loadIdGenerator(){
		return new RandomUniqIDGenerators.PrefixAdderDecorator("RANDOM__", new RandomUniqIDGenerators.NumberAndDate());
	}

	public ICommandStatusesContainer loadCommandStatusesContainer(String jobStorageType, ISerializer serializer) throws SQLException, ClassNotFoundException {
		if (jobStorageType.toUpperCase().startsWith("DB")){
			DBKVsFactory<String> kvFactory = new DBKVsFactory<String>(DBBackend.createInstance(getConnectionProperties(), properties, true));
			return new KeyValueCommandStatusesContainer(
					new RandomUniqIDGenerators.NumberAndDate(),
					new DefaultSafeKVStorage(kvFactory.getKV("Statuses"), "StatusesStorage"),
					serializer);
		}
		else {
			return new SerializedCachedCommandStatusesContainer(new RandomUniqIDGenerators.Numbers(), serializer,
					Integer.valueOf(properties.getProperty(Constants.RESPONSES_CACHE_SIZE)),
					Integer.valueOf(properties.getProperty(Constants.RESPONSES_DUMP_TIME)));
		}
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
	
	public JobsLocksManager loadJobsManager() {
		return new JobsLocksManager();
	}

	private Properties getConnectionProperties(){
		Properties connectionProperties = new Properties();
		connectionProperties.put("user", properties.getProperty(Constants.DB_USER));
		connectionProperties.put("password", properties.getProperty(Constants.DB_PASSWORD));
		return connectionProperties;
	}
}
