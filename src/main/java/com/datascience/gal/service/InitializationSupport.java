package com.datascience.gal.service;

import java.io.IOException;
import javax.servlet.ServletContext;
import org.apache.log4j.Logger;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;

/**
 * This class is used to make sure that all that have to be
 * initialized during application deploy is actualy initialized.
 */
public class InitializationSupport implements ServletContextListener {

	public void contextInitialized(ServletContextEvent event) {
		try {
			logger.info("Initialization support");
			Service.setup(event.getServletContext());
		} catch(Exception e) {
			logger.error("In context initialization support : "+e.getMessage());
		}
	}


	public void contextDestroyed(ServletContextEvent event) {
		//We can put some cleanup here if it will be required
	}

	private static Logger logger = Logger.getLogger(InitializationSupport.class);
}
