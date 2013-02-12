package com.datascience.service;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.datascience.core.Job;
import com.sun.jersey.spi.resource.Singleton;

/**
 * @author artur
 */
@Path("/cjobs/")
@Singleton
public class ContinuousJobsEntry extends AbstractJobsEntry {
	
	@POST
	public Response createJob(@FormParam("id") String jid) throws Exception{
		if (empty_jid(jid)){
			jid = jidGenerator.getID();
		}

		Job job_old = jobStorage.get(jid);
		if (job_old != null) {
			throw new IllegalArgumentException("Job with ID " + jid + " already exists");
		}

		jobStorage.add(jobFactory.createContinuousJob(jid));
		return responser.makeOKResponse("New job created with ID: " + jid);
	}
}
