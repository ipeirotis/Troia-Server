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

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
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
import com.datascience.gal.BatchDawidSkene;
import com.datascience.gal.Category;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.IncrementalDawidSkene;
import com.datascience.gal.MisclassificationCost;
import com.datascience.utils.Utils;

/**
 * a simple web service wrapper for the get another label project
 * 
 * @author josh
 */
@Path("")
public class Service {
	@Context
	ServletContext context;

	private static Logger logger = Logger.getLogger(Service.class);
	private static final String signatureOfTheClass = "Service: ";
	private static DawidSkeneCache dscache = null;

	/**
	 * a simple method to see if the service is awake
	 * 
	 * @return a string with the current time
	 */
	@GET
	@Path("ping")
	@Produces(MediaType.APPLICATION_JSON)
	public Response test() {
		final String signature = signatureOfTheClass+"ping(): ";
		try {
			setup(context);
		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error(signature+"class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		}
		DateTime datetime = new DateTime();
		String cargo = "processing request at: " + datetime.toString();
		logger.info(signature+cargo);
		return Response.ok(JSONUtils.gson.toJson(cargo)).build();
	}

	/**
	 * a simple method to see if the service is awake and access to the DB has
	 * been reached
	 * 
	 * @return a string with the current time
	 */
	@GET
	@Path("pingDB")
	@Produces(MediaType.APPLICATION_JSON)
	public Response testDB() {
		final String nl = "<p> </p>";
		final String signature = signatureOfTheClass+"testDB(): ";
		DateTime datetime = new DateTime();
		String cargo = "processing request at: " + datetime.toString();
		StringBuffer cargoSB = new StringBuffer(cargo);

		final String id = "1234512345";
		Set<Category> categories = new HashSet<Category>();
		categories.add(new Category("mock"));
		DawidSkene ds = new BatchDawidSkene(id, categories);
		cargoSB.append(nl);
		try {
			setup(context);
		} catch (IOException e) {
			Response.ok("I/O Error happened," + e);
			logger.error(signature, e);
		} catch (ClassNotFoundException e) {
			Response.ok("Deploy/Build Error happened," + e);
			logger.error(signature, e);
		} catch (SQLException e) {
			Response.ok("DB Access Error happened," + e);
			logger.error(signature, e);
		}
		DawidSkene dsInserted = dscache.insertDawidSkene(ds);
		DawidSkene dsRetrieved = dscache.getDawidSkene(id);
		if (dsInserted != null && dsRetrieved != null) {
			String msg = "DawidSkene object with id=" + id
					+ " has been inserted to the DB...";
			logger.info(signature + msg);
			cargoSB.append(msg + nl);
			dscache.deleteDawidSkene(id);
			msg = "DawidSkene object with id=" + id
					+ " has been removed from the DB...";
			dsRetrieved = dscache.getDawidSkene(id);
			if (dsRetrieved == null)
				msg += "successfully";
			else
				msg += "unsuccessfully";
			logger.info(signature + msg);
			cargoSB.append(msg + nl);
		} else {
			String msg = "DawidSkene object with id=" + id
					+ " has NOT been inserted to the DB... Error";
			logger.info(signature + msg);
			cargoSB.append(msg + nl);
		}
		logger.info(signature+">>");
		return Response.ok(cargoSB.toString(), MediaType.TEXT_HTML).build();
	}

	/**
	 * resets the ds model
	 * 
	 * @return a simple success message
	 */
	@GET
	@Path("reset")
	@Produces(MediaType.APPLICATION_JSON)
	public Response reset(@QueryParam("id") String idString) {
		final String signature = signatureOfTheClass+"reset(id): ";
		try {
			setup(context);

			if (idString != null) {
				String id = idString;
				dscache.deleteDawidSkene(id);
			}

			String message = "nullified the ds object"
					+ (idString == null ? ", and deleted ds with id "
							+ idString : "");
			logger.info(signature+message);
			return Response.ok(message).build();
		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		}
		logger.error(signature+"problem resetting dscache");
		return Response.status(500).build();
	}

	@GET
	@Path("exists")
	@Produces(MediaType.APPLICATION_JSON)
	public Response exists(@QueryParam("id") String idstr) {
		final String signature = signatureOfTheClass+"exists(id): ";
		String id = "" + 0;
		if (null == idstr) {
			logger.info(signature+"no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			boolean contains = dscache.hasDawidSkene(id);
			String message = (contains ? "found ds object: "
					: "didnt find ds object: ") + id;
			logger.info(signature+message);

			return Response.ok(JSONUtils.toJson(contains)).build();
		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error(signature+"class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	/**
	 * Loads a json set of category objects
	 * 
	 * @return a simple success message
	 */
	@POST
	@Path("loadCategories")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadCategories(@FormParam("categories") String input,
			@FormParam("id") String idstr,
			@FormParam("incremental") String incremental) {
		final String signature = signatureOfTheClass+"load categories(.3.): ";
		Collection<Category> categories;
		if (null == input || input.length() < 3) {
			String message = "invalid input. requires json-ified collection of category objects";
			logger.error(signature+message);
			return Response.status(500).build();
		}
		String id = "" + 0;
		if (null == idstr) {
			logger.info(signature+"no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			categories = JSONUtils.gson.fromJson(input,
					JSONUtils.categorySetType);
			DawidSkene ds;

			if (null == incremental)
				ds = new BatchDawidSkene(id, categories);
			else
				ds = new IncrementalDawidSkene(id, categories);
			dscache.insertDawidSkene(ds);

			String message = "built a ds with " + categories.size()
					+ " categories" + JSONUtils.gson.toJson(categories);
			logger.info(signature+message);
			return Response.ok(message).build();

		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error(signature+"class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(signature+"some other exception: " + e.getMessage());
		}
		return Response.status(500).build();
	}

	/**
	 * Loads a json set of misclassification cost objects
	 * 
	 * @return a simple success message
	 */
	@POST
	@Path("loadCosts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadMisclassificationCosts(@FormParam("costs") String data,
			@FormParam("id") String idstr) {
		final String signature = signatureOfTheClass+"loadCosts(.2.): ";
		Collection<MisclassificationCost> costs;
		if (null == data || data.length() < 3) {
			String message = "invalid input. requires json-ified collection of misclassification cost objects";
			logger.error(signature+message);
			return Response.status(500).build();
		}
		String id = "" + 0;
		if (null == idstr) {
			logger.info(signature+"no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			costs = JSONUtils.gson.fromJson(data,
					JSONUtils.misclassificationCostSetType);
			DawidSkene ds = dscache.getDawidSkene(id);
			ds.addMisclassificationCosts(costs);
			dscache.insertDawidSkene(ds);
			String message = "adding " + costs.size()
					+ " new misclassification costs";
			logger.info(signature+message);
			return Response.ok(message).build();

		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error(signature+"class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(signature+e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	/**
	 * add a worker-assigned label to the model
	 * 
	 * @return a simple success message
	 */
	@POST
	@Path("loadWorkerAssignedLabel")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadWorkerAssignedLabel(@FormParam("data") String data,
			@FormParam("id") String idstr) {
		final String signature = signatureOfTheClass+"loadWorkerAssignedLabel(.2.): ";
		AssignedLabel label;

		String id = "" + 0;
		if (null == idstr) {
			logger.info(signature+"no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			label = JSONUtils.gson.fromJson(data, JSONUtils.assignedLabelType);

			DawidSkene ds = dscache.getDawidSkene(id);
			ds.addAssignedLabel(label);
			dscache.insertDawidSkene(ds);

			String message = "adding " + label.toString();
			logger.info(signature+message);
			return Response.ok(message).build();
		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error(signature+"class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(signature+e.getLocalizedMessage());
		}
		return Response.status(500).build();

	}

	/**
	 * add a worker-assigned label to the model
	 * 
	 * @return a simple success message
	 */
	@POST
	@Path("loadWorkerAssignedLabels")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadWorkerAssignedLabels(@FormParam("data") String data,
			@FormParam("id") String idstr) {
		final String signature = signatureOfTheClass+"loadWorkerAssignedLabels(.2.): ";
		Collection<AssignedLabel> input;

		String id = "" + 0;
		if (null == idstr) {
			logger.info(signature+"no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			input = JSONUtils.gson.fromJson(data,
					JSONUtils.assignedLabelSetType);
			DawidSkene ds = dscache.getDawidSkene(id);
			ds.addAssignedLabels(input);
			dscache.insertDawidSkene(ds);
			String message = "adding " + input.size() + " labels";
			logger.info(signature+message);
			return Response.ok(message).build();
		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error(signature+"class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(signature+e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	/**
	 * add a gold label to the model
	 * 
	 * @return a simple success message
	 */
	@POST
	@Path("loadGoldLabel")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadGoldLabel(@FormParam("data") String data,
			@FormParam("id") String idstr) {
		final String signature = signatureOfTheClass+"loadGoldLabel(.2.): ";
		CorrectLabel label;

		String id = "" + 0;
		if (null == idstr) {
			logger.info(signature+"no id input, using id 0");
		} else {
			id = idstr;
		}

		try {

			label = JSONUtils.gson.fromJson(data, JSONUtils.correctLabelType);
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);
			ds.addCorrectLabel(label);
			dscache.insertDawidSkene(ds);
			String message = "adding gold label: " + label.toString();
			logger.info(signature+message);
			return Response.ok(message).build();
		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error(signature+"class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {

			logger.error(signature+e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	/**
	 * add gold labels to the model
	 * 
	 * @return a simple success message
	 */
	@POST
	@Path("loadGoldLabels")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadGoldLabels(@FormParam("data") String data,
			@FormParam("id") String idstr) {
		final String signature = signatureOfTheClass+"loadGoldlabels(.2.): ";
		Collection<CorrectLabel> input;

		String id = "" + 0;
		if (null == idstr) {
			logger.info(signature+"no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);
			input = JSONUtils.gson
					.fromJson(data, JSONUtils.correctLabelSetType);
			ds.addCorrectLabels(input);
			dscache.insertDawidSkene(ds);
			String message = "adding " + input.size() + " gold labels";
			logger.info(signature+message);
			return Response.ok(message).build();

		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error(signature+"class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(signature+e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	/**
	 * computes majority votes for object
	 * 
	 * @return map of majority votes
	 */
	@GET
	@Path("majorityVote")
	@Produces(MediaType.APPLICATION_JSON)
	public Response majorityVote(@QueryParam("id") String idstr,
			@QueryParam("objectName") String objectName) {
		final String signature = signatureOfTheClass+"majorityVote(.2.): ";
		String id = "" + 0;
		if (null == idstr) {
			logger.info(signature+"no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);
			String votes = ds.getMajorityVote(objectName);

			if (votes == null) {
				logger.warn(signature+"got a null majority vote for object: "
						+ objectName);
				Response.status(500).build();
			}

			String message = "computing majority votes for " + objectName;
			logger.info(signature+message);
			return Response.ok(JSONUtils.gson.toJson(votes)).build();

		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error(signature+"class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {

			logger.error(signature+e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	/**
	 * computes majority votes for objects
	 * 
	 * @return map of majority votes
	 */
	@POST
	@Path("majorityVotes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response majorityVotes(@FormParam("id") String idstr,
			@FormParam("objects") String objects) {
		final String signature = signatureOfTheClass+"majorityVotes(.2.): ";
		Map<String, String> votes;

		String id = "" + 0;
		if (null == idstr) {
			logger.info(signature+"no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);
			if (null == objects)
				votes = ds.getMajorityVote();
			else {
				Collection<String> objectNames = JSONUtils.gson.fromJson(
						objects, JSONUtils.stringSetType);
				votes = ds.getMajorityVote(objectNames);
			}
			String message = "computing majority votes for "
					+ (null == votes ? 0 : votes.size()) + " objects ";
			logger.info(signature+message);
			return Response.ok(JSONUtils.gson.toJson(votes)).build();

		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error(signature+"class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {

			logger.error(signature+e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	/**
	 * computes majority votes for objects
	 * 
	 * @return map of majority votes
	 */
	@GET
	@Path("majorityVotes")
	@Produces(MediaType.APPLICATION_JSON)
	public Response majorityVotes(@QueryParam("id") String idstr) {
		final String signature = signatureOfTheClass+"majorityVotes(id): ";
		Map<String, String> votes;

		String id = "" + 0;
		if (null == idstr) {
			logger.info("no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);
			votes = ds.getMajorityVote();
			String message = "computing majority votes for "
					+ (null == votes ? 0 : votes.size()) + " objects ";
			logger.info(signature+message);
			return Response.ok(JSONUtils.gson.toJson(votes)).build();

		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error(signature+"class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {

			logger.error(signature+e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	/**
	 * computes class probs objects
	 * 
	 * @return map of majority votes
	 */
	@POST
	@Path("objectProbs")
	@Produces(MediaType.APPLICATION_JSON)
	public Response objectProbs(@FormParam("id") String idstr,
			@FormParam("objects") String objects) {
		final String signature = signatureOfTheClass+"objectProbs(.2.): ";
		Map<String, Map<String, Double>> votes;

		String id = "" + 0;
		if (null == idstr) {
			logger.info(signature+"no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);
			if (null == objects)
				votes = ds.getObjectProbs();
			else {
				Collection<String> objectNames = JSONUtils.gson.fromJson(
						objects, JSONUtils.stringSetType);
				votes = ds.getObjectProbs(objectNames);
			}
			String message = "computing object probs for "
					+ (null == votes ? 0 : votes.size()) + " objects ";
			logger.info(signature+message);
			return Response.ok(JSONUtils.gson.toJson(votes)).build();

		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error(signature+"class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {

			logger.error(signature+e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	/**
	 * computes class probs objects
	 * 
	 * @return map of majority votes
	 */
	@GET
	@Path("objectProb")
	@Produces(MediaType.APPLICATION_JSON)
	public Response objectProb(@QueryParam("id") String idstr,
			@QueryParam("object") String objectName) {
		final String signature = signatureOfTheClass+"objectProb(.2.): ";
		Map<String, Double> votes;

		String id = "" + 0;
		if (null == idstr) {
			logger.info(signature+"no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);
			votes = ds.getObjectProbs(objectName);
			String message = "computing object probs for " + objectName;
			logger.info(signature+message);
			return Response.ok(JSONUtils.gson.toJson(votes)).build();

		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error(signature+"class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {

			logger.error(signature+e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	/**
	 * TODO: make async w/ a callback call to run the iterative ds algorithm
	 * 
	 * @return success message
	 */
	@GET
	@Path("computeBlocking")
	@Produces(MediaType.APPLICATION_JSON)
	public Response computeDsBlocking(
			@QueryParam("iterations") String iterations,
			@QueryParam("id") String idstr) {
		final String signature = signatureOfTheClass+"computeBlocking(.2.): ";
		int its = 1;
		long time = 0;

		String id = 0 + "";
		if (null == idstr) {
			logger.info(signature+"no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);

			StopWatch sw = new StopWatch();
			sw.start();
			its = Math.max(1,
					null == iterations ? 1 : Integer.parseInt(iterations));

			String message = new String(
					"Wrong parameters, iterations number is should not be greater than "
							+ Utils.MAX_ITERATIONS);
			if (its > Utils.MAX_ITERATIONS) {
				return Response.ok(message).build();
			}
			ds.estimate(its);
			sw.stop();
			time = sw.getTime();
			message = "performed ds iteration " + its + " times, took: " + time
					+ "ms.";
			logger.info(signature+message);
			dscache.insertDawidSkene(ds);

			return Response.ok(message).build();

		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error(signature+"class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(signature+e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	@GET
	@Path("printWorkerSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public Response printWorkerSummary(@QueryParam("verbose") String verbose,
			@QueryParam("id") String idstr) {
		final String signature = signatureOfTheClass+"printWorkingSummary(.2.): ";
		String output;

		String id = "" + 0;
		if (null == idstr) {
			logger.info(signature+"no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);

			boolean verb = null == verbose ? false : true;
			output = ds.printAllWorkerScores(verb);

			String message = "returning request for worker summary";
			logger.info(signature+message);

			return Response.ok(output).build();

		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error(signature+"class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(signature+e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	@GET
	@Path("printObjectsProbs")
	@Produces(MediaType.APPLICATION_JSON)
	public Response printObjectsProbs(@QueryParam("entropy") String ent,
			@QueryParam("id") String idstr) {
		final String signature = signatureOfTheClass+"printObjectsProbs(.2.): ";
		String output;

		String id = "" + 0;
		if (null == idstr) {
			logger.info(signature+"no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);

			double entropy = null == ent ? 0. : Double.parseDouble(ent);
			output = ds.printObjectClassProbabilities(entropy);

			String message = "returning request for object class probs";
			logger.info(signature+message);
			return Response.ok(output).build();

		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error(signature+"class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(signature+e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	@GET
	@Path("objectProbs")
	@Produces(MediaType.APPLICATION_JSON)
	public Response objectProbs(@QueryParam("object") String object,
			@QueryParam("entropy") String ent, @QueryParam("id") String idstr) {
		final String signature = signatureOfTheClass+"objectProbs(.3.): ";
		Map<String, Double> out;

		String id = "" + 0;
		if (null == idstr) {
			logger.info(signature+"no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);

			double entropy = null == ent ? 0. : Double.parseDouble(ent);
			out = ds.objectClassProbabilities(object, entropy);

			String message = "returning request for object class probs";
			logger.info(signature+message);
			return Response.ok(JSONUtils.gson.toJson(out)).build();

		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error(signature+"class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(signature+e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	@GET
	@Path("printPriors")
	@Produces(MediaType.APPLICATION_JSON)
	public Response printPriors(@QueryParam("id") String idstr) {
		final String signature = signatureOfTheClass+"printPriors(id): ";
		String output;

		String id = "" + 0;
		if (null == idstr) {
			logger.info(signature+"no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);
			output = ds.printPriors();
			String message = "returning request for object class probs";
			logger.info(signature+message);

			return Response.ok(output).build();
		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error(signature+"class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(signature+e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	@GET
	@Path("classPriors")
	@Produces(MediaType.APPLICATION_JSON)
	public Response computePriors(@QueryParam("id") String idstr) {
		final String signature = signatureOfTheClass+"classPriors(id): ";
		Map<String, Double> out = new HashMap<String, Double>();

		String id = "" + 0;
		if (null == idstr) {
			logger.info(signature+"no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);
			out = ds.computePriors();
			String message = "returning request for object class probs";
			logger.info(signature+message);

			return Response.ok(JSONUtils.gson.toJson(out)).build();

		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error(signature+"class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(signature+e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	@GET
	@Path("getDawidSkene")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDawidSkene(@QueryParam("id") String idstr) {
		final String signature = signatureOfTheClass+"getDawidSkene(id): ";
		String id = "" + 0;
		if (null == idstr) {
			logger.info(signature+"no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);
			return Response.ok(JSONUtils.gson.toJson(ds)).build();
		} catch (IOException e) {
			logger.error(signature+"ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error(signature+"class not found exception: "
					+ e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error(signature+"sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(signature+e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	private void setup(ServletContext scontext) throws IOException,
			ClassNotFoundException, SQLException {
		final String signature = signatureOfTheClass+"setup(..): ";
		if (dscache == null) {
			logger.info(signature+"loading props file with context:"
					+ scontext.getContextPath());
			Properties props = new Properties();
			props.load(scontext
					.getResourceAsStream("/WEB-INF/classes/dawidskene.properties"));
			dscache = new DawidSkeneCache(props);
		}
	}

}
