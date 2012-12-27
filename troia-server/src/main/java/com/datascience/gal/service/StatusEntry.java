package com.datascience.gal.service;

import com.datascience.core.storages.IJobStorage;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public class StatusEntry {
	
	private IJobStorage jobStorage;
	private ResponseBuilder responser;
	
	public StatusEntry(IJobStorage jobStorage, ResponseBuilder responser){
		this.jobStorage = jobStorage;
		this.responser = responser;
	}


	@GET @Path("/ping")
	public Response ping(){
		return responser.makeOKResponse("System is working fine");
	}
	
	@GET @Path("/pingDB")
	public Response pingDB(){
		try {
			jobStorage.test();
			return responser.makeOKResponse("Job storage " +
				jobStorage.getClass().getName() + " works fine");
		} catch (Exception ex) {
			return responser.makeExceptionResponse(ex);
		}
	}
}
