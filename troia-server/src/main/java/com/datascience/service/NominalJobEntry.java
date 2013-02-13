package com.datascience.service;

import java.util.Collection;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.datascience.core.storages.JSONUtils;
import com.datascience.gal.AssignedLabel;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.MisclassificationCost;
import com.datascience.gal.commands.AssignsCommands;
import com.datascience.gal.commands.CategoriesCommands;
import com.datascience.gal.commands.CostsCommands;
import com.datascience.gal.commands.DatumCommands;
import com.datascience.gal.commands.EvaluationCommands;
import com.datascience.gal.commands.JobCommands;
import com.datascience.gal.commands.PredictionCommands;
import com.datascience.gal.commands.DSCommandBase;
import com.datascience.gal.commands.WorkerCommands;
import com.datascience.gal.decision.ILabelProbabilityDistributionCalculator;
import com.datascience.gal.decision.ILabelProbabilityDistributionCostCalculator;
import com.datascience.gal.decision.IObjectLabelDecisionAlgorithm;
import com.datascience.gal.decision.LabelProbabilityDistributionCalculators;
import com.datascience.gal.decision.LabelProbabilityDistributionCostCalculators;
import com.datascience.gal.decision.ObjectLabelDecisionAlgorithms;
import com.datascience.gal.decision.WorkerEstimator;
import com.datascience.gal.evaluation.DataEvaluator;
import com.datascience.gal.evaluation.WorkerEvaluator;
import com.datascience.gal.AbstractDawidSkene;


/**
 * @author Konrad Kurdej
 */
@Path("/jobs/{id}/")
public class NominalJobEntry extends JobEntryBase<AbstractDawidSkene> {

	public NominalJobEntry(){
		expectedClass = AbstractDawidSkene.class;
	}

	@Path("")
	@GET
	public Response getJobInfo(){
		DSCommandBase command = new JobCommands.GetJobInfo(job.getProject());
		return buildResponseOnCommand(job, command);
	}
	
	@Path("categories/")
	@GET
	public Response getCategories(){
		DSCommandBase command = new CategoriesCommands.GetCategories(job.getProject());
		return buildResponseOnCommand(job, command);
	}

