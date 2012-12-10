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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;

import com.datascience.gal.AssignedLabel;
import com.datascience.gal.Category;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.MisclassificationCost;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
/**
 * @author Michael Arshynov
 *
 */
@Path("notjson")
public class ServiceNotJSON {
	@Context
	ServletContext context;

	@Context
	UriInfo uriInfo;

	private static Logger logger = Logger.getLogger(ServiceNotJSON.class);

	/**
	 * Loads a json set of category objects
	 *
	 * @param names
	 * @param priors
	 * @param misclassification_costs
	 * @param idstr
	 * @param incremental
	 * @return a simple success message or an error
	 */
	@POST
	@Path("loadCategories")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadCategories(
		@FormParam("names") String names,
		@FormParam("id") String idstr,
		@FormParam("incremental") String incremental) {

		//[{"name":"name1","prior":-1.0,"misclassification_cost":{}},
		//{"name":"name2","prior":-1.0,"misclassification_cost":{"name1":0.1,"name2":0.2}}]
		//  would be names=name1,name2
		//
		// coz to set misclassification_cost is a pointless activity, anyway would be
		//[{"name":"name1","prior":0.0,"misclassification_cost":{"name1":0.0,"name2":1.0}},
		//{"name":"name2","prior":0.0,"misclassification_cost":{"name1":1.0,"name2":0.0}}]
		logger.info("ServiceNotJSON: loadCategories("+names+","+idstr+","+incremental+")");
		Set<Category> categories = null;
		boolean isValid = validateNotEmptyArray(names);
		logger.info("ServiceNotJSON: loadCategories(..) isValid="+isValid);
		if (isValid) {
			categories = loadCategoriesAux(names);
			logger.info("ServiceNotJSON: loadCategories(..) categories="+categories.toString());
			String input = JSONUtils.gson.toJson(categories,JSONUtils.categorySetType);
			logger.info("ServiceNotJSON: loadCategories(..) categories(json)="+input);

			Map<String, String> map = new HashMap<String, String>();
			map.put("categories", input);
			map.put("id", idstr);
			map.put("incremental", incremental);
			return  Response.ok(post(map,"loadCategories")).build();
		} else {
			return Response.ok("input data are not valid, please check-fix-try again").build();
		}
	}


	/**
	 * @param names
	 * @return
	 */
	private synchronized boolean validateNotEmptyArray(String names) {
		boolean isValid = false;
		if (names!=null && names.length()>0 ) {
			String[] namesArray = names.split(",");
			if (namesArray.length>0) {
				isValid = true;
			}
		}
		return isValid;
	}

	/**
	 * @param names
	 * @return
	 */
	private synchronized Set<Category> loadCategoriesAux(String names) {
		String[] namesArray = names.split(",");
		Set<Category> set = new HashSet<Category>();
		for (int i=0; i<namesArray.length; i++) {
			Category category = new Category(namesArray[i]);
			set.add(category);
		}
		return set;
	}

	private synchronized Set<MisclassificationCost> loadCostsAux(final String from, final String to, final String cost) throws Exception {
		String[] fromArray = from.split(",");
		String[] toArray = to.split(",");
		String[] costArray = cost.split(",");

		Set<MisclassificationCost> set = new HashSet<MisclassificationCost>();
		for (int i=0; i<fromArray.length; i++) {
			MisclassificationCost miscCost = new MisclassificationCost(fromArray[i], toArray[i], Double.parseDouble(costArray[i]));
			set.add(miscCost);
		}
		return set;
	}

	/**
	 * @param map
	 * @param methodName
	 * @return
	 */
	private synchronized String post(Map<String, String> map, String methodName) {
		String ret = new String("ok");
		ClientConfig config = new DefaultClientConfig();
		com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create(config);
		String path = uriInfo.getBaseUri().toString()+methodName;
		WebResource resource = client.resource(path);
		if (map==null) {
			ClientResponse response = resource.post(ClientResponse.class);
			ret = response.toString();
		} else {
			MultivaluedMap<String, String> queryParams  = new MultivaluedMapImpl();
			for (String key:map.keySet()) {
				queryParams.add(key, map.get(key));
			}
//    		ClientResponse response = resource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, queryParams);
			ClientResponse response = resource.post(ClientResponse.class, queryParams);
			ret = response.toString();
		}
		return ret;
	}

