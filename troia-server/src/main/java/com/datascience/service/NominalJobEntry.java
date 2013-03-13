package com.datascience.service;


import com.datascience.core.storages.serialization.json.JSONUtils;
import com.datascience.executor.JobCommand;
import com.datascience.gal.AbstractDawidSkene;
import com.datascience.core.nominal.NominalProject;
import com.datascience.gal.commands.*;
import com.datascience.gal.decision.*;
import com.datascience.gal.evaluation.DataEvaluator;
import com.datascience.gal.evaluation.WorkerEvaluator;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;


/**
 * @author Konrad Kurdej
 */
@Path("/jobs/{id}/")
public class NominalJobEntry extends JobEntryBase<NominalProject> {

	public NominalJobEntry(){
		expectedClass = AbstractDawidSkene.class;
		objectsType = JSONUtils.objectsStringType;
		assignsType = JSONUtils.assignsStringType;
	}

	@Path("categories/")
	@GET
	public Response getCategories(){
		return buildResponseOnCommand(new CategoriesCommands.GetCategories());
	}

//	@Path("costs/")
//	@POST
//	public Response setCosts(@FormParam("costs") String sCosts){
//		Collection<MisclassificationCost> costs = serializer.parse(sCosts, JSONUtils.misclassificationCostSetType);
//		return buildResponseOnCommand(new CostsCommands.SetCosts(costs));
//	}
	
	@Path("costs/")
	@GET
	public Response getCosts(){
		return buildResponseOnCommand(new CostsCommands.GetCosts());
	}

	@Path("objects/{oid:[a-zA-Z_0-9/:.-]+}/categoryProbability")
	@GET
	public Response getDatumCategoryProbability(@PathParam("id") String did, 
			@DefaultValue("DS") @QueryParam("type") String type){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(type);
		return buildResponseOnCommand(new DatumCommands.GetDatumCategoryProbability( did, lpdc));
	}

	@Path("objects/prediction/")
	@GET
	public Response getPredictionData(@DefaultValue("DS") @QueryParam("algorithm") String lpd,
			@DefaultValue("MaxLikelihood") @QueryParam("labelChoosing") String lda){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
		IObjectLabelDecisionAlgorithm olda = ObjectLabelDecisionAlgorithms.get(lda);
		return buildResponseOnCommand(new PredictionCommands.GetPredictedCategory( lpdc, olda));
	}
	
	@Path("objects/cost/estimated")
	@GET
	public Response getEstimatedDataCost(@DefaultValue("DS") @QueryParam("algorithm") String lpd,
			@DefaultValue("ExpectedCost") @QueryParam("costAlgorithm") String lca){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
		ILabelProbabilityDistributionCostCalculator lpdcc = LabelProbabilityDistributionCostCalculators.get(lca);
		return buildResponseOnCommand(new PredictionCommands.GetDataCost( lpdc, lpdcc));
	}
	
	@Path("objects/quality/estimated/")
	@GET
	public Response getEstimatedDataQuality(@DefaultValue("DS") @QueryParam("algorithm") String lpd,
			@DefaultValue("ExpectedCost") @QueryParam("costAlgorithm") String lca){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
		ILabelProbabilityDistributionCostCalculator lpdcc = LabelProbabilityDistributionCostCalculators.get(lca);
		return buildResponseOnCommand(new PredictionCommands.GetDataQuality( lpdc, lpdcc));
	}

	@Path("objects/cost/evaluated/")
	@GET
	public Response getEvaluatedDataCost(@DefaultValue("DS") @QueryParam("algorithm") String lpd,
			@DefaultValue("MaxLikelihood") @QueryParam("labelChoosing") String lda){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
		DataEvaluator dataEvaluator= DataEvaluator.get(lda, lpdc);
		return buildResponseOnCommand(new EvaluationCommands.GetDataCost( dataEvaluator));
	}
	
	@Path("objects/quality/evaluated/")
	@GET
	public Response getEvaluatedDataQuality(@DefaultValue("DS") @QueryParam("algorithm") String lpd,
			@DefaultValue("MaxLikelihood") @QueryParam("labelChoosing") String lda){
		ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
		DataEvaluator dataEvaluator= DataEvaluator.get(lda, lpdc);
		return buildResponseOnCommand(new EvaluationCommands.GetDataQuality( dataEvaluator));
	}
	
	@Path("workers/quality/evaluated/")
	@GET
	public Response getEvaluatedWorkersQuality(@DefaultValue("ExpectedCost") @QueryParam("costAlgorithm") String lca){
		ILabelProbabilityDistributionCostCalculator lpdcc = LabelProbabilityDistributionCostCalculators.get(lca);
		return buildResponseOnCommand(new PredictionCommands.GetWorkersQuality( new WorkerEvaluator(lpdcc)));
	}
	
	@Path("workers/quality/estimated/")
	@GET
	public Response getWorkersQuality(@DefaultValue("ExpectedCost") @QueryParam("costAlgorithm") String lca){
		ILabelProbabilityDistributionCostCalculator lpdcc = LabelProbabilityDistributionCostCalculators.get(lca);
		return buildResponseOnCommand(new PredictionCommands.GetWorkersQuality( new WorkerEstimator(lpdcc)));
	}

	@Path("workers/quality/matrix/")
	@GET
	public Response getWorkersConfusionMatrices(){
		return buildResponseOnCommand(new PredictionCommands.GetWorkersConfusionMatrix());
	}

	@Override
	protected JobCommand getPredictionZipCommand(String path){
		return new PredictionCommands.GetPredictionZip(path);
	}

}
