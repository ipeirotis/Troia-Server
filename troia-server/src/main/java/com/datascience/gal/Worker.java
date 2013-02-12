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
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.datascience.core.storages.JSONUtils;
import com.datascience.utils.Utils;
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
	
	//The confusion matrix for the worker based on evaluation data
	private ConfusionMatrix eval_cm;

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
	public Map<String, Double> getPrior(Collection<String> categories){
		int sum = labels.size();
		HashMap<String, Double> worker_prior = new HashMap<String, Double>();
		for (String category : categories) {
			if (sum>0) {
				double cnt = 0;
				for (AssignedLabel al : labels)
					if (al.getCategoryName().equals(category))
						cnt += 1.;
				Double prob = cnt / sum;
				worker_prior.put(category, prob);
			} else {
				worker_prior.put(category, 1.0/categories.size());
			}
		}
		return worker_prior;
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

	public String getName() {
		return name;
	}
	
	public void computeEvalConfusionMatrix(Map<String, CorrectLabel> evalData, Collection<Category> categories) {
		eval_cm = new MultinomialConfusionMatrix(categories, new HashMap<CategoryPair, Double>());
		for (AssignedLabel l : labels) {
			String objectName = l.getObjectName();
			CorrectLabel d = evalData.get(objectName);
			if (d != null){
				String assignedCategory = l.getCategoryName();
				String correctCategory = d.getCorrectCategory();
				eval_cm.addError(correctCategory, assignedCategory, 1.0);
			}
		}
		eval_cm.normalize();
	}
	
	public double getEvalErrorRate(String from, String to){
		return eval_cm.getErrorRateBatch(from, to);
	}
	
	private int countGoldTests(Map<String, Datum> objects){
		int result = 0;
		for (AssignedLabel al : labels) {
			String name = al.getObjectName();
			Datum d = objects.get(name);
			if (d.isGold())
				result++;
		}
		return result;
	}
	
	public Map<String, Object> getInfo(Map<String, Datum> objects, Collection<String> categories){
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("Number of annotations", getAssignedLabels().size());
		ret.put("Gold tests", countGoldTests(objects));
		LinkedList<Map<String, Object>> matrix = new LinkedList<Map<String, Object>>();
		for (String correct_name : categories) {
			for (String assigned_name : categories) {
				Map<String, Object> val = new HashMap<String, Object>();
				val.put("from", correct_name);
				val.put("to", assigned_name);
				double cm_entry = getErrorRateBatch(correct_name, assigned_name);
				String s_cm_entry = Double.isNaN(cm_entry) ? "---" : Utils.round(100 * cm_entry, 3).toString();
				val.put("value", s_cm_entry);
				matrix.add(val);
			}
		}
		ret.put("Confusion matrix", matrix);
		return ret;
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
