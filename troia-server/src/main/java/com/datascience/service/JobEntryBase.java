/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.datascience.service;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.datascience.core.Job;
import com.datascience.core.base.ContValue;
import com.datascience.core.commands.*;
import com.datascience.core.storages.DataJSON;
import com.datascience.core.storages.IJobStorage;
import com.datascience.executor.CommandStatusesContainer;
import com.datascience.executor.JobCommand;
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

	@Path("")
	@GET
	public Response getJobInfo(){
		return buildResponseOnCommand(new ProjectCommands.GetProjectInfo());
	}

	@Path("objects")
	@GET
	public Response getObjects(){
		return buildResponseOnCommand(new ObjectCommands.GetObjects());
	}

	@Path("objects/{oid:[a-zA-Z_0-9/:.-]+}/info")
	@GET
	public Response getObject(@PathParam("oid") String objectId){
		return buildResponseOnCommand(new ObjectCommands.GetObject(objectId));
	}

	@Path("objects/{oid:[a-zA-Z_0-9/:.-]+}/assigns")
	@GET
	public Response getObjectAssigns(@PathParam("oid") String objectId){
		return buildResponseOnCommand(new ObjectCommands.GetObjectAssigns(objectId));
	}

	@Consumes(MediaType.APPLICATION_JSON)
	@Path("objects")
	@POST
	public Response addObjects(DataJSON.ShallowObjectCollection objects){
		return buildResponseOnCommand(new ObjectCommands.AddObjects(objects));
	}

	@Path("goldObjects")
	@GET
	public Response getGoldObjects(){
		return buildResponseOnCommand(new GoldObjectsCommands.GetGoldObjects());
	}

	@Path("goldObjects/{oid:[a-zA-Z_0-9/:.-]+}")
	@GET
	public Response getGoldObject(@PathParam("oid") String objectId){
		return buildResponseOnCommand(new GoldObjectsCommands.GetGoldObject(objectId));
	}

	@Consumes(MediaType.APPLICATION_JSON)
	@Path("goldObjects")
	@POST
	public Response addGoldObjects(DataJSON.ShallowGoldObjectCollection<ContValue> goldObjects){
		return buildResponseOnCommand(new GoldObjectsCommands.AddGoldObjects(goldObjects));
	}

	@Path("evaluationObjects")
	@GET
	public Response getEvaluationObjects(){
		return buildResponseOnCommand(new EvaluationObjectsCommands.GetEvaluationObjects());
	}

	@Path("evaluationObjects/{oid:[a-zA-Z_0-9/:.-]+}")
	@GET
	public Response getEvaluationObject(@PathParam("oid") String objectId){
		return buildResponseOnCommand(new EvaluationObjectsCommands.GetEvaluationObject(objectId));
	}

	@Consumes(MediaType.APPLICATION_JSON)
	@Path("evaluationObjects")
	@POST
	public Response addEvaluationObjects(DataJSON.ShallowGoldObjectCollection<ContValue> goldObjects){
		return buildResponseOnCommand(new EvaluationObjectsCommands.AddEvaluationObjects(goldObjects));
	}

	@Path("workers")
	@GET
	public Response getWorkers(){
		return buildResponseOnCommand(new WorkerCommands.GetWorkers());
	}

	@Path("workers/{wid:[a-zA-Z_0-9/:.-]+}/info")
	@GET
	public Response getWorker(@PathParam("wid") String worker){
		return buildResponseOnCommand(new WorkerCommands.GetWorker(worker));
	}

	@Path("workers/{wid:[a-zA-Z_0-9/:.-]+}/assigns")
	@GET
	public Response getWorkerAssigns(@PathParam("wid") String worker){
		return buildResponseOnCommand(new AssignsCommands.GetWorkerAssigns<T>(worker));
	}

	@Path("assigns")
	@GET
	public Response getAssigns(){
		return buildResponseOnCommand(new AssignsCommands.GetAssigns<T>());
	}

	@Consumes(MediaType.APPLICATION_JSON)
	@Path("assigns")
	@POST
	public Response addAssigns(DataJSON.ShallowAssignCollection<T> assigns){
		return buildResponseOnCommand(new AssignsCommands.AddAssigns<T>(assigns));
	}
}
