package com.datascience.service;

import java.util.Collection;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.storages.JSONUtils;
import com.datascience.core.storages.DataJSON.ShallowAssign;
import com.datascience.core.storages.DataJSON.ShallowGoldObject;
import com.datascience.galc.ContinuousProject;
import com.datascience.galc.commands.AssignsCommands;
import com.datascience.galc.commands.GALCommandBase;
import com.datascience.galc.commands.GoldObjectsCommands;
import com.datascience.galc.commands.ObjectCommands;
import com.datascience.galc.commands.ProjectCommands;
import com.datascience.galc.commands.WorkerCommands;

/**
 * @Author: konrad
 */
@Path("/cjobs/{id}/")
public class ContinuousJobEntry extends JobEntryBase<ContinuousProject> {

	public ContinuousJobEntry(){
		expectedClass = ContinuousProject.class;
	}

	@Path("")
	@GET
	public Response getJobInfo(){
		GALCommandBase command = new ProjectCommands.GetProjectInfo(job.getProject());
		return buildResponseOnCommand(job, command);
	}

	@Path("objects")
	@GET
	public Response getObjects(){
		GALCommandBase command = new ObjectCommands.GetObjects(job.getProject());
		return buildResponseOnCommand(job, command);
	}

	@Path("objects/{oid}")
	@GET
	public Response getObject(@PathParam("oid") String objectId){
		GALCommandBase command = new ObjectCommands.GetObject(job.getProject(), objectId);
		return buildResponseOnCommand(job, command);
	}
	
	@Path("objects/{oid}/assigns")
	@GET
	public Response getObjectAssigns(@PathParam("oid") String objectId){
		GALCommandBase command = new ObjectCommands.GetObjectAssigns(job.getProject(), objectId);
		return buildResponseOnCommand(job, command);
	}

	@Path("object")
	@POST
	public Response addObject(@FormParam("objectId") String objectId){
		GALCommandBase command = new ObjectCommands.AddObject(job.getProject(), objectId);
		return buildResponseOnCommand(job, command);
	}
	
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("objects")
	@POST
	public Response addObjects(String json){
		Collection<String> objects = serializer.parse(json, JSONUtils.stringSetType);
		GALCommandBase command = new ObjectCommands.AddObjects(job.getProject(), objects);
		return buildResponseOnCommand(job, command);
	}


	@Path("goldObjects")
	@GET
	public Response getGoldObjects(){
		GALCommandBase command = new GoldObjectsCommands.GetGoldObjects(job.getProject());
		return buildResponseOnCommand(job, command);
	}

	@Path("goldObjects/{oid}")
	@GET
	public Response getGoldObject(@PathParam("oid") String objectId){
		GALCommandBase command = new GoldObjectsCommands.GetGoldObject(job.getProject(), objectId);
		return buildResponseOnCommand(job, command);
	}

	@Path("goldObject")
	@POST
	public Response addGoldObject(@FormParam("objectId") String objectId,
								  @FormParam("label") Double label,
								  @FormParam("zeta") Double zeta){
		GALCommandBase command = new GoldObjectsCommands.AddGoldObject(job.getProject(), objectId, new ContValue(label, zeta));
		return buildResponseOnCommand(job, command);
	}
	
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("goldObjects")
	@POST
	public Response addGoldObjects(String json){
		Collection<ShallowGoldObject<ContValue>> goldObjects = serializer.parse(json, JSONUtils.goldObjectsCollectionType);
		GALCommandBase command = new GoldObjectsCommands.AddGoldObjects(job.getProject(), goldObjects);
		return buildResponseOnCommand(job, command);
	}

	@Path("assigns")
	@GET
	public Response getAssigns(){
		GALCommandBase command = new AssignsCommands.GetAssigns(job.getProject());
		return buildResponseOnCommand(job, command);
	}

	@Path("assign")
	@POST
	public Response addAssign(@FormParam("label") Double label,
							  @FormParam("worker") String worker,
							  @FormParam("object") String object){
		GALCommandBase command = new AssignsCommands.AddAssign(job.getProject(), worker, object, new ContValue(label));
		return buildResponseOnCommand(job, command);
	}
	
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("assigns/")
	@POST
	public Response addAssigns(String json){
		Collection<ShallowAssign<ContValue>> assigns = serializer.parse(json, JSONUtils.assignedLabelCollectionType);
		GALCommandBase command = new AssignsCommands.AddAssigns(job.getProject(), assigns);
		return buildResponseOnCommand(job, command);
	}

	@Path("workers")
	@GET
	public Response getWorkers(){
		GALCommandBase command = new WorkerCommands.GetWorkers(job.getProject());
		return buildResponseOnCommand(job, command);
	}

	@Path("workers/{wid}")
	@GET
	public Response getWorker(@PathParam("wid") String worker){
		GALCommandBase command = new WorkerCommands.GetWorker(job.getProject(), worker);
		return buildResponseOnCommand(job, command);
	}

	@Path("workers/{wid}/assigns")
	@GET
	public Response getWorkerAssigns(@PathParam("wid") String worker){
		GALCommandBase command = new AssignsCommands.GetWorkerAssigns(job.getProject(), worker);
		return buildResponseOnCommand(job, command);
	}

	@Path("compute/")
	@POST
	public Response compute(@DefaultValue("10") @FormParam("iterations") int iterations){
		GALCommandBase command = new ProjectCommands.Compute(job.getProject(), iterations, 1e-6);
		return buildResponseOnCommand(job, command);
	}

	@Path("prediction/objects/")
	@GET
	public Response getObjectsPrediction(){
		GALCommandBase command = new ProjectCommands.ObjectsPrediction(job.getProject());
		return buildResponseOnCommand(job, command);
	}

	@Path("prediction/workers/")
	@GET
	public Response getWorkerQuality(){
		GALCommandBase command = new ProjectCommands.WorkersPrediction(job.getProject());
		return buildResponseOnCommand(job, command);
	}

}
