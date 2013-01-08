package com.datascience.gal.service;

import java.util.Collection;
import java.util.NoSuchElementException;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.datascience.core.Job;
import com.datascience.core.storages.JSONUtils;
import com.datascience.gal.AssignedLabel;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.MisclassificationCost;
import com.datascience.gal.commands.AssignsCommands;
import com.datascience.gal.commands.CategoriesCommands;
import com.datascience.gal.commands.CommandStatus;
import com.datascience.gal.commands.CostsCommands;
import com.datascience.gal.commands.DatumCommands;
import com.datascience.gal.commands.EvaluationCommands;
import com.datascience.gal.commands.JobCommands;
import com.datascience.gal.commands.PredictionCommands;
import com.datascience.gal.commands.ProjectCommand;
import com.datascience.gal.commands.WorkerCommands;
import com.datascience.gal.decision.ILabelProbabilityDistributionCalculator;
import com.datascience.gal.decision.ILabelProbabilityDistributionCostCalculator;
import com.datascience.gal.decision.IObjectLabelDecisionAlgorithm;
import com.datascience.gal.decision.LabelProbabilityDistributionCalculators;
import com.datascience.gal.decision.LabelProbabilityDistributionCostCalculators;
import com.datascience.gal.decision.ObjectLabelDecisionAlgorithms;
import com.datascience.gal.evaluation.DataEvaluator;
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
	
	@Path("")
	@GET
	public Response getJobInfo(){
		ProjectCommand command = new JobCommands.GetJobInfo(job.getDs());
		return buildResponseOnCommand(job, command);
	}
	
	@Path("categories/")
	@GET
	public Response getCategories(){
		ProjectCommand command = new CategoriesCommands.GetCategories(job.getDs());
		return buildResponseOnCommand(job, command);
	}

	@Path("costs/")
	@POST
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
	
	@Path("labels/markAsGold/")
	@POST
	public Response markAsGold(@FormParam("labels") String labelsString){
		Collection<CorrectLabel> labels = serializer.parse(labelsString, JSONUtils.correctLabelSetType);
		ProjectCommand command = new DatumCommands.MarkDataAsGold(job.getDs(), labels);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("assignedLabels/")
	@POST
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
	
	@Path("goldData/")
	@POST
	public Response addGoldData(@FormParam("labels") String labelsString){
		Collection<CorrectLabel> labels = serializer.parse(labelsString, JSONUtils.correctLabelSetType);
		ProjectCommand command = new DatumCommands.AddGoldData(job.getDs(), labels);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("goldData/")
	@GET
	public Response getGoldData(){
		ProjectCommand command = new DatumCommands.GetGoldData(job.getDs());
		return buildResponseOnCommand(job, command);
	}
	
	@Path("evaluationData/")
	@POST
	public Response addEvaluationData(@FormParam("labels") String labelsString){
		Collection<CorrectLabel> labels = serializer.parse(labelsString, JSONUtils.correctLabelSetType);
		ProjectCommand command = new DatumCommands.AddEvaluationData(job.getDs(), labels);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("evaluationData/")
	@GET
	public Response getEvaluationData(){
		ProjectCommand command = new DatumCommands.GetEvaluationData(job.getDs());
		return buildResponseOnCommand(job, command);
	}
	
	@Path("data/")
	@POST
	public Response addData(@FormParam("objects") String objectsString){
		Collection<String> objects = serializer.parse(objectsString, JSONUtils.stringSetType);
		ProjectCommand command = new DatumCommands.AddData(job.getDs(), objects);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("data/")
	@GET
	public Response getData(@DefaultValue("all") @QueryParam("type") String type){
		ProjectCommand command = new DatumCommands.GetData(job.getDs(), type);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("data/{id: [a-zA-Z_0-9/:.]+}")
	@GET
	public Response getDatum(@PathParam("id") String did){
		ProjectCommand command = new DatumCommands.GetDatum(job.getDs(), did);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("data/{id: [a-zA-Z_0-9/:.]+}/categoryProbability")
	@GET
	public Response getDatumCategoryProbability(@PathParam("id") String did, 
			@DefaultValue("DS") @QueryParam("type") String type){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(type);
		ProjectCommand command = new DatumCommands.GetDatumCategoryProbability(job.getDs(), did, lpdc);
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
			throw new NoSuchElementException("No status with id: " + sid);
		}
		switch(status.getStatus()){
			case OK:
				return responser.makeOKResponse(status.getData());
			case ERROR:
				return responser.makeExceptionResponse(status.getError());
			case NOT_READY:
				return responser.makeNotReadyResponse();
			default:
				return null;
		}
	}
	
	@Path("compute/")
	@POST
	public Response compute(@DefaultValue("10") @FormParam("iterations") int iterations){
		ProjectCommand command = new PredictionCommands.Compute(job.getDs(), iterations);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("prediction/data/")
	@GET
	public Response getPredictionData(@DefaultValue("DS") @QueryParam("algorithm") String lpd,
			@DefaultValue("MaxLikelihood") @QueryParam("labelChoosing") String lda){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
		IObjectLabelDecisionAlgorithm olda = ObjectLabelDecisionAlgorithms.get(lda);
		ProjectCommand command = new PredictionCommands.GetPredictedCategory(job.getDs(), lpdc, olda);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("prediction/dataCost/")
	@GET
	public Response getEstimatedDataCost(@DefaultValue("DS") @QueryParam("algorithm") String lpd,
			@DefaultValue("ExpectedCost") @QueryParam("costAlgorithm") String lca){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
		ILabelProbabilityDistributionCostCalculator lpdcc = LabelProbabilityDistributionCostCalculators.get(lca);
		ProjectCommand command = new PredictionCommands.GetCost(job.getDs(), lpdc, lpdcc);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("prediction/dataQuality/")
	@GET
	public Response getEstimatedDataQuality(@DefaultValue("DS") @QueryParam("algorithm") String lpd,
			@DefaultValue("ExpectedCost") @QueryParam("costAlgorithm") String lca){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
		ILabelProbabilityDistributionCostCalculator lpdcc = LabelProbabilityDistributionCostCalculators.get(lca);
		ProjectCommand command = new PredictionCommands.GetQuality(job.getDs(), lpdc, lpdcc);
		return buildResponseOnCommand(job, command);
	}

	@Path("evaluation/dataCost/")
	@GET
	public Response getEvaluatedDataCost(@DefaultValue("DS") @QueryParam("algorithm") String lpd,
			@DefaultValue("MaxLikelihood") @QueryParam("labelChoosing") String lda){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
		DataEvaluator dataEvaluator= DataEvaluator.get(lpd, lpdc);
		ProjectCommand command = new EvaluationCommands.GetCost(job.getDs(), dataEvaluator);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("evaluation/dataQuality/")
	@GET
	public Response getEvaluatedDataQuality(@DefaultValue("DS") @QueryParam("algorithm") String lpd,
			@DefaultValue("MaxLikelihood") @QueryParam("labelChoosing") String lda){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
		DataEvaluator dataEvaluator= DataEvaluator.get(lpd, lpdc);
		ProjectCommand command = new EvaluationCommands.GetQuality(job.getDs(), dataEvaluator);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("prediction/workersQuality")
	@GET
	public Response getWorkersQuality(@DefaultValue("ExpectedCost") @QueryParam("costAlgorithm") String lca){
		ILabelProbabilityDistributionCostCalculator lpdcc = LabelProbabilityDistributionCostCalculators.get(lca);
		ProjectCommand command = new WorkerCommands.GetWorkersQuality(job.getDs(), lpdcc);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("prediction/workersCost")
	@GET
	public Response getWorkersCost(@DefaultValue("ExpectedCost") @QueryParam("costAlgorithm") String lca){
		ILabelProbabilityDistributionCostCalculator lpdcc = LabelProbabilityDistributionCostCalculators.get(lca);
		ProjectCommand command = new WorkerCommands.GetWorkersCost(job.getDs(), lpdcc);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("prediction/workers")
	@GET
	public Response getWorkersScore(){
		ProjectCommand command = new WorkerCommands.GetWorkersScores(job.getDs());
		return buildResponseOnCommand(job, command);
	}
	
	@Path("prediction/workers/{id}")
	@GET
	public Response getWorkerScore(@PathParam("id") String wid){
		ProjectCommand command = new WorkerCommands.GetWorkerScores(job.getDs(), wid);
		return buildResponseOnCommand(job, command);
	}
}
