package com.datascience.gal.service;

import java.util.Collection;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.datascience.core.Job;
import com.datascience.core.storages.JSONUtils;
import com.datascience.gal.AssignedLabel;
import com.datascience.gal.Category;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.MisclassificationCost;
import com.datascience.gal.commands.AssignsCommands;
import com.datascience.gal.commands.CategoriesCommands;
import com.datascience.gal.commands.CommandStatus;
import com.datascience.gal.commands.CostsCommands;
import com.datascience.gal.commands.DatumCommands;
import com.datascience.gal.commands.PredictionCommands;
import com.datascience.gal.commands.ProjectCommand;
import com.datascience.gal.commands.WorkerCommands;
import com.datascience.gal.executor.ProjectCommandExecutor;

/**
 * @author Konrad Kurdej
 */
public class JobEntry {

	Job job;
	ResponseBuilder responser;
	ProjectCommandExecutor commandExecutor;
	ISerializer serializer;

	public JobEntry(Job job, ProjectCommandExecutor commandExecutor, ResponseBuilder responser){
		this.job = job;
		this.commandExecutor = commandExecutor;
		this.responser = responser;
		serializer = responser.getSerializer();
	}
	
	protected Response buildResponseOnCommand(Job job, ProjectCommand command){
		RequestExecutorCommand rec = new RequestExecutorCommand(
			job.getCommandStatusesContainer().initNewStatus(), command,
			job.getRWLock(), job.getCommandStatusesContainer());
		commandExecutor.add(rec);
		return responser.makeRedirectResponse(rec.commandId);
	}
	
	
	@Path("categories/")
	@PUT
	public Response setCategories(@FormParam("categories") String sCategories){
		Collection<Category> categories = serializer.parse(sCategories, JSONUtils.categorySetType);
		ProjectCommand command = new CategoriesCommands.SetCategories(job.getDs(), categories);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("categories/")
	@GET
	public Response getCategories(){
		ProjectCommand command = new CategoriesCommands.GetCategories(job.getDs());
		return buildResponseOnCommand(job, command);
	}
	

	@Path("costs/")
	@PUT
	public Response setCosts(@FormParam("costs") String sCosts){
		Collection<MisclassificationCost> costs = serializer.parse(sCosts, JSONUtils.misclassificationCostSetType);
		ProjectCommand command = new CostsCommands.SetCosts(job.getDs(), costs);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("costs/")
	@GET
	public Response getCosts(){
		ProjectCommand command = new CostsCommands.GetCosts(job.getDs());
		return buildResponseOnCommand(job, command);
	}
	
	@Path("assignedLabels/")
	@PUT
	public Response addAssigns(@FormParam("labels") String labelsString){
		Collection<AssignedLabel> labels = serializer.parse(labelsString, JSONUtils.assignedLabelSetType);
		ProjectCommand command = new AssignsCommands.AddAssigns(job.getDs(), labels);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("assignedLabels/")
	@GET
	public Response getAssigns(){
		ProjectCommand command = new AssignsCommands.GetAssigns(job.getDs());
		return buildResponseOnCommand(job, command);
	}
	
	@Path("goldDatums/")
	@PUT
	public Response addGoldDatums(@FormParam("labels") String labelsString){
		Collection<CorrectLabel> labels = serializer.parse(labelsString, JSONUtils.correctLabelSetType);
		ProjectCommand command = new DatumCommands.AddGoldDatums(job.getDs(), labels);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("goldDatums/")
	@GET
	public Response getGoldDatums(){
		ProjectCommand command = new DatumCommands.GetGoldDatums(job.getDs());
		return buildResponseOnCommand(job, command);
	}
	
	@Path("evaluationDatums/")
	@PUT
	public Response addEvaluationDatums(@FormParam("labels") String labelsString){
		Collection<CorrectLabel> labels = serializer.parse(labelsString, JSONUtils.correctLabelSetType);
		ProjectCommand command = new DatumCommands.AddEvaluationDatums(job.getDs(), labels);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("evaluationDatums/")
	@GET
	public Response getEvaluationDatums(){
		ProjectCommand command = new DatumCommands.GetEvaluationDatums(job.getDs());
		return buildResponseOnCommand(job, command);
	}
	
	@Path("datums/")
	@GET
	public Response getDatums(){
		ProjectCommand command = new DatumCommands.GetDatums(job.getDs());
		return buildResponseOnCommand(job, command);
	}
	
	@Path("datums/{id}")
	@GET
	public Response getDatum(@PathParam("id") String did){
		ProjectCommand command = new DatumCommands.GetDatum(job.getDs(), did);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("workers/{id}")
	@GET
	public Response getWorker(@PathParam("id") String wid){
		ProjectCommand command = new WorkerCommands.GetWorker(job.getDs(), wid);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("workers/")
	@GET
	public Response getWorkers(){
		ProjectCommand command = new WorkerCommands.GetWorkers(job.getDs());
		return buildResponseOnCommand(job, command);
	}
	
	@Path("status/{id}")
	@GET
	public Response getStatus(@PathParam("id") String sid){
		CommandStatus status = job.getCommandStatusesContainer()
			.getCommandResult(sid);
		if (status == null) {
			throw ServiceException.notFoundException(responser);
		}
		switch(status.getStatus()){
			case OK:
				return responser.makeOKResponse(status.getData());
			case ERROR:
				return responser.makeExceptionResponse(status.getError());
			case NOT_READY:
				return responser.makeNotReadyResponse();
			default://should never happend
				return null;
		}
	}
	
	@Path("compute/")
	@POST
	public Response compute(@DefaultValue("10") @FormParam("iterations") int iterations){
		ProjectCommand command = new PredictionCommands.Compute(job.getDs(), iterations);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("prediction/{algorithm}/datums/")
	@GET
	public Response getPredictionDatums(@PathParam("algorithm") String algorithm){
		if (!algorithm.equals("MV")) {
			throw ServiceException.wrongArgumentException(responser, "Unknown algorithm type: " + algorithm);
		}
		ProjectCommand command = new PredictionCommands.GetDatums(job.getDs(), algorithm);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("prediction/{algorithm}/datums/{id}")
	@GET
	public Response getPredictionDatums(@PathParam("algorithm") String algorithm, @PathParam("id") String did){
		if (!algorithm.equals("MV")) {
			throw ServiceException.wrongArgumentException(responser, "Unknown algorithm type: " + algorithm);
		}
		ProjectCommand command = new PredictionCommands.GetDatum(job.getDs(), did, algorithm);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("prediction/{algorithm}/datums/{id}/{costDecisionAlg}")
	@GET
	public Response getEstimatedCost(@PathParam("algorithm") String algorithm, 
			@PathParam("id") String did, 
			@PathParam("costDecisionAlg") String cda){
		if (!algorithm.equals("MV")) {
			throw ServiceException.wrongArgumentException(responser, "Unknown algorithm type: " + algorithm);
		}
		ProjectCommand command = new PredictionCommands.GetCost(job.getDs(), did, algorithm, cda);
		return buildResponseOnCommand(job, command);
	}
}
