package com.datascience.gal.service;

import java.util.Properties;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;

import org.apache.log4j.Logger;

import com.datascience.gal.dawidSkeneProcessors.DawidSkeneProcessorManager;

/**
 * This class is used to make sure that all that have to be
 * initialized during application deploy is actualy initialized.
 */
public class InitializationSupport implements ServletContextListener {

	public void contextInitialized(ServletContextEvent event) {
		try {
			logger.info("Initialization support started.");
			this.initializeManagerAndCache(event.getServletContext());
		} catch(Exception e) {
			logger.error("In context initialization support : "+e.getMessage());
		}
	}


	public void contextDestroyed(ServletContextEvent event) {
		//We can put some cleanup here if it will be required
	}

	private void initializeManagerAndCache(ServletContext scontext) throws IOException,
		ClassNotFoundException, SQLException {
		Properties props = new Properties();
		props.load(scontext.getResourceAsStream("/WEB-INF/classes/dawidskene.properties"));
		String user = props.getProperty("USER");
		String password = props.getProperty("PASSWORD");
		String db = props.getProperty("DB");
		String url = props.getProperty("URL");
		int threadPollSize = Integer.parseInt(props.getProperty("THREADPOLL_SIZE"));
		int sleepPeriod = Integer.parseInt(props.getProperty("PROCESSOR_MANAGER_SLEEP_PERIOD"));
		DawidSkeneProcessorManager manager;
		if (props.containsKey("cacheSize")) {
			int cachesize = Integer.parseInt(props.getProperty("cacheSize"));
			manager = new DawidSkeneProcessorManager(threadPollSize,sleepPeriod,user,password,db,url,cachesize);
		} else {
			manager = new DawidSkeneProcessorManager(threadPollSize,sleepPeriod,user,password,db,url);
		}
		Service.setManager(manager);
		manager.start();
		logger.info("Manager and cache initialized.");
	}

	private static Logger logger = Logger.getLogger(InitializationSupport.class);
}
