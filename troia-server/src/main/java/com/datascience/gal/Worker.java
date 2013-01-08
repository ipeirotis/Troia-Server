/*******************************************************************************
 * Copyright (c) 2012 Panagiotis G. Ipeirotis & Josh M. Attenberg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.datascience.gal;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.datascience.core.storages.JSONUtils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class Worker {

	public static final WorkerDeserializer deserializer = new WorkerDeserializer();

	private final String name;

	// The error matrix for the worker
	public ConfusionMatrix cm;

	// The labels that have been assigned to this object, together with the
	// workers who
	// assigned these labels. Serves mainly as a speedup, and intended to be
	// used in
	// environments with persistence and caching (especially memcache)
	private Set<AssignedLabel> labels;

	/**
	 * @return the labels
	 */
	public Set<AssignedLabel> getAssignedLabels() {
		return labels;
	}

	private double quality;

	/**
	 * @return the quality
	 */
	public double getQuality() {
		return quality;
	}

	/**
	 * @param quality the quality to set
	 */
	public void setQuality(double quality) {
		this.quality = quality;
	}

	private Worker(String name, Collection<AssignedLabel> labels,
				   ConfusionMatrix cm) {
		this.name = name;
		this.labels = new HashSet<AssignedLabel>(labels);
		this.cm = cm;
	}

	public Worker(String name, Set<Category> categories) {
		this.name = name;
		this.cm = new MultinomialConfusionMatrix(categories);
		this.labels = new HashSet<AssignedLabel>();

	}

	public void empty() {
		cm.empty();
	}

	/**
	 * gets the total categorical error rate weighted by the prior. TODO: make
	 * incremental.
	 *
	 * @param categories
	 * @return
	 */
	public Map<String, Double> getPrior(Map<String, Double> categoryPriors) {
		HashMap<String, Double> worker_prior = new HashMap<String, Double>();

		for (String catName : categoryPriors.keySet()) {
			worker_prior.put(catName, 0.0);
		}

		for (String from : categoryPriors.keySet()) {
			for (String to : categoryPriors.keySet()) {
				double existing = worker_prior.get(to);
				double from2to = categoryPriors.get(from)
								 * cm.getErrorRateBatch(from, to);
				worker_prior.put(to, existing + from2to);
			}
		}

		return worker_prior;
	}
	
	public Map<String, Double> getSoftLabelForLabel(String label, Map<String, Category>	categories, Map<String, Double> workerPriors) {
		HashMap<String, Double> result = new HashMap<String, Double>();
		for (Category source : categories.values()) {
			double error = getErrorRateBatch(source.getName(), label);
			double workerprior = workerPriors.get(label);
			double sourceprior = source.getPrior();
			result.put(source.getName(), workerprior > 0 ? sourceprior * error / workerprior : 0);
		}

		return result;
	}

	public void addError(String source, String destination, double error) {
		cm.addError(source, destination, error);
	}

	public void removeError(String source, String destination, double error) {
		cm.removeError(source, destination, error);
	}

	public void normalize(ConfusionMatrixNormalizationType type) {
		switch (type) {
		case UNIFORM:
			cm.normalize();
			break;
		case LAPLACE:
			cm.normalizeLaplacean();
			break;
		}
	}

	public void addAssignedLabel(AssignedLabel al) {
		if (al.getWorkerName().equals(name)) {
			labels.add(al);
		}
	}

	public double getErrorRateIncremental(String categoryFrom,
										  String categoryTo, ConfusionMatrixNormalizationType type) {
		switch (type) {
		case UNIFORM:
			return cm.getNormalizedErrorRate(categoryFrom, categoryTo);
		case LAPLACE:
		default:
			return cm.getLaplaceNormalizedErrorRate(categoryFrom, categoryTo);
		}
	}

	public double getErrorRateBatch(String categoryFrom, String categoryTo) {
		return cm.getErrorRateBatch(categoryFrom, categoryTo);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Worker))
			return false;
		Worker other = (Worker) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if(!this.labels.equals(other.labels)) {
			return false;
		}
		if (!this.cm.equals(other.cm)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return this.name;
	}

	public static class WorkerDeserializer implements JsonDeserializer<Worker> {

		@Override
		public Worker deserialize(JsonElement json, Type type,
								  JsonDeserializationContext context) throws JsonParseException {
			JsonObject jobject = (JsonObject) json;
			String name = jobject.get("name").getAsString();
			ConfusionMatrix conf = JSONUtils.gson.fromJson(jobject.get("cm"),
								   JSONUtils.confusionMatrixType);
			Collection<AssignedLabel> labels = JSONUtils.gson.fromJson(
												   jobject.get("labels"), JSONUtils.assignedLabelSetType);

			return new Worker(name, labels, conf);
		}

	}
}
