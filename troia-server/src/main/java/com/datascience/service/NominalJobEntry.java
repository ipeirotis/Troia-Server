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
import com.datascience.gal.AbstractDawidSkene;
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
		return buildResponseOnCommand(new JobCommands.GetJobInfo());
	}
	
	@Path("categories/")
	@GET
	public Response getCategories(){
		return buildResponseOnCommand(new CategoriesCommands.GetCategories());
	}

	@Path("costs/")
	@POST
	public Response setCosts(@FormParam("costs") String sCosts){
		Collection<MisclassificationCost> costs = serializer.parse(sCosts, JSONUtils.misclassificationCostSetType);
		return buildResponseOnCommand(new CostsCommands.SetCosts(costs));
	}
	
	@Path("costs/")
	@GET
	public Response getCosts(){
		return buildResponseOnCommand(new CostsCommands.GetCosts());
	}
	
	@Path("labels/markAsGold/")
	@POST
	public Response markAsGold(@FormParam("labels") String labelsString){
		Collection<CorrectLabel> labels = serializer.parse(labelsString, JSONUtils.correctLabelSetType);
		return buildResponseOnCommand(new DatumCommands.MarkDataAsGold(labels));
	}
	
	@Path("assignedLabels/")
	@POST
	public Response addAssigns(@FormParam("labels") String labelsString){
		Collection<AssignedLabel> labels = serializer.parse(labelsString, JSONUtils.assignedLabelSetType);
		return buildResponseOnCommand(new AssignsCommands.AddAssigns(labels));
	}
	
	@Path("assignedLabels/")
	@GET
	public Response getAssigns(){
		return buildResponseOnCommand(new AssignsCommands.GetAssigns());
	}
	
	@Path("goldData/")
	@POST
	public Response addGoldData(@FormParam("labels") String labelsString){
		Collection<CorrectLabel> labels = serializer.parse(labelsString, JSONUtils.correctLabelSetType);
		return buildResponseOnCommand(new DatumCommands.AddGoldData(labels));
	}
	
	@Path("goldData/")
	@GET
	public Response getGoldData(){
		return buildResponseOnCommand(new DatumCommands.GetGoldData());
	}
	
	@Path("evaluationData/")
	@POST
	public Response addEvaluationData(@FormParam("labels") String labelsString){
		Collection<CorrectLabel> labels = serializer.parse(labelsString, JSONUtils.correctLabelSetType);
		return buildResponseOnCommand(new DatumCommands.AddEvaluationData(labels));
	}
	
	@Path("evaluationData/")
	@GET
	public Response getEvaluationData(){
		return buildResponseOnCommand(new DatumCommands.GetEvaluationData());
	}
	
	@Path("data/")
	@POST
	public Response addData(@FormParam("objects") String objectsString){
		Collection<String> objects = serializer.parse(objectsString, JSONUtils.stringSetType);
		return buildResponseOnCommand(new DatumCommands.AddData(objects));
	}
	
	@Path("data/")
	@GET
	public Response getData(@DefaultValue("all") @QueryParam("type") String type){
		return buildResponseOnCommand(new DatumCommands.GetData(type));
	}
	
	@Path("data/{id: [a-zA-Z_0-9/:.-]+}")
	@GET
	public Response getDatum(@PathParam("id") String did){
		return buildResponseOnCommand(new DatumCommands.GetDatum(did));
	}
	
	@Path("data/{id: [a-zA-Z_0-9/:.-]+}/categoryProbability")
	@GET
	public Response getDatumCategoryProbability(@PathParam("id") String did, 
			@DefaultValue("DS") @QueryParam("type") String type){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(type);
		return buildResponseOnCommand(new DatumCommands.GetDatumCategoryProbability( did, lpdc));
	}
	
	@Path("workers/{id}")
	@GET
	public Response getWorker(@PathParam("id") String wid){
		return buildResponseOnCommand(new WorkerCommands.GetWorker( wid));
	}
	
	@Path("workers/")
	@GET
	public Response getWorkers(){
		return buildResponseOnCommand(new WorkerCommands.GetWorkers());
	}
	
	@Path("compute/")
	@POST
	public Response compute(@DefaultValue("10") @FormParam("iterations") int iterations){
		return buildResponseOnCommand(new PredictionCommands.Compute( iterations));
	}
	
	@Path("prediction/data/")
	@GET
	public Response getPredictionData(@DefaultValue("DS") @QueryParam("algorithm") String lpd,
			@DefaultValue("MaxLikelihood") @QueryParam("labelChoosing") String lda){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
		IObjectLabelDecisionAlgorithm olda = ObjectLabelDecisionAlgorithms.get(lda);
		return buildResponseOnCommand(new PredictionCommands.GetPredictedCategory( lpdc, olda));
	}
	
	@Path("prediction/dataCost/")
	@GET
	public Response getEstimatedDataCost(@DefaultValue("DS") @QueryParam("algorithm") String lpd,
			@DefaultValue("ExpectedCost") @QueryParam("costAlgorithm") String lca){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
		ILabelProbabilityDistributionCostCalculator lpdcc = LabelProbabilityDistributionCostCalculators.get(lca);
		return buildResponseOnCommand(new PredictionCommands.GetCost( lpdc, lpdcc));
	}
	
	@Path("prediction/dataQuality/")
	@GET
	public Response getEstimatedDataQuality(@DefaultValue("DS") @QueryParam("algorithm") String lpd,
			@DefaultValue("ExpectedCost") @QueryParam("costAlgorithm") String lca){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
		ILabelProbabilityDistributionCostCalculator lpdcc = LabelProbabilityDistributionCostCalculators.get(lca);
		return buildResponseOnCommand(new PredictionCommands.GetQuality( lpdc, lpdcc));
	}

	@Path("evaluation/dataCost/")
	@GET
	public Response getEvaluatedDataCost(@DefaultValue("DS") @QueryParam("algorithm") String lpd,
			@DefaultValue("MaxLikelihood") @QueryParam("labelChoosing") String lda){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
		DataEvaluator dataEvaluator= DataEvaluator.get(lda, lpdc);
		return buildResponseOnCommand(new EvaluationCommands.GetCost( dataEvaluator));
	}
	
	@Path("evaluation/dataQuality/")
	@GET
	public Response getEvaluatedDataQuality(@DefaultValue("DS") @QueryParam("algorithm") String lpd,
			@DefaultValue("MaxLikelihood") @QueryParam("labelChoosing") String lda){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
		DataEvaluator dataEvaluator= DataEvaluator.get(lda, lpdc);
		return buildResponseOnCommand(new EvaluationCommands.GetQuality( dataEvaluator));
	}
	
	@Path("evaluation/workersQuality")
	@GET
	public Response getEvaluatedWorkersQuality(@DefaultValue("ExpectedCost") @QueryParam("costAlgorithm") String lca){
		ILabelProbabilityDistributionCostCalculator lpdcc = LabelProbabilityDistributionCostCalculators.get(lca);
		return buildResponseOnCommand(new WorkerCommands.GetWorkersQuality( new WorkerEvaluator(lpdcc)));
	}
	
	@Path("prediction/workersQuality")
	@GET
	public Response getWorkersQuality(@DefaultValue("ExpectedCost") @QueryParam("costAlgorithm") String lca){
		ILabelProbabilityDistributionCostCalculator lpdcc = LabelProbabilityDistributionCostCalculators.get(lca);
		return buildResponseOnCommand(new WorkerCommands.GetWorkersQuality( new WorkerEstimator(lpdcc)));
	}

	@Path("prediction/zip")
	@GET
	public Response getPredictionZip(){
		return buildResponseOnCommand(new PredictionCommands.GetStatistics(
				(String)context.getAttribute(Constants.DOWNLOADS_PATH)));
	}
}