	@Path("costs/")
	@POST
	public Response setCosts(@FormParam("costs") String sCosts){
		Collection<MisclassificationCost> costs = serializer.parse(sCosts, JSONUtils.misclassificationCostSetType);
		DSCommandBase command = new CostsCommands.SetCosts(job.getProject(), costs);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("costs/")
	@GET
	public Response getCosts(){
		DSCommandBase command = new CostsCommands.GetCosts(job.getProject());
		return buildResponseOnCommand(job, command);
	}
	
	@Path("labels/markAsGold/")
	@POST
	public Response markAsGold(@FormParam("labels") String labelsString){
		Collection<CorrectLabel> labels = serializer.parse(labelsString, JSONUtils.correctLabelSetType);
		DSCommandBase command = new DatumCommands.MarkDataAsGold(job.getProject(), labels);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("assignedLabels/")
	@POST
	public Response addAssigns(@FormParam("labels") String labelsString){
		Collection<AssignedLabel> labels = serializer.parse(labelsString, JSONUtils.assignedLabelSetType);
		DSCommandBase command = new AssignsCommands.AddAssigns(job.getProject(), labels);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("assignedLabels/")
	@GET
	public Response getAssigns(){
		DSCommandBase command = new AssignsCommands.GetAssigns(job.getProject());
		return buildResponseOnCommand(job, command);
	}
	
	@Path("goldData/")
	@POST
	public Response addGoldData(@FormParam("labels") String labelsString){
		Collection<CorrectLabel> labels = serializer.parse(labelsString, JSONUtils.correctLabelSetType);
		DSCommandBase command = new DatumCommands.AddGoldData(job.getProject(), labels);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("goldData/")
	@GET
	public Response getGoldData(){
		DSCommandBase command = new DatumCommands.GetGoldData(job.getProject());
		return buildResponseOnCommand(job, command);
	}
	
	@Path("evaluationData/")
	@POST
	public Response addEvaluationData(@FormParam("labels") String labelsString){
		Collection<CorrectLabel> labels = serializer.parse(labelsString, JSONUtils.correctLabelSetType);
		DSCommandBase command = new DatumCommands.AddEvaluationData(job.getProject(), labels);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("evaluationData/")
	@GET
	public Response getEvaluationData(){
		DSCommandBase command = new DatumCommands.GetEvaluationData(job.getProject());
		return buildResponseOnCommand(job, command);
	}
	
	@Path("data/")
	@POST
	public Response addData(@FormParam("objects") String objectsString){
		Collection<String> objects = serializer.parse(objectsString, JSONUtils.stringSetType);
		DSCommandBase command = new DatumCommands.AddData(job.getProject(), objects);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("data/")
	@GET
	public Response getData(@DefaultValue("all") @QueryParam("type") String type){
		DSCommandBase command = new DatumCommands.GetData(job.getProject(), type);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("data/{id: [a-zA-Z_0-9/:.-]+}")
	@GET
	public Response getDatum(@PathParam("id") String did){
		DSCommandBase command = new DatumCommands.GetDatum(job.getProject(), did);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("data/{id: [a-zA-Z_0-9/:.-]+}/categoryProbability")
	@GET
	public Response getDatumCategoryProbability(@PathParam("id") String did, 
			@DefaultValue("DS") @QueryParam("type") String type){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(type);
		DSCommandBase command = new DatumCommands.GetDatumCategoryProbability(job.getProject(), did, lpdc);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("workers/{id}")
	@GET
	public Response getWorker(@PathParam("id") String wid){
		DSCommandBase command = new WorkerCommands.GetWorker(job.getProject(), wid);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("workers/")
	@GET
	public Response getWorkers(){
		DSCommandBase command = new WorkerCommands.GetWorkers(job.getProject());
		return buildResponseOnCommand(job, command);
	}
	
	@Path("compute/")
	@POST
	public Response compute(@DefaultValue("10") @FormParam("iterations") int iterations){
		DSCommandBase command = new PredictionCommands.Compute(job.getProject(), iterations);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("prediction/data/")
	@GET
	public Response getPredictionData(@DefaultValue("DS") @QueryParam("algorithm") String lpd,
			@DefaultValue("MaxLikelihood") @QueryParam("labelChoosing") String lda){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
		IObjectLabelDecisionAlgorithm olda = ObjectLabelDecisionAlgorithms.get(lda);
		DSCommandBase command = new PredictionCommands.GetPredictedCategory(job.getProject(), lpdc, olda);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("prediction/dataCost/")
	@GET
	public Response getEstimatedDataCost(@DefaultValue("DS") @QueryParam("algorithm") String lpd,
			@DefaultValue("ExpectedCost") @QueryParam("costAlgorithm") String lca){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
		ILabelProbabilityDistributionCostCalculator lpdcc = LabelProbabilityDistributionCostCalculators.get(lca);
		DSCommandBase command = new PredictionCommands.GetCost(job.getProject(), lpdc, lpdcc);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("prediction/dataQuality/")
	@GET
	public Response getEstimatedDataQuality(@DefaultValue("DS") @QueryParam("algorithm") String lpd,
			@DefaultValue("ExpectedCost") @QueryParam("costAlgorithm") String lca){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
		ILabelProbabilityDistributionCostCalculator lpdcc = LabelProbabilityDistributionCostCalculators.get(lca);
		DSCommandBase command = new PredictionCommands.GetQuality(job.getProject(), lpdc, lpdcc);
		return buildResponseOnCommand(job, command);
	}

	@Path("evaluation/dataCost/")
	@GET
	public Response getEvaluatedDataCost(@DefaultValue("DS") @QueryParam("algorithm") String lpd,
			@DefaultValue("MaxLikelihood") @QueryParam("labelChoosing") String lda){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
		DataEvaluator dataEvaluator= DataEvaluator.get(lda, lpdc);
		DSCommandBase command = new EvaluationCommands.GetCost(job.getProject(), dataEvaluator);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("evaluation/dataQuality/")
	@GET
	public Response getEvaluatedDataQuality(@DefaultValue("DS") @QueryParam("algorithm") String lpd,
			@DefaultValue("MaxLikelihood") @QueryParam("labelChoosing") String lda){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
		DataEvaluator dataEvaluator= DataEvaluator.get(lda, lpdc);
		DSCommandBase command = new EvaluationCommands.GetQuality(job.getProject(), dataEvaluator);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("evaluation/workersQuality")
	@GET
	public Response getEvaluatedWorkersQuality(@DefaultValue("ExpectedCost") @QueryParam("costAlgorithm") String lca){
		ILabelProbabilityDistributionCostCalculator lpdcc = LabelProbabilityDistributionCostCalculators.get(lca);
		DSCommandBase command = new WorkerCommands.GetWorkersQuality(job.getProject(), new WorkerEvaluator(lpdcc));
		return buildResponseOnCommand(job, command);
	}
	
	@Path("prediction/workersQuality")
	@GET
	public Response getWorkersQuality(@DefaultValue("ExpectedCost") @QueryParam("costAlgorithm") String lca){
		ILabelProbabilityDistributionCostCalculator lpdcc = LabelProbabilityDistributionCostCalculators.get(lca);
		DSCommandBase command = new WorkerCommands.GetWorkersQuality(job.getProject(), new WorkerEstimator(lpdcc));
		return buildResponseOnCommand(job, command);
	}
}
