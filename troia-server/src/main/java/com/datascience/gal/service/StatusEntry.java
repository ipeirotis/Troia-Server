package com.datascience.gal.service;

import com.datascience.core.storages.IJobStorage;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.joda.time.DateTime;

public class StatusEntry {

	private IJobStorage jobStorage;
	private ResponseBuilder responser;
	private DateTime initializationTimestamp;

	public StatusEntry(IJobStorage jobStorage, ResponseBuilder responser, DateTime initializationTimestamp){
		this.jobStorage = jobStorage;
		this.responser = responser;
		this.initializationTimestamp = initializationTimestamp;
	}

	@GET @Path("/")
	public Response status(){
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("status", "OK");
		content.put("deploy_time", initializationTimestamp.toString());
		content.put("job_storage", jobStorage.toString());
		content.put("job_storage_status", getJobStorageStatus());
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
}
