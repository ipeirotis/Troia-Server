package com.datascience.gal.service;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author konrad
 */
@Provider
public class ExceptionMapping implements ExceptionMapper<Exception>{

	@Context
	ServletContext context;
	
	@Override
	public Response toResponse(Exception e) {
		return ((ResponseBuilder) context.getAttribute(Constants.RESPONSER)).makeExceptionResponse(e);
	}
	
}
