/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.datascience.service;

import com.datascience.core.Job;
import com.datascience.core.storages.IJobStorage;
import com.datascience.executor.CommandStatusesContainer;
import com.datascience.executor.ProjectCommandExecutor;
import com.datascience.gal.commands.DSCommandBase;
import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author konrad
 */
public abstract class JobEntryBase<T> {
	
	@Context ServletContext context;
	@Context Request request;
	@Context UriInfo uriInfo;
	@PathParam("id") String jid;
	
	Job<T> job;
	ResponseBuilder responser;
	ProjectCommandExecutor executor;
	ISerializer serializer;
	CommandStatusesContainer statusesContainer;
	IJobStorage jobStorage;
	
	@PostConstruct
	public void postConstruct() throws Exception{
		jobStorage = (IJobStorage) context.getAttribute(Constants.JOBS_STORAGE);
		responser = (ResponseBuilder) context.getAttribute(Constants.RESPONSER);
		executor = (ProjectCommandExecutor) context.getAttribute(Constants.COMMAND_EXECUTOR);
		statusesContainer = (CommandStatusesContainer) context.getAttribute(Constants.COMMAND_STATUSES_CONTAINER);
		serializer = responser.getSerializer();
		
		job = jobStorage.get(jid);
		if (job == null) {
			throw new IllegalArgumentException("Job with ID " + jid + " does not exist");
		}
	}
	
	protected Response buildResponseOnCommand(Job job, DSCommandBase command){
		RequestExecutorCommand rec = new RequestExecutorCommand(
				statusesContainer.initNewStatus(), command, job.getRWLock(), statusesContainer);
		executor.add(rec);
		return responser.makeRedirectResponse(String.format("responses/%s/%s/%s", rec.commandId, request.getMethod(), uriInfo.getPath()));
	}
}
