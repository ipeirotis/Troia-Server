/*******************************************************************************
 * Copyright (c) 2012 Panagiotis G. Ipeirotis & Josh M. Attenberg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.datascience.gal.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.datascience.gal.AssignedLabel;
import com.datascience.gal.Category;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.MisclassificationCost;
import com.datascience.gal.WorkerCostMethod;
import com.datascience.gal.dawidSkeneProcessors.DawidSkeneProcessorManager;

/**
 * a simple web service wrapper for the get another label project
 *
 * @author josh
 */
@Path("")
public class Service {
	@Context
	ServletContext context;

	public final static String SUCCESS = "Success";
	public final static String FAILURE = "Failure";

	private static String DEFAULT_JOB_ID = "0";
	private static Logger logger = Logger.getLogger(Service.class);

	private DawidSkeneProcessorManager getManager() {
		return (DawidSkeneProcessorManager) context.getAttribute("manager");
	}

	private static String getIdFromInput(String input, String def) {
		String id = def;
		if (null == input)  {
			logger.info("No id input: using default id=" + def);
		} else {
			id = input;
		}
		return id;
	}

	private static String getIdFromInput(String input) {
		return getIdFromInput(input, "0");
	}

	private static String getJobId(String jid) {
		if (jid == null || jid == "") {
			logger.info("No job ID or empty - using default");
			return DEFAULT_JOB_ID;
		}
		return jid;
	}

	private static <T> T parseJsonInput(String input, Type type) throws
		Exception {
		if (null == input || input.length() < 3) {
			throw new Exception("Invalid input: required JSON-ified " +
								"collection of objects of type " + type);
		}
		T result = JSONUtils.gson.fromJson(input, type);
		if (result == null) {
			throw new Exception("I could not parse JSON");
		}
		return result;
	}

	private static void logErrorFromException(Exception e) {
		logger.error("Error processing the request: " +
					 e.getClass().getName() + ": " + e.getLocalizedMessage());
		for (StackTraceElement ste : e.getStackTrace()) {
			logger.error(ste);
		}
	}

	private static void logRequestProcessing(String endpointName) {
		logger.info("Processing the request: " + endpointName);
	}

	private static void handleException(String endpointName, String message,
										Exception e) {
		message = endpointName + " with " + message;
		handleException(message, e);
	}

	private static void handleException(String message, Exception e) {
		logger.error(message);
		logger.error("EXCEPTION: " + e.getClass().getName());
		for (StackTraceElement ste : e.getStackTrace()) {
			logger.error(ste);
		}
	}

	private static Response buildResponse(String message, String status,
										  Object result, DateTime timestamp,
										  Map<String, ?> others) {
		Map<String, Object> cargo = new HashMap<String, Object>();
		if (null != message) {
			cargo.put("message", message);
		}
		if (null != status) {
			cargo.put("status", status);
		}
		if (null != result) {
			cargo.put("result", result);
		}
		if (null != timestamp) {
			cargo.put("timestamp", timestamp.toString());
		}
		if (null != others) {
			cargo.putAll(others);
		}
		if (null != message && null != status) {
			if (!status.equals(FAILURE)) {
				logger.info(message);
			} else {
				logger.warn(message);
			}
		}
		return Response.ok(JSONUtils.gson.toJson(cargo)).build();
	}

	public Service() {
		logger.debug("A new Service instance has been created");
	}