	/**
	 * @param map
	 * @param methodName
	 * @return
	 */
	private synchronized String get(Map<String,String> map, String methodName) {
		String ret = new String("ok");
		ClientConfig config = new DefaultClientConfig();
		com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create(config);
		String path = uriInfo.getBaseUri().toString()+methodName;
		WebResource resource = client.resource(path);

		if (map==null) {
			ClientResponse response = resource.get(ClientResponse.class);
			ret = response.toString();
		} else {
			MultivaluedMap<String, String> queryParams  = new MultivaluedMapImpl();
			for (String key:map.keySet()) {
				queryParams.add(key, map.get(key));
			}
			resource.queryParams(queryParams);
//    		ClientResponse response = resource.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
			ClientResponse response = resource.get(ClientResponse.class);
			ret = response.toString();
		}

		return ret;
	}

	public static final void main(String [] args) {
		System.out.println("<<<<<");
		Map<String, Double> misclassification_costs = new HashMap<String, Double>();
		misclassification_costs.put("cat1", 0.1);
		misclassification_costs.put("cat2", 0.2);

		System.out.println(JSONUtils.gson.toJson(misclassification_costs, JSONUtils.categoryProbMapType));
		System.out.println(">>>>>");
	}

	/**
	 * @param from
	 * @param to
	 * @param cost
	 * @param id
	 * @return
	 */
	@POST
	@Path("loadCosts")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadMisclassificationCosts(@FormParam("from") String from,
			@FormParam("to") String to, @FormParam("cost") String cost,
			@FormParam("id") String id) {
		logger.info("ServiceNotJSON: loadCosts(" + from + "," + to + ","
					+ cost + "," + id + ")");

		Collection<MisclassificationCost> costs;
		boolean isOkFrom = validateNotEmptyArray(from);
		boolean isOkTo = validateNotEmptyArray(to);
		boolean isOkCost = validateNotEmptyArray(cost);
		if (isOkFrom && isOkTo && isOkCost) {
			try {
				Set<MisclassificationCost> mCosts = loadCostsAux(from, to, cost);
				Map<String, String> map = new HashMap<String, String>();
				String input = JSONUtils.gson.toJson(mCosts,JSONUtils.misclassificationCostSetType);
				map.put("costs", input);
				map.put("id", id);
				return  Response.ok(post(map,"loadCosts")).build();
			} catch (Exception e) {
				logger.error("ServiceNotJSON: loadCosts(),"+e.getMessage());
			}
		}
		return Response.ok("input data are not valid, please check-fix-try again").build();

	}

	/**
	 * @param workerName
	 * @param objectName
	 * @param categoryName
	 * @param idstr
	 * @return
	 */
	@POST
	@Path("loadWorkerAssignedLabel")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadWorkerAssignedLabel(@FormParam("workerName") String workerName,
											@FormParam("objectName") String objectName,
											@FormParam("categoryName") String categoryName,
											@FormParam("id") String idstr) {
		logger.info("ServiceNotJSON: loadWorkerAssignedLabel(" + workerName + "," + objectName + ","
					+ categoryName + "," + idstr + ")");
		if (workerName!=null && objectName!=null && categoryName!=null) {
			try {
				AssignedLabel label = new AssignedLabel(workerName, objectName, categoryName);
				Map<String, String> map = new HashMap<String, String>();
				String input = JSONUtils.gson.toJson(label,JSONUtils.assignedLabelType);
				map.put("data", input);
				map.put("id", idstr);
				return  Response.ok(post(map,"loadWorkerAssignedLabel")).build();
			} catch(Exception e) {
				logger.error("ServiceNotJSON: loadWorkerAssignedLabel(),"+e.getMessage());
			}
		}
		return Response.ok("input data are not valid, please check-fix-try again").build();
	}

