package com.datascience.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.datascience.core.Job;
import com.google.gson.JsonObject;
import com.sun.jersey.spi.resource.Singleton;

/**
 * @author artur
 */
@Path("/cjobs/")
@Singleton
public class ContinuousJobsEntry extends AbstractJobsEntry {

	@Override
	protected Job createJob(JsonObject jo, String jid) {
		return jobFactory.createContinuousJob(jo, jid);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createJob(String json) throws Exception{
		return super.createJob(json);
	}
}
