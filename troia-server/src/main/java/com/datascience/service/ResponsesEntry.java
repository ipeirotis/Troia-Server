package com.datascience.service;

import java.util.NoSuchElementException;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.datascience.gal.commands.CommandStatus;
import com.datascience.gal.commands.CommandStatusesContainer;
import com.sun.jersey.spi.resource.Singleton;

@Path("/responses/")
@Singleton
public class ResponsesEntry {

	@Context ServletContext context;
	
	private ResponseBuilder responser;
	private CommandStatusesContainer statusesContainer;

	@PostConstruct
	public void postConstruct(){
		responser = (ResponseBuilder) context.getAttribute(Constants.RESPONSER);
		statusesContainer = (CommandStatusesContainer) context.getAttribute(Constants.COMMAND_STATUSES_CONTAINER);
	}

	@GET
	@Path("/{id}/{res: .*}")
	public Response getResponse(@PathParam("id") String sid) throws Exception {
		CommandStatus status = statusesContainer.getCommandResult(sid);
		if (status == null) {
			throw new NoSuchElementException("No status with id: " + sid);
		}
		switch(status.getStatus()){
			case OK:
				return responser.makeOKResponse(status.getData());
			case ERROR:
				return responser.makeExceptionResponse(status.getError());
			case NOT_READY:
				return responser.makeNotReadyResponse();
			default:
				return null;
		}
	}
}
