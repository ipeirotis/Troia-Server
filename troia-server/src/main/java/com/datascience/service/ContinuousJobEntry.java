package com.datascience.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.datascience.core.base.ContValue;
import com.datascience.core.storages.DataJSON.ShallowAssignCollection;
import com.datascience.core.storages.DataJSON.ShallowGoldObjectCollection;
import com.datascience.core.storages.DataJSON.ShallowObjectCollection;
import com.datascience.galc.ContinuousProject;
import com.datascience.galc.commands.AssignsCommands;
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
		return buildResponseOnCommand(new ProjectCommands.GetProjectInfo());
	}

	@Path("objects")
	@GET
	public Response getObjects(){
		return buildResponseOnCommand(new ObjectCommands.GetObjects());
	}

	@Path("objects/{oid}")
	@GET
	public Response getObject(@PathParam("oid") String objectId){
		return buildResponseOnCommand(new ObjectCommands.GetObject(objectId));
	}
	
	@Path("objects/{oid}/assigns")
	@GET
	public Response getObjectAssigns(@PathParam("oid") String objectId){
		return buildResponseOnCommand(new ObjectCommands.GetObjectAssigns(objectId));
	}

	@Consumes(MediaType.APPLICATION_JSON)
	@Path("objects")
	@POST
	public Response addObjects(ShallowObjectCollection objects){
		return buildResponseOnCommand(new ObjectCommands.AddObjects(objects));
	}


	@Path("goldObjects")
	@GET
	public Response getGoldObjects(){
		return buildResponseOnCommand(new GoldObjectsCommands.GetGoldObjects());
	}

	@Path("goldObjects/{oid}")
	@GET
	public Response getGoldObject(@PathParam("oid") String objectId){
		return buildResponseOnCommand(new GoldObjectsCommands.GetGoldObject(objectId));
	}
	
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("goldObjects")
	@POST
	public Response addGoldObjects(ShallowGoldObjectCollection<ContValue> goldObjects){
		return buildResponseOnCommand(new GoldObjectsCommands.AddGoldObjects(goldObjects));
	}

	@Path("assigns")
	@GET
	public Response getAssigns(){
		return buildResponseOnCommand(new AssignsCommands.GetAssigns());
	}

	@Consumes(MediaType.APPLICATION_JSON)
	@Path("assigns")
	@POST
	public Response addAssigns(ShallowAssignCollection<ContValue> assigns){
		return buildResponseOnCommand(new AssignsCommands.AddAssigns(assigns));
	}

	@Path("workers")
	@GET
	public Response getWorkers(){
		return buildResponseOnCommand(new WorkerCommands.GetWorkers());
	}

	@Path("workers/{wid}")
	@GET
	public Response getWorker(@PathParam("wid") String worker){
		return buildResponseOnCommand(new WorkerCommands.GetWorker(worker));
	}

	@Path("workers/{wid}/assigns")
	@GET
	public Response getWorkerAssigns(@PathParam("wid") String worker){
		return buildResponseOnCommand(new AssignsCommands.GetWorkerAssigns(worker));
	}

	@Path("compute/")
	@POST
	public Response compute(@DefaultValue("10") @FormParam("iterations") int iterations){
		return buildResponseOnCommand(new ProjectCommands.Compute(iterations, 1e-6));
	}

	@Path("prediction/objects/")
	@GET
	public Response getObjectsPrediction(){
		return buildResponseOnCommand(new ProjectCommands.ObjectsPrediction());
	}

	@Path("prediction/workers/")
	@GET
	public Response getWorkerQuality(){
		return buildResponseOnCommand(new ProjectCommands.WorkersPrediction());
	}
}
