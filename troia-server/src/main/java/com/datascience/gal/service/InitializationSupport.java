package com.datascience.gal.service;

import com.datascience.core.storages.IJobStorage;
import com.datascience.gal.executor.ProjectCommandExecutor;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;

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
			props.load(scontext.getResourceAsStream("/WEB-INF/classes/dawidskene.properties"));

			ServiceComponentsFactory factory = new ServiceComponentsFactory(props);
			
			ISerializer serializer = factory.loadSerializer();
			scontext.setAttribute(Constants.SERIALIZER, serializer);
			
			IJobStorage jobStorage = factory.loadJobStorage(serializer);
			scontext.setAttribute(Constants.JOBS_STORAGE, jobStorage);
			
			ResponseBuilder responser = factory.loadResponser(serializer);
			scontext.setAttribute(Constants.RESPONSER, responser);

			ProjectCommandExecutor executor = factory.loadProjectCommandExecutor();
			scontext.setAttribute(Constants.COMMAND_EXECUTOR, responser);
			
			JobsEntry je = factory.loadJobsEntry(responser, jobStorage, executor);
			scontext.setAttribute(Constants.JOBS_ENTRY, je);

			StatusEntry se = factory.loadStatusEntry(responser, jobStorage);
			scontext.setAttribute(Constants.STATUS_ENTRY, se);

			logger.info("Initialization support ended without complications");
		} catch (Exception e) {
			logger.error("In context initialization support: " + e.getMessage());
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
}
