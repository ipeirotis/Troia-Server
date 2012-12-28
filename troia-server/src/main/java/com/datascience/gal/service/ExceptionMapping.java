package com.datascience.gal.service;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;

/**
 *
 * @author konrad
 */
@Provider
public class ExceptionMapping implements ExceptionMapper<Exception>{

	private static Logger log = Logger.getLogger(ExceptionMapping.class.getName());
	@Context
	ServletContext context;
	
	@Override
	public Response toResponse(Exception e) {
		log.fatal("Formating exception into response", e);
		return ((ResponseBuilder) context.getAttribute(Constants.RESPONSER)).makeExceptionResponse(e);
	}
	
}
