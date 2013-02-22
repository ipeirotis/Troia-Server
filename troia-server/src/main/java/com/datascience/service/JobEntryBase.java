/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.datascience.service;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.datascience.core.Job;
import com.datascience.core.storages.IJobStorage;
import com.datascience.executor.CommandStatusesContainer;
import com.datascience.executor.ProjectCommand;
import com.datascience.executor.ProjectCommandExecutor;

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
	protected Class expectedClass;
	ResponseBuilder responser;
	ProjectCommandExecutor executor;
	ISerializer serializer;
	CommandStatusesContainer statusesContainer;
	IJobStorage jobStorage;
	JobsManager jobsManager;
	
	@PostConstruct
	public void postConstruct() throws Exception{
		jobStorage = (IJobStorage) context.getAttribute(Constants.JOBS_STORAGE);
		responser = (ResponseBuilder) context.getAttribute(Constants.RESPONSER);
		executor = (ProjectCommandExecutor) context.getAttribute(Constants.COMMAND_EXECUTOR);
		statusesContainer = (CommandStatusesContainer) context.getAttribute(Constants.COMMAND_STATUSES_CONTAINER);
		serializer = responser.getSerializer();
		jobsManager = (JobsManager) context.getAttribute(Constants.JOBS_MANAGER);
		
		Logger.getAnonymousLogger().info(uriInfo.getPath());
	}
	
	protected Response buildResponseOnCommand(ProjectCommand command){
		command.setJobId(jid);
		command.setJobStorage(jobStorage);
		RequestExecutorCommand rec = new RequestExecutorCommand(
				statusesContainer.initNewStatus(), command, jobsManager.getLock(jid), statusesContainer);
		executor.add(rec);
		return responser.makeRedirectResponse(String.format("responses/%s/%s/%s", rec.commandId, request.getMethod(), uriInfo.getPath()));
	}
}
