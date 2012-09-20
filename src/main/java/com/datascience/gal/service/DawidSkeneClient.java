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
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.datascience.gal.AssignedLabel;
import com.datascience.gal.Category;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.MisclassificationCost;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.representation.Form;

/**
 * java client for the dawid skene service
 *
 * @author josh
 *
 */
public class DawidSkeneClient {
	private String host = "localhost";
	private String prefix = "GetAnotherLabel";
	private String port = "8080";
	private WebResource service;

	private ClientConfig config = new DefaultClientConfig();
	private Client client = Client.create(config);

	public DawidSkeneClient() {
		service = client.resource(getBaseURI());
	}

	public DawidSkeneClient(InputStream stream) throws IOException {
		Properties props = new Properties();
		props.load(stream);

		if (props.contains("host"))
			host = props.getProperty("host");
		if (props.contains("prefix"))
			prefix = props.getProperty("prefix");
		if (props.contains("port"))
			port = props.getProperty("port");

		service = client.resource(getBaseURI());
	}

	public boolean isAlive() {
		ClientResponse response = service.path("rest").path("ping")
								  .accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		return response.getClientResponseStatus().equals(
				   ClientResponse.Status.OK);
	}

	public DawidSkene getDawidSkene(int id) {
		DawidSkene ds = JSONUtils.gson.fromJson(
							service.path("rest").path("getDawidSkene")
							.queryParam("id", id + "").get(String.class),
							JSONUtils.dawidSkeneType);
		return ds;
	}

	public boolean deleteDS(int id) {
		ClientResponse response = service.path("rest").path("reset")
								  .queryParam("id", id + "").accept(MediaType.APPLICATION_JSON)
								  .get(ClientResponse.class);
		return response.getClientResponseStatus().equals(
				   ClientResponse.Status.OK);
	}

	public boolean initializeDS(int id, Collection<Category> categories,
								boolean incremental) {
		Form form = new Form();
		form.add("categories", JSONUtils.toJson(categories));
		form.add("id", id);
		if (incremental)
			form.add("incremental", "incremental");
		ClientResponse response = service.path("rest").path("loadCategories")
								  .type(MediaType.APPLICATION_FORM_URLENCODED)
								  .post(ClientResponse.class, form);

		return response.getClientResponseStatus().equals(
				   ClientResponse.Status.OK);
	}

	public boolean addMisclassificationCosts(int id,
			Collection<MisclassificationCost> costs) {
		Form form = new Form();
		form.add("id", id);
		form.add("costs", JSONUtils.toJson(costs));
		ClientResponse response = service.path("rest").path("loadCosts")
								  .type(MediaType.APPLICATION_FORM_URLENCODED)
								  .post(ClientResponse.class, form);
		return response.getClientResponseStatus().equals(
				   ClientResponse.Status.OK);
	}

	public boolean addAssignedLabels(int id, Collection<AssignedLabel> labels) {
		Form form = new Form();
		form.add("id", id);
		form.add("data", JSONUtils.toJson(labels));
		ClientResponse response = service.path("rest")
								  .path("loadWorkerAssignedLabels")
								  .type(MediaType.APPLICATION_FORM_URLENCODED)
								  .post(ClientResponse.class, form);
		return response.getClientResponseStatus().equals(
				   ClientResponse.Status.OK);
	}

	public boolean addCorrectLabels(int id, Collection<CorrectLabel> labels) {
		Form form = new Form();
		form.add("id", id);
		form.add("data", JSONUtils.toJson(labels));
		ClientResponse response = service.path("rest").path("loadGoldLabels")
								  .type(MediaType.APPLICATION_FORM_URLENCODED)
								  .post(ClientResponse.class, form);
		return response.getClientResponseStatus().equals(
				   ClientResponse.Status.OK);
	}

	public boolean iterateBlocking(int id, int iterations) {
		ClientResponse response = service.path("rest").path("computeBlocking")
								  .queryParam("id", "" + id)
								  .queryParam("iterations", iterations + "")
								  .get(ClientResponse.class);
		return response.getClientResponseStatus().equals(
				   ClientResponse.Status.OK);
	}

	public String computeMajorityVote(int id, String objectName) {
		ClientResponse response = service.path("rest").path("majorityVote")
								  .queryParam("id", "" + id).queryParam("objectName", objectName)
								  .get(ClientResponse.class);
		if (!response.getClientResponseStatus()
				.equals(ClientResponse.Status.OK)) {
			return null;
		}
		return response.getEntity(String.class);
	}

	public Map<String, String> computeMajorityVotes(int id) {
		ClientResponse response = service.path("rest").path("majorityVotes")
								  .queryParam("id", id + "").get(ClientResponse.class);
		if (!response.getClientResponseStatus()
				.equals(ClientResponse.Status.OK)) {
			return null;
		}
		Map<String, String> majorityvotes = JSONUtils.gson
											.fromJson(response.getEntity(String.class),
													JSONUtils.stringStringMapType);
		return majorityvotes;
	}

	public Map<String, String> computeMajorityVotes(int id,
			Collection<String> objectNames) {
		Form form = new Form();
		form.add("id", id);
		form.add("objects", JSONUtils.toJson(objectNames));

		ClientResponse response = service.path("rest").path("majorityVotes")
								  .type(MediaType.APPLICATION_FORM_URLENCODED)
								  .post(ClientResponse.class, form);
		if (!response.getClientResponseStatus()
				.equals(ClientResponse.Status.OK)) {
			return null;
		}
		Map<String, String> majorityvotes = JSONUtils.gson
											.fromJson(response.getEntity(String.class),
													JSONUtils.stringStringMapType);
		return majorityvotes;
	}

	public Map<String, Map<String, Double>> computeObjectProbs(int id,
			Collection<String> objectNames) {
		Form form = new Form();
		form.add("id", id);
		form.add("objects", JSONUtils.toJson(objectNames));

		ClientResponse response = service.path("rest").path("objectProbs")
								  .type(MediaType.APPLICATION_FORM_URLENCODED)
								  .post(ClientResponse.class, form);
		if (!response.getClientResponseStatus()
				.equals(ClientResponse.Status.OK)) {
			return null;
		}
		Map<String, Map<String, Double>> majorityvotes = JSONUtils.gson
				.fromJson(response.getEntity(String.class),
						  JSONUtils.stringStringDoubleMapType);
		return majorityvotes;
	}

	public Map<String, Double> computeObjectProb(int id, String objectName) {

		ClientResponse response = service.path("rest").path("objectProbs")
								  .queryParam("id", "" + id).queryParam("object", objectName)
								  .get(ClientResponse.class);
		if (!response.getClientResponseStatus()
				.equals(ClientResponse.Status.OK)) {
			return null;
		}
		Map<String, Double> objectProb = JSONUtils.gson
										 .fromJson(response.getEntity(String.class),
												 JSONUtils.stringDoubleMapType);
		return objectProb;
	}

	public boolean hasDSObject(int id) {
		ClientResponse response = service.path("rest").path("exists")
								  .queryParam("id", "" + id).get(ClientResponse.class);
		if (!response.getClientResponseStatus()
				.equals(ClientResponse.Status.OK)) {
			return false;
		}
		Boolean contains = JSONUtils.gson.fromJson(
							   response.getEntity(String.class), JSONUtils.booleanType);
		return contains;
	}

	private URI getBaseURI() {
		String url = (host.startsWith("http") ? host : "http://" + host) + ":"
					 + port + "/" + prefix;
		return UriBuilder.fromUri(url).build();
	}

}