	/**
	 * a simple method to see if the service is awake
	 *
	 * @return a JSON containing a message and a timestamp
	 */
	@GET
	@Path("ping")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ping() {
		logRequestProcessing("ping");
		Response rs;
		try {
			String message = "Request successfully processed";
			rs = buildResponse(message, SUCCESS, null, new DateTime(), null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		}
		return rs;
	}

	/**
	 * a simple method to see if the service is awake and access to the DB has
	 * been reached
	 *
	 * @return a simple JSON status message
	 */
	@GET
	@Path("pingDB")
	@Produces(MediaType.APPLICATION_JSON)
	public Response pingDB() {
		String message = null ;
		Response rs;
		try {
			getManager().pingDatabase();
			message = " Object has been inserted to the DB and removed " +
					  "from the DB ";

		} catch(Exception e) {
			logErrorFromException(e);
			message = e.getMessage();
		} finally {
			rs = buildResponse(message, SUCCESS, null, new DateTime(),null);

		}
		return rs;

	}

	/**
	 * resets the ds model
	 *
	 * @return a simple JSON status message
	 */
	@GET
	@Path("reset")
	@Produces(MediaType.APPLICATION_JSON)
	public Response reset(@QueryParam("id") String idString) {
		logRequestProcessing("reset");
		Response rs;
		try {
			if (idString != null) {
				String id = idString;
				getManager().deleteProject(id);
			}
			String message = "Reset the ds model";
			if (null != idString) {
				message += " with id: " + idString;
			}
			rs = buildResponse(message, SUCCESS, null, new DateTime(), null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		}
		return rs;
	}

	/**
	 * simply checks wheter the ds model with specyfied id exists in the DB
	 *
	 * @return a simple JSON status message
	 */
	@GET
	@Path("exists")
	@Produces(MediaType.APPLICATION_JSON)
	public Response exists(@QueryParam("id") String idString) {
		String id = getIdFromInput(idString);
		Response rs;
		try {

			Boolean exists = getManager().containsProject(id);
			String message = "Request model with id: " + id + " " +
							 (exists ? "exists" : "does not exist");
			rs = buildResponse(message, null, exists, null, null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		}
		return rs;
	}

	/**
	 * loads a JSON set of category objects
	 *
	 * @return a simple success message
	 */
	@POST
	@Path("loadCategories")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadCategories(@FormParam("id") String idString,
								   @FormParam("categories") String categoriesString,
								   @FormParam("incremental") String incremental) {
		logRequestProcessing("loadCategories");
		String id = getIdFromInput(idString);
		Response rs;
		try {

			Collection<Category> categories = parseJsonInput(categoriesString,
											  JSONUtils.categorySetType);
			getManager().createProject(id, categories, incremental != null);
			String message = "Built a request model with " + categories.size()
							 + " categories";
			rs = buildResponse(message, SUCCESS, null, new DateTime(), null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		}
		return rs;
	}

	/**
	 * loads a JSON set of misclassification cost objects
	 *
	 * @return a simple success message
	 */
	@POST
	@Path("loadCosts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadMisclassificationCosts(
		@FormParam("id") String idString,
		@FormParam("costs") String costsString) {
		logRequestProcessing("loadCosts");
		String id = getIdFromInput(idString);
		Response rs;
		try {
			Collection<MisclassificationCost> costs = parseJsonInput(
						costsString,
						JSONUtils.misclassificationCostSetType);
			String message = "Loaded " + costs.size()
							 + " misclassification costs";
			getManager().addMisclassificationCost(id, costs);
			rs = buildResponse(message, SUCCESS, null, new DateTime(), null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		}
		return rs;
	}

	/**
	 * add a worker-assigned label to the model
	 *
	 * @return a simple success message
	 */
	@POST
	@Path("loadWorkerAssignedLabel")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadWorkerAssignedLabel(@FormParam("id") String idString,
											@FormParam("label") String labelString) {
		logRequestProcessing("loadWorkerAssignedLabel");
		String id = getIdFromInput(idString);
		Response rs;
		try {
			AssignedLabel label = parseJsonInput(labelString,
												 JSONUtils.assignedLabelType);
			Collection<AssignedLabel> labels = new ArrayList<AssignedLabel>();
			labels.add(label);
			getManager().addLabels(id, labels);
			String message = "Loaded label: " + label;
			rs = buildResponse(message, SUCCESS, null, new DateTime(), null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		}
		return rs;
	}

	/**
	 * add a worker-assigned label to the model
	 *
	 * @return a simple success message
	 */
	@POST
	@Path("loadWorkerAssignedLabels")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadWorkerAssignedLabels(@FormParam("id") String idString,
			@FormParam("labels") String labelsString) {
		logRequestProcessing("loadWorkerAssignedLabels");
		String id = getIdFromInput(idString);
		Response rs;
		try {
			Collection<AssignedLabel> labels = parseJsonInput(labelsString,
											   JSONUtils.assignedLabelSetType);
			getManager().addLabels(id, labels);
			String message = "Loaded " + labels.size() + " labels";
			rs = buildResponse(message, SUCCESS, null, new DateTime(), null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		}
		return rs;
	}

	/**
	 * loads a gold label for the model
	 *
	 * @return a simple success message
	 */
	@POST
	@Path("loadGoldLabel")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadGoldLabel(@FormParam("id") String idString,
								  @FormParam("label") String labelString) {
		logRequestProcessing("loadGoldLabel");
		String id = getIdFromInput(idString);
		Response rs;
		try {
			CorrectLabel label = parseJsonInput(labelString,
												JSONUtils.correctLabelType);
			Collection<CorrectLabel> labels = new ArrayList<CorrectLabel>();
			labels.add(label);
			getManager().addGoldLabels(id, labels);
			String message = "Loaded gold label: " + label;
			rs = buildResponse(message, SUCCESS, null, new DateTime(), null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		}
		return rs;
	}

	/**
	 * loads gold labels for the model
	 *
	 * @return a simple success message
	 */
	@POST
	@Path("loadGoldLabels")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadGoldLabels(@FormParam("id") String idString,
								   @FormParam("labels") String labelsString) {
		logRequestProcessing("loadGoldLabels");
		String id = getIdFromInput(idString);
		Response rs;
		try {
			Collection<CorrectLabel> labels = parseJsonInput(labelsString,
											  JSONUtils.correctLabelSetType);
			getManager().addGoldLabels(id, labels);
			String message = "Loaded " + labels.size() + " gold labels";
			rs = buildResponse(message, SUCCESS, null, new DateTime(), null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		}
		return rs;
	}

	/**
	 * loads gold labels for the model
	 *
	 * @return a simple success message
	 */
	@POST
	@Path("loadEvaluationData")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadEvaluationData(@FormParam("id") String idString,
									   @FormParam("labels") String labelsString) {
		logRequestProcessing("loadGoldLabels");
		String id = getIdFromInput(idString);
		Response rs;
		try {
			Collection<CorrectLabel> labels = parseJsonInput(labelsString,
											  JSONUtils.correctLabelSetType);
			getManager().addEvaluationData(id, labels);
			String message = "Loaded " + labels.size() + " gold labels";
			rs = buildResponse(message, SUCCESS, null, new DateTime(), null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		}
		return rs;
	}

	/**
	 * computes majority votes for the model
	 *
	 * @return a JSON containing a map of majority votes
	 */
	@GET
	@Path("majorityVote")
	@Produces(MediaType.APPLICATION_JSON)
	public Response majorityVote(@QueryParam("id") String idString,
								 @QueryParam("objectName") String objectName) {
		logRequestProcessing("majorityVote");
		String id = getIdFromInput(idString);
		Response rs;
		DawidSkeneProcessorManager manager = getManager();
		try {
			DawidSkene ds = manager.getDawidSkeneForReadOnly(id);
			String votes = ds.getMajorityVote(objectName);
			if (votes == null) {
				throw new Exception("Got a null majority vote for the object: "
									+ objectName);
			}
			String message = "Computing majority votes for the object: " +
							 objectName;
			rs = buildResponse(message, SUCCESS, null, new DateTime(), null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		} finally {
			manager.finalizeReading(id);
		}
		return rs;
	}

	/**
	 * computes majority votes for objects
	 *
	 * @return a JSON containing a map of majority votes
	 */
	@POST
	@Path("majorityVotes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response majorityVotes(@FormParam("id") String idString,
								  @FormParam("objectsNames") String objectsNamesJson) {
		logRequestProcessing("majorityVotes");
		String id = getIdFromInput(idString);
		Response rs;
		DawidSkeneProcessorManager manager = getManager();
		try {
			DawidSkene ds = manager.getDawidSkeneForReadOnly(id);
			Map<String, String> votes = null;
			if (null == objectsNamesJson)
				votes = ds.getMajorityVote();
			else {
				Collection<String> objectsNames = parseJsonInput(
													  objectsNamesJson,
													  JSONUtils.stringSetType);
				votes = ds.getMajorityVote(objectsNames);
			}
			String message = "Computing majority votes for " +
							 (null == votes ? 0 : votes.size()) + " objects";
			rs =  buildResponse(message, SUCCESS, votes, new DateTime(),
								null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		} finally {
			manager.finalizeReading(id);
		}
		return rs;
	}

	/**
	 * computes majority votes for objects
	 *
	 * @return a JSON containing a map of majority votes
	 */
	@GET
	@Path("majorityVotes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response majorityVotes(@QueryParam("id") String idString) {
		logRequestProcessing("majorityVotes");
		String id = getIdFromInput(idString);
		Response rs;
		DawidSkeneProcessorManager manager = getManager();
		try {
			DawidSkene ds = manager.getDawidSkeneForReadOnly(id);
			Map<String, String> votes = ds.getMajorityVote();
			String message = "Computing majority votes for " +
							 (null == votes ? 0 : votes.size()) + " objects";
			rs = buildResponse(message, SUCCESS, votes,
							   new DateTime(), null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		} finally {
			manager.finalizeReading(id);
		}
		return rs;
	}

	/**
	 * computes class probs objects
	 *
	 * @return map of majority votes // TODO for sure?
	 */
	@GET
	@Path("objectProb")
	@Produces(MediaType.APPLICATION_JSON)
	public Response objectProb(@QueryParam("id") String idString,
							   @QueryParam("objectName") String objectName) {
		logRequestProcessing("objectProb");
		String id = getIdFromInput(idString);
		Response rs;
		DawidSkeneProcessorManager manager = getManager();
		try {
			DawidSkene ds = manager.getDawidSkeneForReadOnly(id);
			Map<String, Double> probs = ds.getObjectProbs(objectName);
			String message = "Computing probs for the object: " + objectName;
			rs =  buildResponse(message, SUCCESS, probs, new DateTime(),
								null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		} finally {
			manager.finalizeReading(id);
		}
		return rs;
	}
	/**
	 * computes class probs objects
	 *
	 * @return map of majority votes // TODO for sure?
	 */
	@POST
	@Path("objectProbs")
	@Produces(MediaType.APPLICATION_JSON)
	public Response objectProbs(@FormParam("id") String idString,
								@FormParam("objectsNames") String objectsNamesJson) {
		String id = getIdFromInput(idString);
		Response rs;
		DawidSkeneProcessorManager manager = getManager();
		try {
			DawidSkene ds = manager.getDawidSkeneForReadOnly(id);
			Map<String, Map<String, Double>> probs = null;
			if (null == objectsNamesJson)
				probs = ds.getObjectProbs();
			else {
				Collection<String> objectNames = parseJsonInput(
													 objectsNamesJson,
													 JSONUtils.stringSetType);
				probs = ds.getObjectProbs(objectNames);
			}
			String message = "Computing probs for " +
							 (null == probs ? 0 : probs.size()) + " objects";
			rs = buildResponse(message, SUCCESS, probs, new DateTime(),
							   null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		} finally {
			manager.finalizeReading(id);
		}
		return rs;
	}

	/**
	 * TODO: make async w/ a callback call to run the iterative ds algorithm
	 *
	 * @return a JSON containing a success message
	 */
	@GET
	@Path("computeBlocking")
	@Produces(MediaType.APPLICATION_JSON)
	public Response computeDsBlocking(@QueryParam("id") String idString,
									  @QueryParam("iterations") String iterations) {
		Response rs;
		DawidSkeneProcessorManager manager = getManager();
		int its = Math.max(1,
						   null == iterations ? 1 : Integer.parseInt(iterations));
		String id = getIdFromInput(idString);
		try {
			DawidSkene ds = manager.getDawidSkeneForReadOnly(id);
			StopWatch sw = new StopWatch();
			sw.start();
			ds.estimate(its);
			sw.stop();
			long time = sw.getTime();
			String message = "Performed ds iteration " + its + " times, took: "
							 + time + "ms.";
			manager.updateDawidSkene(id, ds);
			rs = buildResponse(message, SUCCESS, null, new DateTime(), null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		} finally {
			manager.finalizeReading(id);
		}
		return rs;
	}

	@GET
	@Path("computeNotBlocking")
	@Produces( {MediaType.APPLICATION_JSON,MediaType.TEXT_HTML})
	public Response compute(@QueryParam("id") String idString,
							@QueryParam("iterations") String iterations) {
		logRequestProcessing("compute");
		Response rs;
		int its = Math.max(1,
						   null == iterations ? 1 : Integer.parseInt(iterations));
		String id = getIdFromInput(idString);
		try {
			getManager().computeDawidSkene(id, its);
			String message = "Registered DScomputer with  " +
							 its + " iterations";
			rs = buildResponse(message, SUCCESS, null, new DateTime(), null);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			rs = Response.status(500).build();
		}
		return rs;
	}

	@GET
	@Path("printWorkerSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public Response printWorkerSummary(@QueryParam("id") String idString,
									   @QueryParam("verbose") String verbose) {
		logRequestProcessing("printWorkerSummary");
		String id = getIdFromInput(idString);
		Response rs;
		DawidSkeneProcessorManager manager = getManager();
		try {
			DawidSkene ds = manager.getDawidSkeneForReadOnly(id);
			String message = "Worker summary";
			String result = ds.printAllWorkerScores(null != verbose);
			rs = buildResponse(message, SUCCESS, result, new DateTime(),
							   null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		} finally {
			manager.finalizeReading(id);
		}
		return rs;
	}

	@GET
	@Path("printObjectsProbs")
	@Produces(MediaType.APPLICATION_JSON)
	public Response printObjectsProbs(@QueryParam("id") String idString,
									  @QueryParam("entropy") String ent) {
		logRequestProcessing("printObjectsProbs");
		String id = getIdFromInput(idString);
		Response rs;
		DawidSkeneProcessorManager manager = getManager();
		try {
			DawidSkene ds = manager.getDawidSkeneForReadOnly(id);
			double entropy = null == ent ? 0. : Double.parseDouble(ent);
			String message = "Object class probabilities";
			String result = ds.printObjectClassProbabilities(entropy);
			rs = buildResponse(message, SUCCESS, result, new DateTime(),
							   null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		} finally {
			manager.finalizeReading(id);
		}
		return rs;
	}

	@GET
	@Path("objectProbs")
	@Produces(MediaType.APPLICATION_JSON)
	public Response objectProbs(@QueryParam("id") String idString,
								@QueryParam("objectName") String objectName,
								@QueryParam("entropy") String ent) {
		String id = getIdFromInput(idString);
		Response rs;
		DawidSkeneProcessorManager manager = getManager();
		try {
			DawidSkene ds = manager.getDawidSkeneForReadOnly(id);
			double entropy = null == ent ? 0. : Double.parseDouble(ent);
			logger.info("Processing request for object class probabilities");
			Map<String, Double> probs = ds.objectClassProbabilities(objectName,
										entropy);
			rs = buildResponse(null, null, probs, null, null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		} finally {
			manager.finalizeReading(id);
		}
		return rs;
	}

	@GET
	@Path("printPriors")
	@Produces(MediaType.APPLICATION_JSON)
	public Response printPriors(@QueryParam("id") String idString) {
		String id = getIdFromInput(idString);
		Response rs;
		DawidSkeneProcessorManager manager = getManager();
		try {
			DawidSkene ds = manager.getDawidSkeneForReadOnly(id);
			logger.info("Returning request for object class probabilities");
			String result = ds.printPriors();
			rs = buildResponse(null, null, result, null, null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		} finally {
			manager.finalizeReading(id);
		}
		return rs;
	}

	@GET
	@Path("isComputed")
	@Produces(MediaType.APPLICATION_JSON)
	public Response isComputed(@QueryParam("id") String idString) {
		logRequestProcessing("isComputed");
		String id = getIdFromInput(idString);
		Response rs;
		DawidSkeneProcessorManager manager = getManager();
		try {
			DawidSkene ds = manager.getDawidSkeneForReadOnly(id);
			boolean result = ds.isComputed();
			rs = buildResponse(null, null, result, null, null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		} finally {
			manager.finalizeReading(id);
		}
		return rs;
	}

	@GET
	@Path("classPriors")
	@Produces(MediaType.APPLICATION_JSON)
	public Response computePriors(@QueryParam("id") String idString) {
		String id = getIdFromInput(idString);
		Response rs;
		DawidSkeneProcessorManager manager = getManager();
		try {

			DawidSkene ds = manager.getDawidSkeneForReadOnly(id);
			logger.info("Returning request for object class probabilities");
			Map<String, Double> priors = ds.computePriors();
			rs = buildResponse(null, null, priors, null, null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		} finally {
			manager.finalizeReading(id);
		}
		return rs;
	}

	@GET
	@Path("getDawidSkene")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDawidSkene(@QueryParam("id") String idString) {
		String id = getIdFromInput(idString);
		Response rs;
		DawidSkeneProcessorManager manager = getManager();
		try {
			while(manager.getProcessorCountForProject(id)>0) {
				Thread.sleep(1);
			}
			logger.debug("Service retrives DS with id: " + id);
			DawidSkene ds = manager.getDawidSkeneForReadOnly(id);
			rs = buildResponse(null, null, ds, null, null);
		} catch (Exception e) {
			logErrorFromException(e);
			rs = Response.status(500).build();
		} finally {
			manager.finalizeReading(id);
		}
		return rs;
	}

	@GET
	@Path("calculateEstimatedCost")
	@Produces(MediaType.APPLICATION_JSON)
	public Response calculateEstimatedCost(@QueryParam("id") String jid,
										   @QueryParam("method") String method) {
		String id = getJobId(jid);
		String message = "calculateEstimatedCost(" + method + ") for job " + id;
		getManager().calculateEvaluationCost(id,method);
		return Response.status(500).build();
	}

	@GET
	@Path("getEstimatedCost")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEstimatedCost(@QueryParam("id") String jid,
									 @QueryParam("object") String object,
									 @QueryParam("category") String category) {
		DawidSkeneProcessorManager manager = getManager();
		String id = getJobId(jid);
		Response rs;
		String message = "getEstimatedCost for job " + id;
		DawidSkene ds = manager.getDawidSkeneForReadOnly(id);
		rs = buildResponse(null, null,ds.getQuality(object, category), null, null);
		manager.finalizeReading(id);
		return rs;

	}

	@GET
	@Path("getWorkerCost")
	@Produces(MediaType.APPLICATION_JSON)
	public Response calculateEstimatedCost(@QueryParam("id") String jid,@QueryParam("method") String method,
										   @QueryParam("worker") String worker) {
		DawidSkeneProcessorManager manager = getManager();
		String id = getJobId(jid);
		Response rs;
		WorkerCostMethod methodEnum;
		if("CostAdjusted".equals(method)) {
			methodEnum = WorkerCostMethod.COST_ADJUSTED;
		} else if("CostAdjustedMinimized".equals(method)) {
			methodEnum = WorkerCostMethod.COST_ADJUSTED_MINIMIZED;
		} else {
			methodEnum = WorkerCostMethod.COST_ADJUSTED_MINIMIZED;
		}
		DawidSkene ds = manager.getDawidSkeneForReadOnly(id);
		rs = buildResponse(null, null,ds.getWorkerCost(ds.getWorker(worker),methodEnum), null, null);
		manager.finalizeReading(id);
		return rs;
	}
}
