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
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
import com.datascience.gal.BatchDawidSkene;
import com.datascience.gal.Category;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.MisclassificationCost;
import com.datascience.gal.core.DataQualityEstimator;
import com.datascience.gal.dawidSkeneProcessors.CacheUpdater;
import com.datascience.gal.dawidSkeneProcessors.CategoryWriter;
import com.datascience.gal.dawidSkeneProcessors.DSalgorithmComputer;
import com.datascience.gal.dawidSkeneProcessors.DawidSkeneProcessorManager;
import com.datascience.gal.dawidSkeneProcessors.DawidSkeneRemover;
import com.datascience.gal.dawidSkeneProcessors.GoldLabelWriter;
import com.datascience.gal.dawidSkeneProcessors.LabelWriter;
import com.datascience.gal.dawidSkeneProcessors.MisclassificationCostsWriter;


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

	private static DawidSkeneCache dscache = null;

	private static DawidSkeneProcessorManager manager = null;
       
    private String getIdFromInputDefault(String input, String def) {
        String id = def;
        if (null == input)  {
			logger.info("No id input: using default id=" + def);
        } else {
            id = input;
        }
        return id;
    }

    private String getIdFromInput(String input) {
        return getIdFromInputDefault(input, "0");
    }

    private <T> T parseJsonInput(String input, Type type) throws Exception {
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

    private void logErrorFromException(Exception e) {
        logger.error("Error processing the request: " + 
                e.getClass().getName() + ": " + e.getLocalizedMessage());
    }

    private static Response buildResponse(String message, String status, 
            Object result, DateTime timestamp,
            Map<String, Object> others) {
            Map<String, Object> cargo = new HashMap<String, Object>();
        if (null != message) {
            cargo.put("message", message);
        }
        if (null != status) {
            cargo.put("status", status);
        }
        if (null != result) {
            cargo.put("result", result.toString());
        }
        if (null != timestamp) {
            cargo.put("timestamp", timestamp.toString());
        }
        if (null != others) {
            cargo.putAll(others);
        }
        return Response.ok(JSONUtils.gson.toJson(cargo)).build();
    }

	public void init(ServletConfig config) throws ServletException {
		ServletContext scontext = config.getServletContext();
		Properties props = new Properties();
		try {
			props.load(scontext.getResourceAsStream("/WEB-INF/classes/dawidskene.properties"));
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
				this.manager.start();
			}
		} catch(Exception e) {
			logger.error(e.getMessage());
		}
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
		try {
			setup(context);
            String message = "Request successfully processed";
            logger.info(message);
            return buildResponse(message, SUCCESS, null, new DateTime(), null); 
		} catch (Exception e) {
            logErrorFromException(e);
		}
		return Response.status(500).build();
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
		final String id = "1234512345";
		Set<Category> categories = new HashSet<Category>();
		categories.add(new Category("mock"));
		DawidSkene ds = new BatchDawidSkene(id, categories);
		try {
			setup(context);
            String message = "";
            DawidSkene dsInserted = dscache.insertDawidSkene(ds);
            DawidSkene dsRetrieved = dscache.getDawidSkene(id);
            if (null != dsInserted && null != dsRetrieved) {
                dscache.deleteDawidSkene(id);
                message = " Object has been inserted to the DB and removed " +
                    "from the DB ";
                dsRetrieved = dscache.getDawidSkene(id);
                message += (null != dsRetrieved) ? "successfully" : 
                    "unsuccessfully";
            } else {
                message = "Object has NOT been inserted to the DB";
            }
            logger.info(message);
            Map<String, Object> others = new HashMap<String, Object>();
            others.put("id", id);
            return buildResponse(message, SUCCESS, null, new DateTime(), 
                    others); 
		} catch (Exception e) {
            logErrorFromException(e);
        }
        return Response.status(500).build();
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
		try {
			setup(context);
			if (idString != null) {
				String id = idString;
				DawidSkeneRemover remover = new DawidSkeneRemover(idString,dscache);
				manager.addProcessor(remover);
			}
			String message = "Reset the ds model";
            if (idString != null) {
                message += " with id=" + idString;
            }
			logger.info(message);
            return buildResponse(message, SUCCESS, null, new DateTime(), 
                    null);
        } catch (Exception e) {
            logErrorFromException(e);
		}
		return Response.status(500).build();
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
		try {
			setup(context);
			Boolean exists = dscache.hasDawidSkene(id);
			logger.info("Model with id=" + id + " " + 
                    (exists ? "exists" : "does not exist"));
            return buildResponse(null, null, exists, null, null);
		} catch (Exception e) {
		    logger.error("A problem with processing the request: " 
                         + e.getClass() + ": " + e.getLocalizedMessage());
		}
        return Response.status(500).build();
	}

	/**
	 * loads a JSON set of category objects
	 *
	 * @return a simple success message
	 */
	@POST
	@Path("loadCategories")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadCategories(@FormParam("data") String data,
		    @FormParam("id") String idString,
			@FormParam("incremental") String incremental) {
		String id = getIdFromInput(idString);
		try {
			setup(context);
			Collection<Category> categories = parseJsonInput(data, 
                    JSONUtils.categorySetType);
			DawidSkene ds = (null == incremental) ? 
                new BatchDawidSkene(id, categories) :
                new IncrementalDawidSkene(id, categories);
			CategoryWriter writer = new CategoryWriter(id,dscache,categories,incrementalDs);
			manager.addProcessor(writer);
			String message = "Built a ds model with " + categories.size()
			    + " categories";
			logger.info(message);
            return buildResponse(message, SUCCESS, null, new DateTime(), null);
		} catch (Exception e) {
            logErrorFromException(e);
		}
		return Response.status(500).build();
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
            @FormParam("data") String data, 
            @FormParam("id") String idString) {
		String id = getIdFromInput(idString);
		try {
			setup(context);
            Collection<MisclassificationCost> costs = parseJsonInput(data, 
                    JSONUtils.misclassificationCostSetType);
			String message = "Loaded " + costs.size()
			    + " misclassification costs";
			MisclassificationCostsWriter writer = 
                new MisclassificationCostsWriter(id, dscache, costs);
            // TODO manager.add(writer)?
			logger.info(message);
			return buildResponse(message, SUCCESS, null, new DateTime(), null); 
		} catch (Exception e) {
			logErrorFromException(e);
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
		    @FormParam("id") String idString) {
		String id = getIdFromInput(idString);
		try {
			setup(context);
            AssignedLabel label = parseJsonInput(data, 
                    JSONUtils.assignedLabelType);
			Collection<AssignedLabel> labels = new ArrayList<AssignedLabel>();
			labels.add(label);
			LabelWriter writer = new LabelWriter(id, dscache, labels);
			manager.addProcessor(writer);
			String message = "Added 1 label: " + label;
			logger.info(message);
            return buildResponse(message, SUCCESS, null, new DateTime(), null);
		} catch (Exception e) {
            logErrorFromException(e);
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
			@FormParam("id") String idString) {
		String id = getIdFromInput(idString);
		try {
			setup(context);
		    Collection<AssignedLabel> labels = parseJsonInput(data,
			    JSONUtils.assignedLabelSetType);
			LabelWriter writer = new LabelWriter(id,dscache,labels);
			manager.addProcessor(writer);
			String message = "Added " + labels.size() + " labels";
			logger.info(message);
            return buildResponse(message, SUCCESS, null, new DateTime(), null);
		} catch (Exception e) {
            logErrorFromException(e);
		}
		return Response.status(500).build();
	}

	/**
	 * loads a gold label for given model
	 *
	 * @return a simple success message
	 */
	@POST
	@Path("loadGoldLabel")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadGoldLabel(@FormParam("data") String data,
            @FormParam("id") String idString) {
		String id = getIdFromInput(idString);
		try {
			setup(context);
            CorrectLabel label = parseJsonInput(data, 
                    JSONUtils.correctLabelType);
			Collection<CorrectLabel> labels = new ArrayList<CorrectLabel>();
			labels.add(label);
			GoldLabelWriter writer = new GoldLabelWriter(id,dscache,input);
			manager.addProcessor(writer);
			String message = "Added 1 gold label: " + label;
			logger.info(message);
            return buildResponse(message, SUCCESS, null, new DateTime(), null);
		} catch (Exception e) {
            logErrorFromException(e);
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
								   @FormParam("id") String idString) {
		Collection<CorrectLabel> input;

		String id = "" + 0;
		if (null == idString) {
			logger.info("no id input, using id 0");
		} else {
			id = idString;
		}

		try {
			setup(context);
			input = JSONUtils.gson
					.fromJson(data, JSONUtils.correctLabelSetType);
			GoldLabelWriter writer = new GoldLabelWriter(id,dscache,input);
			manager.addProcessor(writer);
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
	public Response majorityVote(@QueryParam("id") String idString,
								 @QueryParam("objectName") String objectName) {

		String id = "" + 0;
		if (null == idString) {
			logger.info("no id input, using id 0");
		} else {
			id = idString;
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
	public Response majorityVotes(@FormParam("id") String idString,
								  @FormParam("objects") String objects) {
		Map<String, String> votes;

		String id = "" + 0;
		if (null == idString) {
			logger.info("no id input, using id 0");
		} else {
			id = idString;
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
	public Response majorityVotes(@QueryParam("id") String idString) {
		Map<String, String> votes;

		String id = "" + 0;
		if (null == idString) {
			logger.info("no id input, using id 0");
		} else {
			id = idString;
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
	public Response objectProbs(@FormParam("id") String idString,
								@FormParam("objects") String objects) {
		Map<String, Map<String, Double>> votes;

		String id = "" + 0;
		if (null == idString) {
			logger.info("no id input, using id 0");
		} else {
			id = idString;
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
	public Response objectProb(@QueryParam("id") String idString,
							   @QueryParam("object") String objectName) {
		Map<String, Double> votes;

		String id = "" + 0;
		if (null == idString) {
			logger.info("no id input, using id 0");
		} else {
			id = idString;
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
		@QueryParam("id") String idString) {
		int its = 1;
		long time = 0;

		String id = 0 + "";
		if (null == idString) {
			logger.info("no id input, using id 0");
		} else {
			id = idString;
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
			CacheUpdater updater = new CacheUpdater(id,dscache,ds);
			manager.addProcessor(updater);

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
		@QueryParam("id") String idString) {
		int its = 1;

		String id = 0 + "";
		if (null == idString) {
			logger.info("no id input, using id 0");
		} else {
			id = idString;
		}

		its = Math.max(1,
					   null == iterations ? 1 : Integer.parseInt(iterations));


		try {
			setup(context);
			DSalgorithmComputer computer = new DSalgorithmComputer(id,dscache,its);
			manager.addProcessor(computer);
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
									   @QueryParam("id") String idString) {
		String output;

		String id = "" + 0;
		if (null == idString) {
			logger.info("no id input, using id 0");
		} else {
			id = idString;
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
									  @QueryParam("id") String idString) {
		String output;

		String id = "" + 0;
		if (null == idString) {
			logger.info("no id input, using id 0");
		} else {
			id = idString;
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
								@QueryParam("entropy") String ent, @QueryParam("id") String idString) {
		Map<String, Double> out;

		String id = "" + 0;
		if (null == idString) {
			logger.info("no id input, using id 0");
		} else {
			id = idString;
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
	public Response printPriors(@QueryParam("id") String idString) {
		String output;

		String id = "" + 0;
		if (null == idString) {
			logger.info("no id input, using id 0");
		} else {
			id = idString;
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
	public Response computePriors(@QueryParam("id") String idString) {
		Map<String, Double> out = new HashMap<String, Double>();

		String id = "" + 0;
		if (null == idString) {
			logger.info("no id input, using id 0");
		} else {
			id = idString;
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
	public Response getDawidSkene(@QueryParam("id") String idString) {
		String id = "" + 0;
		if (null == idString) {
			logger.info("no id input, using id 0");
		} else {
			id = idString;
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

	private String getJobId(String jid) {
		if (jid == null || jid == "") {
			logger.info("No job ID or empty - using default");
			return DEFAULT_JOB_ID;
		}
		return jid;
	}
	
	private void handleException(String apiEntry, String message, Exception e){
		message = apiEntry + " with " + message;
		handleException(message, e);
	}
	
	private void handleException(String message, Exception e){
		logger.error(message);
		logger.error("EXCEPTION: " + e.getClass().getName());
		for (StackTraceElement ste : e.getStackTrace()){
			logger.error(ste);
		}
	}

	@GET
	@Path("getEstimatedCost")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEstimatedCost(@QueryParam("id") String jid, @QueryParam("method") String method, @QueryParam("object") String object) {
		String id = getJobId(jid);
		String message = "getEstimatedCost(" + method + ") for " + object + " in job " + id;
		try {
			setup(context);
			DawidSkene ds = dscache.getDawidSkene(id);
			DataQualityEstimator dqe = new DataQualityEstimator();
			Double ec = dqe.estimateMissclassificationCost(ds, method, object);
			logger.info(message + " OK");
			return Response.ok(ec.toString()).build();
		} catch (Exception e) {
			handleException(message, e);
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
				manager = new DawidSkeneProcessorManager(threadPollSize,sleepPeriod);
				manager.start();
			}
		}
	}

}
