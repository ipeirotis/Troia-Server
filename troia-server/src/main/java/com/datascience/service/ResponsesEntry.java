package com.datascience.service;

import java.util.NoSuchElementException;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.datascience.executor.CommandStatus;
import com.datascience.executor.CommandStatusesContainer;
import com.sun.jersey.spi.resource.Singleton;

@Path("/responses/")
@Singleton
public class ResponsesEntry {

	@Context ServletContext context;
	
	private ResponseBuilder getResponseBuilder(){
		return (ResponseBuilder) context.getAttribute(Constants.RESPONSER);
	}

	private CommandStatusesContainer getCommandStatusContainer(){
		return (CommandStatusesContainer) context.getAttribute(Constants.COMMAND_STATUSES_CONTAINER);
	}

	@GET
	@Path("/{id}/{res: .*}")
	public Response getResponse(@PathParam("id") String sid) throws Exception {
		CommandStatus status = getCommandStatusContainer().getCommandResult(sid);
		if (status == null) {
			throw new NoSuchElementException("No status with id: " + sid);
		}
		return getResponseBuilder().makeStatusResponse(status);
	}
}
