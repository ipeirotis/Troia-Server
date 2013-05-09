package com.datascience.service;

import java.util.NoSuchElementException;
import javax.servlet.ServletContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;

/**
 * We assume that jersey errors are less important mostly user errors
 * @author konrad
 */
@Provider
public class ExceptionMapping implements ExceptionMapper<Exception>{

	private static Logger log = Logger.getLogger(ExceptionMapping.class);
	@Context
	ServletContext context;
	
	protected ResponseBuilder getResponser() {
		return (ResponseBuilder) context.getAttribute(Constants.RESPONSER);
	}
	
	@Override
	public Response toResponse(Exception e) {
		if (e instanceof WebApplicationException) {
			log.debug("Formating exception", e);
			return serviceTechnicalError((WebApplicationException) e);
		}
		ResponseBuilder responser = getResponser();
		Integer status_code = null;
		if (e instanceof IllegalArgumentException) {
			status_code = Response.Status.BAD_REQUEST.getStatusCode();
		} else if (e instanceof NoSuchElementException || e instanceof NoSuchElementException) {
			status_code = Response.Status.NOT_FOUND.getStatusCode();
		}
		if (status_code != null) {
			log.debug("Formating exception", e);
			return responser.makeErrorResponse(status_code, e.getMessage());
		}
		log.fatal("Formating exception into response", e);
		return responser.makeExceptionResponse(e);
	}
	
	public Response serviceTechnicalError(WebApplicationException e){
		Response tmp = e.getResponse();
		String message = e.getLocalizedMessage();
		if (e instanceof com.sun.jersey.api.NotFoundException) {
			com.sun.jersey.api.NotFoundException exp =
				(com.sun.jersey.api.NotFoundException) e;
			message = "Unknown adress: " + exp.getNotFoundUri();
		}
		return getResponser().makeErrorResponse(tmp.getStatus(), message);
	}
	
}
