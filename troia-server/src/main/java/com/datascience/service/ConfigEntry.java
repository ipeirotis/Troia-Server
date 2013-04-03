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
		Properties properties = (Properties)scontext.getAttribute(Constants.PROPERTIES);
		List<NameValue> items = new ArrayList<NameValue>();
		for (String s : new ArrayList<String>(new TreeSet<String>(properties.stringPropertyNames()))){
			items.add(new NameValue(s, properties.get(s)));
		}
		model.put("items", items);
		return Response.ok(new Viewable("/config", model)).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response setConfig(MultivaluedMap<String, String> form){
		Map<String, String> simpleForm = new HashMap<String, String>();
		for (String s : form.keySet()){
			simpleForm.put(s, form.getFirst(s));
		}
		InitializationSupport.destroyContext(scontext);
		initializeContext(simpleForm);
		return Response.ok().build();
	}

	private void initializeContext(Map<String, String> properties){
		try {
			Properties props = (Properties) scontext.getAttribute(Constants.PROPERTIES);
			props.putAll(properties);

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
			Logger.getAnonymousLogger().warning("In context initialization support: " + e.getLocalizedMessage());
		}
	}
}
