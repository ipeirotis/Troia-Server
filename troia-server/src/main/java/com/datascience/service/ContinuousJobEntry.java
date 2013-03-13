package com.datascience.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.datascience.serialization.json.JSONUtils;
import com.datascience.executor.JobCommand;
import com.datascience.galc.ContinuousProject;
import com.datascience.galc.commands.PredictionCommands;

/**
 * @Author: konrad
 */
@Path("/cjobs/{id}/")
public class ContinuousJobEntry extends JobEntryBase<ContinuousProject> {

	public ContinuousJobEntry(){
		expectedClass = ContinuousProject.class;
		objectsType = JSONUtils.objectsContValueType;
		assignsType = JSONUtils.assignsContValueType;
	}

	@Path("objects/{oid:[a-zA-Z_0-9/:.-]+}/prediction")
	@GET
	public Response getObjectPrediction(@PathParam("oid") String objectId){
		return buildResponseOnCommand(new PredictionCommands.ObjectPrediction(objectId));
	}

	@Path("objects/prediction/")
	@GET
	public Response getObjectsPrediction(){
		return buildResponseOnCommand(new PredictionCommands.ObjectsPrediction());
	}

	@Path("workers/{wid:[a-zA-Z_0-9/:.-]+}/quality/estimated")
	@GET
	public Response getWorkerQuality(@PathParam("wid") String worker){
		return buildResponseOnCommand(new PredictionCommands.WorkerPrediction(worker));
	}

	@Path("workers/quality/estimated")
	@GET
	public Response getWorkersQuality(){
		return buildResponseOnCommand(new PredictionCommands.WorkersPrediction());
	}

	@Override
	protected JobCommand getPredictionZipCommand(String path){
		return new PredictionCommands.GetPredictionZip(path);
	}
}
