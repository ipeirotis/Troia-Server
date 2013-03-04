package com.datascience.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;

import com.datascience.core.storages.IJobStorage;
import com.sun.jersey.spi.resource.Singleton;

@Path("/status/")
@Singleton
public class StatusEntry {

	@Context ServletContext context;
	
	private IJobStorage jobStorage;
	private ResponseBuilder responser;
	private DateTime initializationTimestamp;

	@PostConstruct
	public void postConstruct(){
		jobStorage = (IJobStorage) context.getAttribute(Constants.JOBS_STORAGE);
		responser = (ResponseBuilder) context.getAttribute(Constants.RESPONSER);
		initializationTimestamp = (DateTime) context.getAttribute(Constants.DEPLOY_TIME);
	}
	
	@GET @Path("/")
	public Response status(){
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("status", "OK");
		content.put("deploy_time", initializationTimestamp.toString());
		content.put("job_storage", jobStorage.toString());
		content.put("job_storage_status", getJobStorageStatus());
		content.put("memory", getMemoryStats());
		return responser.makeOKResponse(content);
	}

	protected String getJobStorageStatus() {
		try {
			jobStorage.test();
			return "OK";
		} catch (Exception e) {
			return "FAIL: " + e.getMessage();
		}
	}

	protected Map<String, Object> getMemoryStats(){
		Map<String, Object> memory = new HashMap<String, Object>();
		Runtime runtime = Runtime.getRuntime();
		memory.put("free", runtime.freeMemory());
		memory.put("total", runtime.totalMemory());
		memory.put("max", runtime.maxMemory());
		memory.put("used", runtime.totalMemory() - runtime.freeMemory());
		return memory;
	}
}
