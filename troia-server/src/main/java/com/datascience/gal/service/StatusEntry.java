package com.datascience.gal.service;

import com.datascience.core.storages.IJobStorage;
import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

public class StatusEntry {

	private IJobStorage jobStorage;
	private ResponseBuilder responser;
	private Date initializationTimestamp;

	public StatusEntry(IJobStorage jobStorage, ResponseBuilder responser, Date initializationTimestamp){
		this.jobStorage = jobStorage;
		this.responser = responser;
		this.initializationTimestamp = initializationTimestamp;
	}


	@GET @Path("/ping")
	public Response ping(){
		return responser.makeOKResponse("System is working fine (initialized at " + initializationTimestamp + ")");
	}

	@GET @Path("/pingDB")
	public Response pingDB() throws Exception{
		jobStorage.test();
		return responser.makeOKResponse("Job storage " +
			jobStorage.getClass().getName() + " works fine");
	}
}
