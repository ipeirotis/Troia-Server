package com.datascience.gal.service;

import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

/**
 * @author Konrad Kurdej
 */
@Path("")
public class MainEntry {
	
	@Context
	ServletContext context;
	
	@Path("/jobs/")
	public JobsEntry getJob(){
		return (JobsEntry) context.getAttribute(Constants.JOBS_ENTRY);
	}
	
	@Path("/status/")
	public StatusEntry getStatus(){
		return (StatusEntry) context.getAttribute(Constants.STATUS_ENTRY);
	}
}
