package com.datascience.service;


import com.datascience.serialization.json.JSONUtils;
import com.datascience.core.jobs.JobCommand;
import com.datascience.gal.AbstractDawidSkene;
import com.datascience.core.nominal.NominalProject;
import com.datascience.gal.commands.*;
import com.datascience.core.nominal.decision.*;
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
	
	@Path("costs/")
	@GET
	public Response getCosts(){
		return buildResponseOnCommand(new CostsCommands.GetCosts());
	}

	@Path("objects/{oid:[a-zA-Z_0-9/:.-]+}/categoryProbability")
	@GET
	public Response getDatumCategoryProbability(@PathParam("oid") String did){
		return buildResponseOnCommand(new DatumCommands.GetDatumCategoryProbability( did));
	}

	@Path("objects/prediction/")
	@GET
	public Response getPredictionData(@DefaultValue("MaxLikelihood") @QueryParam("labelChoosing") String lda){
		return buildResponseOnCommand(new PredictionCommands.GetPredictedCategory(ObjectLabelDecisionAlgorithms.get(lda)));
	}
	
	@Path("objects/cost/estimated")
	@GET
	public Response getEstimatedDataCost(@DefaultValue("ExpectedCost") @QueryParam("costAlgorithm") String lca){
		return buildResponseOnCommand(new PredictionCommands.GetDataCost(LabelProbabilityDistributionCostCalculators.get(lca)));
	}
	
	@Path("objects/quality/estimated/")
	@GET
	public Response getEstimatedDataQuality(@DefaultValue("ExpectedCost") @QueryParam("costAlgorithm") String lca){
		return buildResponseOnCommand(new PredictionCommands.GetDataQuality(LabelProbabilityDistributionCostCalculators.get(lca)));
	}

	@Path("objects/cost/evaluated/")
	@GET
	public Response getEvaluatedDataCost(@DefaultValue("MaxLikelihood") @QueryParam("labelChoosing") String lda){
		return buildResponseOnCommand(new EvaluationCommands.GetDataCost(new DataEvaluator(lda)));
	}
	
	@Path("objects/quality/evaluated/")
	@GET
	public Response getEvaluatedDataQuality(@DefaultValue("MaxLikelihood") @QueryParam("labelChoosing") String lda){
		return buildResponseOnCommand(new EvaluationCommands.GetDataQuality(new DataEvaluator(lda)));
	}

	@Path("objects/quality/summary/")
	@GET
	public Response getSummaryDataQuality(){
		return buildResponseOnCommand(new PredictionCommands.GetDataQualitySummary());
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

	@Path("workers/quality/summary/")
	@GET
	public Response getSummaryWorkersQuality(){
		return buildResponseOnCommand(new PredictionCommands.GetWorkersQualitySummary());
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
