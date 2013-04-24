package com.datascience.service;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.datascience.core.jobs.Job;
import com.datascience.core.jobs.JobFactory;
import com.datascience.core.storages.IJobStorage;
import com.datascience.galc.ContinuousProject;
import com.datascience.utils.IRandomUniqIDGenerator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.spi.resource.Singleton;

@Path("/jobs/")
@Singleton
public class AbstractJobsEntry {
	
	@Context
	ServletContext context;
	@Context
	Request request;
	@Context
	UriInfo uriInfo;
	
	protected JobFactory jobFactory;

	private IJobStorage getJobStorage(){
		return (IJobStorage) context.getAttribute(Constants.JOBS_STORAGE);
	}

	private IRandomUniqIDGenerator getJidGenerator(){
		return (IRandomUniqIDGenerator) context.getAttribute(Constants.ID_GENERATOR);
	}

	private ResponseBuilder getResponseBuilder(){
		return (ResponseBuilder) context.getAttribute(Constants.RESPONSER);
	}

	protected JobFactory getJobFactory(){
		if (jobFactory != null)
			return jobFactory;
		ResponseBuilder responser = getResponseBuilder();
		IJobStorage storage = getJobStorage();
		if (responser != null && storage != null){
			jobFactory = new JobFactory(responser.getSerializer(), storage);
			return jobFactory;
		}
		return null;
	}

	protected boolean empty_jid(String jid){
		return jid == null || "".equals(jid);
	}

	@DELETE
	public Response deleteJob(@FormParam("id") String jid) throws Exception{
		if (!InitializationSupport.checkIsInitialized(context))
			return InitializationSupport.makeNotInitializedResponse(context);
		if (empty_jid(jid)) {
			throw new IllegalArgumentException("No job ID given");
		}
		Job job = getJobStorage().get(jid);
		if (job == null) {
			throw new IllegalArgumentException("Job with ID " + jid + " does not exist");
		}
		getJobStorage().remove(job);
		return getResponseBuilder().makeOKResponse("Removed job with ID: " + jid);
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createJob(String json) throws Exception{
		if (!InitializationSupport.checkIsInitialized(context))
			return InitializationSupport.makeNotInitializedResponse(context);

		JsonObject jo = json.isEmpty() ? new JsonObject() : new JsonParser().parse(json).getAsJsonObject();

		String jid = jo.has("id") ? jo.get("id").getAsString() : getJidGenerator().getID();
		Job job_old = getJobStorage().get(jid);
		if (job_old != null) {
			throw new IllegalArgumentException("Job with ID " + jid + " already exists");
		}
		if (!jo.has("algorithm")){
			throw new IllegalArgumentException("You should provide alogorithm type (BDS/IDS/BMV/IMV/GALC)");
		}

		getJobStorage().add(getJobFactory().create(jo.get("algorithm").getAsString(), jo, jid));
		return getResponseBuilder().makeOKResponse("New job created with ID: " + jid);
	}

	@Path("{id}")
	public JobEntryBase getJobEntry(@PathParam("id") String id) throws Exception{
		Job job = getJobStorage().get(id);
		if (job == null) {
			throw new IllegalArgumentException("Job with ID " + id + " does not exist");
		}
		if (job.getProject() instanceof ContinuousProject)
			return new ContinuousJobEntry(context, request, uriInfo, id);
		else
			return new NominalJobEntry(context, request, uriInfo, id);
	}
}
