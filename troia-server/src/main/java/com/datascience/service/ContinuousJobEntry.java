package com.datascience.service;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Label;
import com.datascience.core.base.Worker;
import com.datascience.galc.ContinuousProject;
import com.datascience.galc.commands.AssignsCommands;
import com.datascience.galc.commands.GALCommandBase;
import com.datascience.galc.commands.GoldObjectsCommands;

/**
 * @Author: konrad
 */
@Path("/cjobs/{id}/")
public class ContinuousJobEntry extends JobEntryBase<ContinuousProject> {

	@Path("")
	@GET
	public Response getJobInfo(){
		GALCommandBase command = null; // TODO
		return buildResponseOnCommand(job, command);
	}

	@Path("objects")
	@GET
	public Response getObjects(){
		GALCommandBase command = null; // TODO
		return buildResponseOnCommand(job, command);
	}

	@Path("objects/{oid}")
	@GET
	public Response getObject(@QueryParam("oid") String objectId){
		GALCommandBase command = null; // TODO
		return buildResponseOnCommand(job, command);
	}

	@Path("objects/{oid}")
	@POST
	public Response addObject(@QueryParam("oid") String objectId){
		GALCommandBase command = null; // TODO
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
	public Response getGoldObject(@QueryParam("oid") String objectId){
		GALCommandBase command = new GoldObjectsCommands.GetGoldObject(job.getProject(), objectId);
		return buildResponseOnCommand(job, command);
	}

	@Path("goldObjects/{oid}")
	@POST
	public Response addGoldObject(@QueryParam("oid") String objectId,
								  @FormParam("label") Double label,
								  @FormParam("zeta") Double zeta){
		LObject<ContValue> lobject = new LObject<ContValue>(objectId);
		lobject.setGoldLabel(new Label<ContValue>(new ContValue(label, zeta)));
		GALCommandBase command = new GoldObjectsCommands.AddGoldObjects(job.getProject(), lobject);
		return buildResponseOnCommand(job, command);
	}

	@Path("assigns")
	@GET
	public Response getAssigns(){
		GALCommandBase command = new AssignsCommands.GetAssigns(job.getProject());
		return buildResponseOnCommand(job, command);
	}

	@Path("assigns/")
	@POST
	public Response addAssign(@FormParam("label") Double label,
							  @FormParam("worker") String worker,
							  @FormParam("object") String object){
		AssignedLabel<ContValue> al = new AssignedLabel<ContValue>(worker, object, new ContValue(label));
		GALCommandBase command = new AssignsCommands.AddAssigns(job.getProject(), al);
		return buildResponseOnCommand(job, command);
	}

	@Path("workers")
	@GET
	public Response getWorkers(){
		GALCommandBase command = null; // TODO
		return buildResponseOnCommand(job, command);
	}

	@Path("workers/{wid}")
	@GET
	public Response getWorker(@PathParam("wid") String worker){
		GALCommandBase command = null; // TODO
		return buildResponseOnCommand(job, command);
	}

	@Path("workers/{wid}/assigns")
	@GET
	public Response getWorkerAssigns(@PathParam("wid") String worker){
		GALCommandBase command = null; // TODO
		return buildResponseOnCommand(job, command);
	}

	@Path("compute/")
	@POST
	public Response compute(@DefaultValue("10") @FormParam("iterations") int iterations){
		GALCommandBase command = null; // TODO
		return buildResponseOnCommand(job, command);
	}

	@Path("prediction/objects/")
	@GET
	public Response getObjectsPrediction(){
		GALCommandBase command = null; // TODO
		return buildResponseOnCommand(job, command);
	}

	@Path("prediction/workers/")
	@GET
	public Response getWorkerQuality(){
		GALCommandBase command = null; // TODO
		return buildResponseOnCommand(job, command);
	}

}
