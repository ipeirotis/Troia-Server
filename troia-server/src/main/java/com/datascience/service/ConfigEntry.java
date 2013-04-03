package com.datascience.service;

import com.datascience.core.JobsManager;
import com.datascience.core.storages.IJobStorage;
import com.datascience.executor.CommandStatusesContainer;
import com.datascience.executor.ProjectCommandExecutor;
import com.datascience.serialization.ISerializer;
import com.sun.jersey.api.view.Viewable;
import com.sun.jersey.spi.resource.Singleton;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.logging.Logger;

@Path("/config/")
@Singleton
public class ConfigEntry {

	@Context ServletContext scontext;

	public static class NameValue{
		public String name;
		public Object value;

		public NameValue(String n, Object v){
			this.name = n;
			this.value = v;
		}

		public String getName(){
			return name;
		}

		public Object getValue(){
			return value;
		}
	}

	@GET
	@Produces("text/html")
	public Response getConfig() {
		Map<String, Object> model = new HashMap<String, Object>();
		Properties properties = (Properties)scontext.getAttribute("properties");
		List<NameValue> items = new ArrayList<NameValue>();
		for (String s : properties.stringPropertyNames()){
			NameValue nv = new NameValue(s, properties.get(s));
			Logger.getAnonymousLogger().info(nv.toString());
			items.add(nv);
		}
		model.put("items", items);
		return Response.ok(new Viewable("/config", model)).build();
	}

	@POST
	@Produces("text/html")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response setConfig(MultivaluedMap<String, String> form){
		StringBuilder sb = new StringBuilder();
		for (String s : form.keySet()){
			sb.append(s + " " + form.getFirst(s) + "\n");
		}

		initializeContext(form);

		return Response.ok(sb.toString()).build();
	}

	public void initializeContext(MultivaluedMap<String, String> properties){
		try {
			Properties props = (Properties) scontext.getAttribute("properties");
			props.putAll(properties);

			scontext.setAttribute(Constants.DOWNLOADS_PATH, props.getProperty(Constants.DOWNLOADS_PATH));
			ServiceComponentsFactory factory = new ServiceComponentsFactory(props);

			ISerializer serializer = factory.loadSerializer();
			scontext.setAttribute(Constants.SERIALIZER, serializer);

			ResponseBuilder responser = factory.loadResponser(serializer);
			scontext.setAttribute(Constants.RESPONSER, responser);

			ProjectCommandExecutor executor = factory.loadProjectCommandExecutor();
			scontext.setAttribute(Constants.COMMAND_EXECUTOR, executor);

			JobsManager jobsManager = factory.loadJobsManager();
			scontext.setAttribute(Constants.JOBS_MANAGER, jobsManager);

			IJobStorage jobStorage = factory.loadJobStorage(serializer, executor, jobsManager);
			scontext.setAttribute(Constants.JOBS_STORAGE, jobStorage);

			CommandStatusesContainer statusesContainer = factory.loadCommandStatusesContainer(serializer);
			scontext.setAttribute(Constants.COMMAND_STATUSES_CONTAINER, statusesContainer);

			scontext.setAttribute(Constants.ID_GENERATOR, factory.loadIdGenerator());
		} catch (Exception e) {
			Logger.getAnonymousLogger().warning("In context initialization support");
		}
	}
}
