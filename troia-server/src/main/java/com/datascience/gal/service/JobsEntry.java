package com.datascience.gal.service;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.datascience.core.Job;
import com.datascience.core.JobFactory;
import com.datascience.core.storages.IJobStorage;
import com.datascience.core.storages.JSONUtils;
import com.datascience.gal.Category;
import com.datascience.gal.commands.CommandStatusesContainer;
import com.datascience.gal.commands.JobStorageCommands;
import com.datascience.gal.executor.ProjectCommandExecutor;
import com.sun.jersey.spi.resource.Singleton;

/**
 * @author Konrad Kurdej
 */
@Path("/jobs/")
@Singleton
public class JobsEntry {
	
	private static final String RANDOM_PREFIX = "RANDOM__";
	
	@Context ServletContext context;
	@Context UriInfo uriInfo;
	
	IJobStorage jobStorage;
	private IRandomUniqIDGenerator jidGenerator;
	private JobFactory jobFactory;
	private ResponseBuilder responser;
	private ProjectCommandExecutor executor;
	private CommandStatusesContainer statusesContainer;
	
	@PostConstruct
	public void postConstruct(){
		jidGenerator = new RandomUniqIDGenerators.PrefixAdderDecorator(RANDOM_PREFIX,
				new RandomUniqIDGenerators.NumberAndDate());
		jobStorage = (IJobStorage) context.getAttribute(Constants.JOBS_STORAGE);
		responser = (ResponseBuilder) context.getAttribute(Constants.RESPONSER);
		jobFactory = new JobFactory();
		executor = (ProjectCommandExecutor) context.getAttribute(Constants.COMMAND_EXECUTOR);
		statusesContainer = (CommandStatusesContainer) context.getAttribute(Constants.COMMAND_STATUSES_CONTAINER);
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

		JobStorageCommands.Adder jsc = new JobStorageCommands.Adder(
				jobStorage, job, statusesContainer.initNewStatus(), statusesContainer);
		executor.add(jsc);
		return responser.makeRedirectResponse(jsc.commandId, uriInfo.getPath());
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
		
		JobStorageCommands.Remover jsc = new JobStorageCommands.Remover(
				jobStorage, job, statusesContainer.initNewStatus(), statusesContainer);
		executor.add(jsc);
		return responser.makeRedirectResponse(jsc.commandId, uriInfo.getPath());
	}
}
