package com.datascience.service;

import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.core.Response;

import com.datascience.core.jobs.JobsManager;
import com.datascience.executor.ICommandStatusesContainer;
import com.datascience.serialization.ISerializer;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.datascience.core.jobs.IJobStorage;
import com.datascience.executor.ProjectCommandExecutor;

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
			logger.info("Initialization support started");

			ServletContext scontext = event.getServletContext();
			Properties props = new Properties();
			props.load(scontext.getResourceAsStream("/WEB-INF/classes/troia.properties"));
			scontext.setAttribute(Constants.PROPERTIES, props);

			scontext.setAttribute(Constants.IS_INITIALIZED, false);
			scontext.setAttribute(Constants.IS_FREEZED, false);

			ServiceComponentsFactory factory = new ServiceComponentsFactory(props);

			scontext.setAttribute(Constants.DEPLOY_TIME, DateTime.now());

			ISerializer serializer = factory.loadSerializer();
			scontext.setAttribute(Constants.SERIALIZER, serializer);

			ResponseBuilder responser = factory.loadResponser(serializer);
			scontext.setAttribute(Constants.RESPONSER, responser);

			initializeContext(scontext);
			logger.info("Initialization support ended without complications");
		}
		catch (Exception e) {
			logger.fatal("In context initialization support", e);
		}
	}

	public static void initializeContext(ServletContext scontext) throws SQLException, IOException, ClassNotFoundException {
		Properties props = (Properties) scontext.getAttribute(Constants.PROPERTIES);
		ServiceComponentsFactory factory = new ServiceComponentsFactory(props);

		ProjectCommandExecutor executor = factory.loadProjectCommandExecutor();
		scontext.setAttribute(Constants.COMMAND_EXECUTOR, executor);

		JobsManager jobsManager = factory.loadJobsManager();
		scontext.setAttribute(Constants.JOBS_MANAGER, jobsManager);

		ISerializer serializer = (ISerializer) scontext.getAttribute(Constants.SERIALIZER);

		IJobStorage jobStorage = factory.loadJobStorage(props.getProperty(Constants.JOBS_STORAGE), serializer, executor, jobsManager);
		scontext.setAttribute(Constants.JOBS_STORAGE, jobStorage);

		ICommandStatusesContainer statusesContainer = factory.loadCommandStatusesContainer(serializer);
		scontext.setAttribute(Constants.COMMAND_STATUSES_CONTAINER, statusesContainer);

		scontext.setAttribute(Constants.ID_GENERATOR, factory.loadIdGenerator());
		scontext.setAttribute(Constants.IS_INITIALIZED, true);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		destroyContext(event.getServletContext());
		deregisterDrivers();
	}

	public static void destroyContext(ServletContext scontext){
		logger.info("STARTED Cleaning service");
		IJobStorage jobStorage =
				(IJobStorage) scontext.getAttribute(Constants.JOBS_STORAGE);
		ProjectCommandExecutor executor =
				(ProjectCommandExecutor) scontext.getAttribute(Constants.COMMAND_EXECUTOR);
		try {
			if (jobStorage != null)
				jobStorage.stop();
		} catch (Exception ex) {
			logger.error("FAILED Cleaning service - jobStorage", ex);
		}
		// executor might be already closed - if jobStorage was using it
		try {
			if (executor != null)
				executor.stop();
		} catch (Exception ex) {
			logger.error("FAILED Cleaning service - executor", ex);
		}
		logger.info("DONE Cleaning service");
	}
		
	/**
	 * This manually deregisters JDBC driver, which prevents
	 * Tomcat 7 from complaining about memory leaks wrto this class
	 * @see: http://stackoverflow.com/a/5315467/1585082
	 */
	public void deregisterDrivers() {
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
				logger.info(String.format("deregistering jdbc driver: %s", driver));
			} catch (SQLException e) {
				logger.fatal(String.format("Error deregistering driver %s", driver), e);
			}
		}
	}

	public static boolean checkIsInitialized(ServletContext context){
		return (Boolean) context.getAttribute(Constants.IS_INITIALIZED);
	}

	public static Response makeNotInitializedResponse(ServletContext context){
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("deploy_time", context.getAttribute(Constants.DEPLOY_TIME).toString());
		return ((ResponseBuilder) context.getAttribute(Constants.RESPONSER)).makeNotInitializedResponse(content);
	}
}
