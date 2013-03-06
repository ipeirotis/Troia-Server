/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.datascience.service;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.datascience.core.Job;
import com.datascience.core.storages.IJobStorage;
import com.datascience.executor.CommandStatusesContainer;
import com.datascience.executor.JobCommand;
import com.datascience.executor.ProjectCommandExecutor;
import com.datascience.galc.commands.ProjectCommands;

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

	protected abstract JobCommand getPredictionZipCommand(String path);

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

	protected Response buildResponseOnCommand(JobCommand command){
		command.setJobId(jid);
		command.setJobStorage(jobStorage);
		RequestExecutorCommand rec = new RequestExecutorCommand(
				statusesContainer.initNewStatus(), command, jobsManager.getLock(jid), statusesContainer);
		executor.add(rec);
		return responser.makeRedirectResponse(String.format("responses/%s/%s/%s", rec.commandId, request.getMethod(), uriInfo.getPath()));
	}

	@Path("prediction/zip")
	@GET
	public Response getPredictionsZip(){
		return buildResponseOnCommand(getPredictionZipCommand((String)context.getAttribute(Constants.DOWNLOADS_PATH)));
	}
}
