/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.datascience.gal.service;

import javax.ws.rs.WebApplicationException;

/**
 *
 * @author konrad
 */
public class ServiceException extends WebApplicationException{

	public ServiceException(ResponseBuilder responser, int status, String message){
		super(responser.makeErrorResponse(status, message));
	}
	
	public static ServiceException notFoundException(ResponseBuilder responser){
		return new ServiceException(responser, 404, "Not found");
	}
	
	public static ServiceException wrongArgumentException(ResponseBuilder responser, String message){
		return new ServiceException(responser, 400, message);
	}
}
