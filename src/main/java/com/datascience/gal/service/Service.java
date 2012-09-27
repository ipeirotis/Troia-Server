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
import com.datascience.gal.dawidSkeneProcessors.*;

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

	private static DawidSkeneCache dscache = null;

	private static DawidSkeneProcessorManager manager = null;

	/**
	 * A simple method to see if the service is awake
	 *
	 * @return A string with the current time
	 */
	@GET
	@Path("ping")
	@Produces(MediaType.APPLICATION_JSON)
	public Response test() {
		try {
			setup(context);
		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
		}
        String message = "Request successfully processed";
        logger.info(message);
        Map<String, String> cargo = new HashMap<String, String>();
        cargo.put("message", message);
        cargo.put("timastamp", new DateTime().toString());
        cargo.put("status", "Success");
		return Response.ok(JSONUtils.gson.toJson(cargo)).build();
	}

	/**
	 * a simple method to see if the service is awake and access to the DB has been reached
	 *
	 * @return a string with the current time
	 */
	@GET
	@Path("pingDB")
	@Produces(MediaType.APPLICATION_JSON)
	public Response testDB() {
		final String id = "1234512345";
		final String methodSign = "com.datascience.gal.service.Service:testDB ";
		Set<Category> categories = new HashSet<Category>();
		categories.add(new Category("mock"));
		DawidSkene ds = new BatchDawidSkene(id, categories);
        String message = ""; 
        String status = "";
        String timestamp = new DateTime().toString();
		try {
			setup(context);
		} catch (IOException e) {
			Response.ok("I/O Error happened,"+e);
			logger.error(methodSign, e);
		} catch (ClassNotFoundException e) {
			Response.ok("Deploy/Build Error happened,"+e);
			logger.error(methodSign, e);
		} catch (SQLException e) {
			Response.ok("DB Access Error happened,"+e);
			logger.error(methodSign, e);
		}
		DawidSkene dsInserted = dscache.insertDawidSkene(ds);
		DawidSkene dsRetrieved = dscache.getDawidSkene(id);
		if (dsInserted != null && dsRetrieved != null) {
			dscache.deleteDawidSkene(id);
            message += " Object has been inserted to the DB and removed from the DB ";
			dsRetrieved = dscache.getDawidSkene(id);
			if (dsRetrieved != null) {
				message += "successfully";
                status = "Success";
            } else {
                message += "unsuccessfully";
                status = "Failure";
            }
			logger.info(methodSign + message);
			
		} else {
		    message += " Object has NOT been inserted to the DB";
            status = "Failure";
			logger.info(methodSign + message);
		}
         
        Map<String, String> cargo = new HashMap<String, String>();
        cargo.put("message", message);
        cargo.put("status", status);
        cargo.put("object", id);
        cargo.put("timestamp", timestamp);
		return Response.ok(JSONUtils.gson.toJson(cargo)).build();
	}
	/**
	 * Resets the ds model.
	 *
	 * @return a simple JSON status message.
	 */
	@GET
	@Path("reset")
	@Produces(MediaType.APPLICATION_JSON)
	public Response reset(@QueryParam("id") String idString) {
        String message = "";
        Map<String, String> cargo = new HashMap<String, String>();
		try {
			setup(context);
			if (idString != null) {
				String id = idString;
				dscache.deleteDawidSkene(id);
			}
            // XXX a have inverted condition.
			message = "Reset the ds object";
            if (idString != null) {
                message += " with id=" + idString;
            }
			logger.info(message);
            cargo.put("message", message);
            cargo.put("status", "Success");
			return Response.ok(JSONUtils.gson.toJson(cargo)).build();
		} catch (IOException e) {
            message = e.getLocalizedMessage();
		} catch (ClassNotFoundException e) {
            message = e.getLocalizedMessage();
		} catch (SQLException e) {
            message = e.getLocalizedMessage();
		}
        message = "Problem resseting the ds model: " + message;
		logger.error(message);
        cargo.put("message", message);
        cargo.put("status", "Failure");
		return Response.ok(JSONUtils.gson.toJson(cargo)).build();
	}

	@GET
	@Path("exists")
	@Produces(MediaType.APPLICATION_JSON)
	public Response exists(@QueryParam("id") String idstr) {

		String id = "" + 0;
		if (null == idstr) {
			logger.info("no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			boolean contains = dscache.hasDawidSkene(id);
			String message = (contains ? "found ds object: "
							  : "didnt find ds object: ") + id;
			logger.info(message);

			return Response.ok(JSONUtils.toJson(contains)).build();
		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
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
		Collection<Category> categories;
		if (null == input || input.length() < 3) {
			String message = "invalid input. requires json-ified collection of category objects";
			logger.error(message);
			return Response.status(500).build();
		}
		String id = "" + 0;
		if (null == idstr) {
			logger.info("no id input, using id 0");
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
			logger.info(message);
			return Response.ok(message).build();

		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error("some other exception: " + e.getMessage());
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
		Collection<MisclassificationCost> costs;
		if (null == data || data.length() < 3) {
			String message = "invalid input. requires json-ified collection of misclassification cost objects";
			logger.error(message);
			return Response.status(500).build();
		}
		String id = "" + 0;
		if (null == idstr) {
			logger.info("no id input, using id 0");
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
			logger.info(message);
			return Response.ok(message).build();

		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
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
		AssignedLabel label;

		String id = "" + 0;
		if (null == idstr) {
			logger.info("no id input, using id 0");
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
			logger.info(message);
			return Response.ok(message).build();
		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
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
		Collection<AssignedLabel> input;

		String id = "" + 0;
		if (null == idstr) {
			logger.info("no id input, using id 0");
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
			logger.info(message);
			return Response.ok(message).build();
		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
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
		CorrectLabel label;

		String id = "" + 0;
		if (null == idstr) {
			logger.info("no id input, using id 0");
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
			logger.info(message);
			return Response.ok(message).build();
		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {

			logger.error(e.getLocalizedMessage());
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
		Collection<CorrectLabel> input;

		String id = "" + 0;
		if (null == idstr) {
			logger.info("no id input, using id 0");
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
			logger.info(message);
			return Response.ok(message).build();

		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
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

		String id = "" + 0;
		if (null == idstr) {
			logger.info("no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);
			String votes = ds.getMajorityVote(objectName);

			if (votes == null) {
				logger.warn("got a null majority vote for object: "
							+ objectName);
				Response.status(500).build();
			}

			String message = "computing majority votes for " + objectName;
			logger.info(message);
			return Response.ok(JSONUtils.gson.toJson(votes)).build();

		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {

			logger.error(e.getLocalizedMessage());
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
			if (null == objects)
				votes = ds.getMajorityVote();
			else {
				Collection<String> objectNames = JSONUtils.gson.fromJson(
													 objects, JSONUtils.stringSetType);
				votes = ds.getMajorityVote(objectNames);
			}
			String message = "computing majority votes for "
							 + (null == votes ? 0 : votes.size()) + " objects ";
			logger.info(message);
			return Response.ok(JSONUtils.gson.toJson(votes)).build();

		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {

			logger.error(e.getLocalizedMessage());
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
			logger.info(message);
			return Response.ok(JSONUtils.gson.toJson(votes)).build();

		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {

			logger.error(e.getLocalizedMessage());
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
		Map<String, Map<String, Double>> votes;

		String id = "" + 0;
		if (null == idstr) {
			logger.info("no id input, using id 0");
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
			logger.info(message);
			return Response.ok(JSONUtils.gson.toJson(votes)).build();

		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {

			logger.error(e.getLocalizedMessage());
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
		Map<String, Double> votes;

		String id = "" + 0;
		if (null == idstr) {
			logger.info("no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);
			votes = ds.getObjectProbs(objectName);
			String message = "computing object probs for " + objectName;
			logger.info(message);
			return Response.ok(JSONUtils.gson.toJson(votes)).build();

		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {

			logger.error(e.getLocalizedMessage());
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
		int its = 1;
		long time = 0;

		String id = 0 + "";
		if (null == idstr) {
			logger.info("no id input, using id 0");
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
			ds.estimate(its);
			sw.stop();
			time = sw.getTime();
			String message = "performed ds iteration " + its + " times, took: "
							 + time + "ms.";
			logger.info(message);
			dscache.insertDawidSkene(ds);

			return Response.ok(message).build();

		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}



	@GET
	@Path("compute")
	@Produces(MediaType.APPLICATION_JSON)
	public Response computeDS(
		@QueryParam("iterations") String iterations,
		@QueryParam("id") String idstr) {
		int its = 1;
		long time = 0;

		String id = 0 + "";
		if (null == idstr) {
			logger.info("no id input, using id 0");
		} else {
			id = idstr;
		}

		its = Math.max(1,
					   null == iterations ? 1 : Integer.parseInt(iterations));


		try {
			setup(context);
			DSalgorithmComputer computer = new DSalgorithmComputer(id,this.dscache,its);
			String message = "Registered DScomputer with  " + its + " iterations/";
			return Response.ok(message).build();

		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}


	@GET
	@Path("printWorkerSummary")
	@Produces(MediaType.APPLICATION_JSON)
	public Response printWorkerSummary(@QueryParam("verbose") String verbose,
									   @QueryParam("id") String idstr) {
		String output;

		String id = "" + 0;
		if (null == idstr) {
			logger.info("no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);

			boolean verb = null == verbose ? false : true;
			output = ds.printAllWorkerScores(verb);

			String message = "returning request for worker summary";
			logger.info(message);

			return Response.ok(output).build();

		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	@GET
	@Path("printObjectsProbs")
	@Produces(MediaType.APPLICATION_JSON)
	public Response printObjectsProbs(@QueryParam("entropy") String ent,
									  @QueryParam("id") String idstr) {
		String output;

		String id = "" + 0;
		if (null == idstr) {
			logger.info("no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);

			double entropy = null == ent ? 0. : Double.parseDouble(ent);
			output = ds.printObjectClassProbabilities(entropy);

			String message = "returning request for object class probs";
			logger.info(message);
			return Response.ok(output).build();

		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	@GET
	@Path("objectProbs")
	@Produces(MediaType.APPLICATION_JSON)
	public Response objectProbs(@QueryParam("object") String object,
								@QueryParam("entropy") String ent, @QueryParam("id") String idstr) {
		Map<String, Double> out;

		String id = "" + 0;
		if (null == idstr) {
			logger.info("no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);

			double entropy = null == ent ? 0. : Double.parseDouble(ent);
			out = ds.objectClassProbabilities(object, entropy);

			String message = "returning request for object class probs";
			logger.info(message);
			return Response.ok(JSONUtils.gson.toJson(out)).build();

		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	@GET
	@Path("printPriors")
	@Produces(MediaType.APPLICATION_JSON)
	public Response printPriors(@QueryParam("id") String idstr) {
		String output;

		String id = "" + 0;
		if (null == idstr) {
			logger.info("no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);
			output = ds.printPriors();
			String message = "returning request for object class probs";
			logger.info(message);

			return Response.ok(output).build();
		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	@GET
	@Path("classPriors")
	@Produces(MediaType.APPLICATION_JSON)
	public Response computePriors(@QueryParam("id") String idstr) {
		Map<String, Double> out = new HashMap<String, Double>();

		String id = "" + 0;
		if (null == idstr) {
			logger.info("no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);
			out = ds.computePriors();
			String message = "returning request for object class probs";
			logger.info(message);

			return Response.ok(JSONUtils.gson.toJson(out)).build();

		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	@GET
	@Path("getDawidSkene")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDawidSkene(@QueryParam("id") String idstr) {
		String id = "" + 0;
		if (null == idstr) {
			logger.info("no id input, using id 0");
		} else {
			id = idstr;
		}

		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);
			return Response.ok(JSONUtils.gson.toJson(ds)).build();
		} catch (IOException e) {
			logger.error("ioexception: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			logger.error("class not found exception: "
						 + e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.error("sql exception: " + e.getLocalizedMessage());
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
		return Response.status(500).build();
	}

	private void setup(ServletContext scontext) throws IOException,
		ClassNotFoundException, SQLException {
		if(dscache==null||manager==null) {
			logger.info("loading props file with context:"
						+ scontext.getContextPath());
			Properties props = new Properties();
			props.load(scontext
					   .getResourceAsStream("/WEB-INF/classes/dawidskene.properties"));
			if (dscache == null) {
				String user = props.getProperty("USER");
				String password = props.getProperty("PASSWORD");
				String db = props.getProperty("DB");
				String url = props.getProperty("URL");
				if (props.containsKey("cacheSize")) {
					int cachesize = Integer.parseInt(props.getProperty("cacheSize"));
					dscache = new DawidSkeneCache(user,password,db,url,cachesize);
				} else {
					dscache = new DawidSkeneCache(user,password,db,url);
				}
			}
			if(manager == null) {
				int threadPollSize = Integer.parseInt(props.getProperty("THREADPOLL_SIZE"));
				int sleepPeriod = Integer.parseInt(props.getProperty("PROCESSOR_MANAGER_SLEEP_PERIOD"));
				this.manager = new DawidSkeneProcessorManager(threadPollSize,sleepPeriod);
				this.manager.run();
			}
		}
	}

}
