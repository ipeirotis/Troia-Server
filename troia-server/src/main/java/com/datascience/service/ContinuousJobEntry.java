package com.datascience.service;

import com.datascience.core.base.Data;
import com.datascience.galc.ContinuousProject;
import com.datascience.galc.commands.GALCommandBase;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

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
		GALCommandBase command = null; // TODO
		return buildResponseOnCommand(job, command);
	}

	@Path("goldObjects/{oid}")
	@GET
	public Response getGoldObject(@QueryParam("oid") String objectId){
		GALCommandBase command = null; // TODO
		return buildResponseOnCommand(job, command);
	}

	@Path("goldObjects/{oid}")
	@POST
	public Response addGoldObject(@QueryParam("oid") String objectId,
								  @FormParam("label") Double label){
		GALCommandBase command = null; // TODO
		return buildResponseOnCommand(job, command);
	}

	@Path("assigns")
	@GET
	public Response getAssigns(){
		GALCommandBase command = null; // TODO
		return buildResponseOnCommand(job, command);
	}

	@Path("assigns/")
	@POST
	public Response addAssign(@FormParam("label") Double label,
							  @FormParam("worker") String worker,
							  @FormParam("object") String object){
		GALCommandBase command = null; // TODO
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
