package com.datascience.service;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.datascience.core.jobs.Job;
import com.datascience.core.jobs.JobFactory;
import com.datascience.core.jobs.IJobStorage;
import com.datascience.galc.ContinuousProject;
import com.datascience.utils.IRandomUniqIDGenerator;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.spi.resource.Singleton;

import static com.datascience.serialization.json.JSONUtils.tKeys;
import static com.google.common.base.Preconditions.checkArgument;

@Path("/jobs/")
@Singleton
public class JobsEntry {
	
	@Context
	ServletContext context;
	@Context
	Request request;
	@Context
	UriInfo uriInfo;
	
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
		ResponseBuilder responser = getResponseBuilder();
		IJobStorage storage = getJobStorage();
		if (responser != null && storage != null){
			return new JobFactory(responser.getSerializer(), storage);
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

		JsonObject jo = json.isEmpty() ? new JsonObject() : tKeys(new JsonParser().parse(json).getAsJsonObject());

		String jid = jo.has("id") ? jo.get("id").getAsString() : getJidGenerator().getID();
		checkArgument(getJobStorage().get(jid) == null, "Job with ID " + jid + " already exists");
		checkArgument(jo.has("algorithm"), "You should provide alogorithm type (BDS/IDS/BMV/IMV/GALC)");

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
