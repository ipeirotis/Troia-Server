package com.datascience.gal.service;

import com.datascience.core.JobFactory;
import com.datascience.core.storages.CachedJobStorage;
import com.datascience.core.storages.DBJobStorage;
import com.datascience.core.storages.IJobStorage;
import com.datascience.gal.executor.ProjectCommandExecutor;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

/**
 * @author Konrad
 *
 * this initializing commands shouldn't use ServletContext or even be here
 * TODO: ^^^^
 *
 */
public class InitializationSupport implements ServletContextListener {

	private static Logger logger = Logger.getLogger(InitializationSupport.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			logger.info("Initialization support started.");
			ServletContext scontext = event.getServletContext();
			loadResponser(scontext);
			loadJobStorage(scontext);
			loadStatusEntry(scontext);
			loadJobsEntry(scontext);
		} catch (Exception e) {
			logger.error("In context initialization support : " + e.getMessage());
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		ServletContext scontext = event.getServletContext();
		IJobStorage jobStorage = (IJobStorage) scontext.getAttribute(Constants.JOBS_STORAGE);
		try {
			jobStorage.stop();
		} catch (Exception ex) {
			logger.error("Failed stoping job storage", ex);
		}
	}

	public IJobStorage loadJobStorage(ServletContext scontext) throws IOException, ClassNotFoundException, SQLException {
		Properties props = new Properties();
		props.load(scontext.getResourceAsStream("/WEB-INF/classes/dawidskene.properties"));
		String user = props.getProperty("USER");
		String password = props.getProperty("PASSWORD");
		String db = props.getProperty("DB");
		String url = props.getProperty("URL");
		int cachesize;
		if (props.containsKey("cacheSize")) {
			cachesize = Integer.parseInt(props.getProperty("cacheSize"));
		} else {
			cachesize = 15;
		}
		ResponseBuilder responser = (ResponseBuilder) scontext.getAttribute(Constants.RESPONSER);
		IJobStorage internalJobStorage = new DBJobStorage(user, password, db, url, responser.getSerializer());
		IJobStorage jobStorage = new CachedJobStorage(
			internalJobStorage, cachesize);
		scontext.setAttribute(Constants.JOBS_STORAGE, jobStorage);

		logger.info("Job Storage loaded");
		return jobStorage;
	}

	public StatusEntry loadStatusEntry(ServletContext scontext){
		IJobStorage jobStorage = (IJobStorage) scontext.getAttribute(Constants.JOBS_STORAGE);
		ResponseBuilder responser = (ResponseBuilder) scontext.getAttribute(Constants.RESPONSER);
		StatusEntry se = new StatusEntry(jobStorage, responser, DateTime.now());
		scontext.setAttribute(Constants.STATUS_ENTRY, se);
		return se;
	}

	public JobsEntry loadJobsEntry(ServletContext scontext){
		ResponseBuilder responser = (ResponseBuilder) scontext.getAttribute(Constants.RESPONSER);
		IJobStorage jobStorage = (IJobStorage) scontext.getAttribute(Constants.JOBS_STORAGE);
		JobsEntry je = new JobsEntry(responser, new JobFactory(),
			loadProjectCommandExecutor(), jobStorage);
		scontext.setAttribute(Constants.JOBS_ENTRY, je);
		return je;
	}

	private ProjectCommandExecutor loadProjectCommandExecutor(){
		return new ProjectCommandExecutor();
	}

	private ResponseBuilder loadResponser(ServletContext scontext) {
		// TODO - put in properties what serializer we would like to use as default one
		ResponseBuilder responser = new ResponseBuilder(new GSONSerializer());
		scontext.setAttribute(Constants.RESPONSER, responser);
		return responser;
	}
}
