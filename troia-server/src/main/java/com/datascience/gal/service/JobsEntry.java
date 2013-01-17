package com.datascience.gal.service;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.datascience.core.Job;
import com.datascience.core.JobFactory;
import com.datascience.core.storages.IJobStorage;
import com.datascience.core.storages.JSONUtils;
import com.datascience.gal.Category;
import com.datascience.gal.executor.ProjectCommandExecutor;
import java.util.Collection;
import javax.ws.rs.POST;

/**
 * @author Konrad Kurdej
 */
public class JobsEntry {
	
	private static final String RANDOM_PREFIX = "RANDOM__";
	
	IJobStorage jobStorage;
	private IRandomUniqIDGenerator jidGenerator;
	private JobFactory jobFactory;
	private ResponseBuilder responser;
	private ProjectCommandExecutor executor;
	
	public JobsEntry(){
		jidGenerator = new RandomUniqIDGenerators.PrefixAdderDecorator(RANDOM_PREFIX,
			new RandomUniqIDGenerators.NumberAndDate());
	}
	
	public JobsEntry(ResponseBuilder responser, JobFactory jobFactory,
			ProjectCommandExecutor executor, IJobStorage jobStorage){
		this();
		this.jobStorage = jobStorage;
		this.responser = responser;
		this.jobFactory = jobFactory;
		this.executor = executor;
	}
	
	protected JobEntry jobEntryFactory(Job job){
		return new JobEntry(job, executor, responser);
	}
	
	@Path("/{id}/")
	public JobEntry getJob(@PathParam("id") String jid) throws Exception{
		Job job = jobStorage.get(jid);
		if (job == null) {
			throw new IllegalArgumentException("Job with ID " + jid + " does not exist");
		}
		return jobEntryFactory(job);
	}
	
	private boolean empty_jid(String jid){
		return jid == null || "".equals(jid);
	}
	
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

		Collection<Category> categories = responser.getSerializer().parse(sCategories,
			JSONUtils.categorySetType);
		Job job = jobFactory.createJob(type, jid, categories);

		jobStorage.add(job);
		return responser.makeOKResponse("New job created with ID: " + jid);
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
