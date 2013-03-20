package com.datascience.service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.datascience.core.Job;
import com.google.gson.JsonObject;
import com.sun.jersey.spi.resource.Singleton;

/**
 * @author Konrad Kurdej
 */
@Path("/jobs/")
@Singleton
public class JobsEntry extends AbstractJobsEntry{

	@Override
	protected Job createJob(JsonObject jo, String jid) {
		return jobFactory.createNominalJob(jo, jid);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createJob(String json) throws Exception{
		return super.createJob(json);
	}
}
