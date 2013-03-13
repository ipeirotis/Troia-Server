package com.datascience.service;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.datascience.core.Job;
import com.datascience.core.JobFactory;
import com.datascience.core.storages.IJobStorage;
import com.datascience.utils.IRandomUniqIDGenerator;
import com.sun.jersey.spi.resource.Singleton;

@Singleton
public abstract class AbstractJobsEntry {
	
	
	@Context ServletContext context;
	
	protected IJobStorage jobStorage;
	protected IRandomUniqIDGenerator jidGenerator;
	protected JobFactory jobFactory;
	protected ResponseBuilder responser;
	
	@PostConstruct
	public void postConstruct(){
		jidGenerator = (IRandomUniqIDGenerator) context.getAttribute(Constants.ID_GENERATOR);
		jobStorage = (IJobStorage) context.getAttribute(Constants.JOBS_STORAGE);
		responser = (ResponseBuilder) context.getAttribute(Constants.RESPONSER);
		jobFactory = new JobFactory();
	}
	
	protected boolean empty_jid(String jid){
		return jid == null || "".equals(jid);
	}
	
	@DELETE
	public Response deleteJob(@FormParam("id") String jid) throws Exception{
		if (empty_jid(jid)) {
			throw new IllegalArgumentException("No job ID given");
		}
		Job job = jobStorage.get(jid);
		if (job == null) {
			throw new IllegalArgumentException("Job with ID " + jid + " does not exist");
		}
		jobStorage.remove(job);
		return responser.makeOKResponse("Removed job with ID: " + jid);
	}
}
