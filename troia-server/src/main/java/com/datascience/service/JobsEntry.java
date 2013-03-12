package com.datascience.service;

import java.util.Collection;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.datascience.core.Job;
import com.datascience.core.stats.Category;
import com.datascience.core.storages.JSONUtils;
import com.sun.jersey.spi.resource.Singleton;

/**
 * @author Konrad Kurdej
 */
@Path("/jobs/")
@Singleton
public class JobsEntry extends AbstractJobsEntry{
	
	@POST
	public Response createJob(@FormParam("id") String jid,
			@FormParam("categories") String sCategories,
			@DefaultValue("batch") @FormParam("type") String type) throws Exception{
		if (empty_jid(jid)){
			jid = jidGenerator.getID();
		}

		Job job_old = jobStorage.get(jid);
		if (job_old != null) {
			throw new IllegalArgumentException("Job with ID " + jid + " already exists");
		}
		
		if (sCategories == null){
			throw new IllegalArgumentException("You should provide categories list");
		}
		Collection<Category> categories = responser.getSerializer().parse(sCategories,
			JSONUtils.categorySetType);
		Job job = jobFactory.createNominalJob(type, jid, categories);

		jobStorage.add(job);
		return responser.makeOKResponse("New job created with ID: " + jid);
	}
}