	/**
	 * @param workerName
	 * @param objectName
	 * @param categoryName
	 * @param idstr
	 * @return
	 */
	@POST
	@Path("loadWorkerAssignedLabels")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadWorkerAssignedLabels(@FormParam("workerName") String workerName,
			@FormParam("objectName") String objectName,
			@FormParam("categoryName") String categoryName,
			@FormParam("id") String idstr) {
		logger.info("ServiceNotJSON: loadWorkerAssignedLabels(" + workerName + "," + objectName + ","
					+ categoryName + "," + idstr + ")");
		if (workerName!=null && objectName!=null && categoryName!=null) {
			boolean isOkWorkerName = validateNotEmptyArray(workerName);
			boolean isOkObjectName = validateNotEmptyArray(objectName);
			boolean isOkCategoryName = validateNotEmptyArray(categoryName);
			if (isOkWorkerName && isOkObjectName && isOkCategoryName) {
				try {
					String[] wnArray = workerName.split(",");
					String[] onArray = objectName.split(",");
					String[] cnArray = categoryName.split(",");
					if (wnArray.length==onArray.length && onArray.length==cnArray.length) {
						Set<AssignedLabel> labelSet = new HashSet<AssignedLabel>();
						for(int i=0; i<wnArray.length; i++) {
							AssignedLabel label = new AssignedLabel(wnArray[i], onArray[i], cnArray[i]);
							logger.info("....."+label);
							labelSet.add(label);
						}
						Map<String, String> map = new HashMap<String, String>();
						String input = JSONUtils.gson.toJson(labelSet,JSONUtils.assignedLabelSetType);
						logger.info("ServiceNotJSON: loadWorkerAssignedLabels(..."+input);
						map.put("data", input);
						map.put("id", idstr);
						return  Response.ok(post(map,"loadWorkerAssignedLabels")).build();
					}
				} catch(Exception e) {
					logger.error("ServiceNotJSON: loadWorkerAssignedLabels(),"+e.getMessage());
				}
			}
		}
		return Response.ok("input data are not valid, please check-fix-try again").build();
	}

	/**
	 * @param objectName
	 * @param correctCategory
	 * @param idstr
	 * @return
	 */
	@POST
	@Path("loadGoldLabel")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadGoldLabel(@FormParam("objectName") String objectName,
								  @FormParam("correctCategory") String correctCategory,
								  @FormParam("id") String idstr) {
		if (objectName!=null && correctCategory!=null && idstr!=null) {
			try {
				CorrectLabel label = new CorrectLabel(objectName, correctCategory);
				String input = JSONUtils.gson.toJson(label, JSONUtils.correctLabelType);
				Map<String, String> map = new HashMap<String, String>();
				map.put("data", input);
				map.put("id", idstr);
				return  Response.ok(post(map,"loadGoldLabel")).build();
			} catch (Exception e) {
				logger.error("ServiceNotJSON: loadGoldLabel(),"+e.getMessage());
			}
		}
		return Response.ok("input data are not valid, please check-fix-try again").build();
	}

	/**
	 * @param objectName
	 * @param correctCategory
	 * @param idstr
	 * @return
	 */
	@POST
	@Path("loadGoldLabels")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loadGoldLabels(@FormParam("objectName") String objectName,
								   @FormParam("correctCategory") String correctCategory,
								   @FormParam("id") String idstr) {
		if (objectName!=null && correctCategory!=null && idstr!=null) {
			try {
				boolean isOkObjectName = validateNotEmptyArray(objectName);
				boolean isOkCorrectCategory = validateNotEmptyArray(correctCategory);
				if (isOkObjectName && isOkCorrectCategory) {
					String[] onArray = objectName.split(",");
					String[] ccArray = correctCategory.split(",");
					if (onArray.length==ccArray.length) {
						Set<CorrectLabel> labelSet = new HashSet<CorrectLabel>();
						for (int i=0; i<onArray.length; i++) {
							labelSet.add(new CorrectLabel(onArray[i], ccArray[i]));
						}
						String input = JSONUtils.gson.toJson(labelSet, JSONUtils.correctLabelSetType);
						Map<String, String> map = new HashMap<String, String>();
						map.put("data", input);
						map.put("id", idstr);
						return  Response.ok(post(map,"loadGoldLabels")).build();
					}
				}
			} catch (Exception e) {
				logger.error("ServiceNotJSON: loadGoldLabels(),"+e.getMessage());
			}
		}
		return Response.ok("input data are not valid, please check-fix-try again").build();
	}
}
