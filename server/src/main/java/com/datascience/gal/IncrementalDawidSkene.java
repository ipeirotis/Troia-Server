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

import org.apache.log4j.Logger;

import com.datascience.gal.service.JSONUtils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 * fully incremental version of DS- adjustments to the data structures are made
 * when new information becomes available, not waiting for an explicit
 * estimation step
 *
 * TODO: batch updates TODO: override addAssignedLabels and addCorrectLabels
 *
 * @author josh
 *
 */
public class IncrementalDawidSkene extends AbstractDawidSkene {
	public static final IncrementalDawidSkeneDeserializer deserializer = new IncrementalDawidSkeneDeserializer();

	private IncrementalDSMethod dsmethod = IncrementalDSMethod.UPDATEWORKERS;
	private double priorDenominator;

	public IncrementalDawidSkene(String id, Collection<Category> categories,
								 IncrementalDSMethod dsmethod) {
		this(id, categories);
		this.dsmethod = dsmethod;
	}

	public IncrementalDawidSkene(String id, Collection<Category> categories) {
		super(id);
		objects = new HashMap<String, Datum>();
		workers = new HashMap<String, Worker>();

		fixedPriors = false;
		this.categories = new HashMap<String, Category>();

		for (Category c : categories) {
			this.categories.put(c.getName(), c);
			if (c.hasPrior())
				this.fixedPriors = true;
		}

		if (!fixedPriors)
			initializePriors();

		initializeCosts();
		super.logger = this.logger;
	}

	private IncrementalDawidSkene(String id, Map<String, Datum> objects,
								  Map<String, Worker> workers, Map<String, Category> categories,
								  boolean fixedPriors, IncrementalDSMethod dsmethod,
								  double priorDenominator) {
		super(id);
		this.objects = objects;
		this.workers = workers;
		this.categories = categories;
		this.fixedPriors = fixedPriors;
		this.dsmethod = dsmethod;
		this.priorDenominator = priorDenominator;
	}

	@Override
	protected void initializePriors() {
		for (String cat : categories.keySet()) {
			Category c = categories.get(cat);
			c.setPrior(0);
		}
		priorDenominator = 0;
	}

	@Override
	public double prior(String categoryName) {
		if (fixedPriors || priorDenominator == 0)
			return 1. / (double) categories.size();
		else
			return categories.get(categoryName).getPrior() / priorDenominator;
	}

	@Override
	public void addAssignedLabel(AssignedLabel al) {
		switch (dsmethod) {
		case ITERATELOCAL:
			iterateLocalLablel(al);
			break;
		case UPDATEWORKERS:
		default:
			updateWorkersWithAssignedLabel(al);
			break;
		}
		invalidateComputed();
	}

	private void iterateLocalLablel(AssignedLabel al) {
		Datum d = coreAssignedLabelUpdate(al);
		for (int i = 0; i < 5; i++)
			updateObjectInformation(d, 0 != i);
	}

	private void updateWorkersWithAssignedLabel(AssignedLabel al) {
		Datum d = coreAssignedLabelUpdate(al);
		updateObjectInformation(d, false);
	}

	private Datum coreAssignedLabelUpdate(AssignedLabel al) {
		String workerName = al.getWorkerName();
		String objectName = al.getObjectName();

		String categoryName = al.getCategoryName();
		this.validateCategory(categoryName);
		// If we already have the object, un-update it's prevous contribution to
		// the prior,
		// and remove the previous contribution to the workers confusion
		// matricies

		Datum d;
		if (objects.containsKey(objectName)) {
			unupdatePrior(objectName);
			unupdateWorkers(objectName);
			d = objects.get(objectName);
		} else {
			Set<Category> datumCategories = new HashSet<Category>(
				categories.values());
			d = new Datum(objectName, datumCategories);
		}
		d.addAssignedLabel(al);
		objects.put(objectName, d);

		// If we already have the worker, then just add the label
		// in the set of labels assigned by the worker.
		// If it is the first time we see the object, then create
		// the appropriate entry in the objects hashmap
		Worker w;
		if (this.workers.containsKey(workerName)) {
			w = this.workers.get(workerName);
		} else {
			Set<Category> categories = new HashSet<Category>(
				this.categories.values());
			w = new Worker(workerName, categories);
		}
		w.addAssignedLabel(al);
		workers.put(workerName, w);
		return d;
	}

	private void updateObjectInformation(Datum d, boolean unupdate) {
		String objectName = d.name;
		if (unupdate) {
			unupdatePrior(objectName);
			unupdateWorkers(objectName);
		}
		d.categoryProbability = null;
		d.categoryProbability = getObjectClassProbabilities(objectName);

		incrementPrior(objectName);
		updateWorkers(objectName);
	}

	@Override
	public void addCorrectLabel(CorrectLabel cl) {

		switch (dsmethod) {
		case ITERATELOCAL:
			iterateLocalCorrectLablel(cl);
			break;
		case UPDATEWORKERS:
		default:
			updateWorkersWithCorrectLabel(cl);
			break;
		}

	}

	private void iterateLocalCorrectLablel(CorrectLabel cl) {
		Datum d = coreCorrectLabelUpdate(cl);
		for (int i = 0; i < 5; i++)
			updateObjectInformation(d, 0 != i);
	}

	/**
	 * TODO: less gets- pass the datum object to methods.
	 *
	 */
	private void updateWorkersWithCorrectLabel(CorrectLabel cl) {

		Datum d = coreCorrectLabelUpdate(cl);
		updateObjectInformation(d, false);
	}

	private Datum coreCorrectLabelUpdate(CorrectLabel cl) {
		String objectName = cl.getObjectName();
		String correctCategory = cl.getCorrectCategory();

		Datum d;
		this.validateCategory(correctCategory);
		if (objects.containsKey(objectName)) {
			unupdatePrior(objectName);
			unupdateWorkers(objectName);
			d = objects.get(objectName);
			d.setGold(true);
			d.setCorrectCategory(correctCategory);
		} else {
			Set<Category> categories = new HashSet<Category>(
				this.categories.values());
			d = new Datum(objectName, categories);
			d.setGold(true);
			d.setCorrectCategory(correctCategory);
		}
		objects.put(objectName, d);
		return d;
	}

	private void updateWorkers(String objectName) {
		Datum datum = objects.get(objectName);
		for (AssignedLabel al : datum.getAssignedLabels()) {
			String destination = al.getCategoryName();
			String workerName = al.getWorkerName();
			updateWorker(objectName, workerName, destination);
		}

	}

	private void updateWorker(String objectName, String workerName,
							  String destination) {
		Map<String, Double> probs = getObjectClassProbabilities(objectName);
		Worker worker = workers.get(workerName);

		for (String source : probs.keySet())
			worker.addError(source, destination, probs.get(source));
	}

	private void unupdateWorkers(String objectName) {
		Datum datum = objects.get(objectName);
		for (AssignedLabel al : datum.getAssignedLabels()) {
			String destination = al.getCategoryName();
			String workerName = al.getWorkerName();
			unupdateWorker(objectName, workerName, destination);
		}
	}

	private void unupdateWorker(String objectName, String workerName,
								String destination) {
		Map<String, Double> probs = getObjectClassProbabilities(objectName);
		Worker worker = workers.get(workerName);

		for (String source : probs.keySet())
			worker.removeError(source, destination, probs.get(source));
	}

	/**
	 * removes information to the priors
	 *
	 * @param objectName
	 */
	private void unupdatePrior(String objectName) {
		priorDenominator--;
		Map<String, Double> probs = getObjectClassProbabilities(objectName);

		for (String catName : categories.keySet()) {
			double prior = probs.get(catName);
			Category category = categories.get(catName);
			double oldValue = category.getPrior();
			category.setPrior(Math.max(0, oldValue - prior));
		}
	}

	/**
	 * increases the prior using the object's probs
	 *
	 * @param objectName
	 */
	private void incrementPrior(String objectName) {
		priorDenominator++;
		Map<String, Double> probs = getObjectClassProbabilities(objectName);
		for (String catName : categories.keySet()) {
			double prior = probs.get(catName);
			Category category = categories.get(catName);
			double oldValue = category.getPrior();
			category.setPrior(Math.max(0, oldValue + prior));
		}
	}

	@Override
	public Map<String, Double> objectClassProbabilities(String objectName,
			double entropyThreshold) {

		Map<String, Double> out = new HashMap<String, Double>();

		if (!objects.containsKey(objectName)) {
			logger.warn("attempting to get class probabilities for non-existent object");
		} else {
			Datum datum = objects.get(objectName);
			if (datum.getEntropy() >= entropyThreshold) {
				for (String cat : categories.keySet())
					out.put(cat, datum.getCategoryProbability(cat));
			}
		}
		return out;
	}

	@Override
	public Map<String, Double> computePriors() {
		Map<String, Double> out = new HashMap<String, Double>(categories.size());
		for (String catName : categories.keySet())
			out.put(catName, prior(catName));
		return out;
	}

	@Override
	public Map<String, Double> getWorkerPriors(Worker worker) {
		return worker.getPrior(computePriors());
	}

	@Override
	public double getErrorRateForWorker(Worker worker, String from, String to) {
		return worker.getErrorRateIncremental(from, to,
											  ConfusionMatrixNormalizationType.UNIFORM);
	}

	@Override
	public Map<String, Double> getObjectClassProbabilities(String objectName,
			String workerToIgnore) {
		Datum datum = objects.get(objectName);
		if (null != datum.categoryProbability)
			return datum.categoryProbability;
		else
			return super
				   .getObjectClassProbabilities(objectName, workerToIgnore);
	}

	@Override
	protected void estimateInner() {
		for (Datum d : objects.values())
			updateObjectInformation(d, true);
	}

	public static class IncrementalDawidSkeneDeserializer implements
		JsonDeserializer<IncrementalDawidSkene> {

		@Override
		public IncrementalDawidSkene deserialize(JsonElement json, Type type,
				JsonDeserializationContext context) throws JsonParseException {
			JsonObject jobject = (JsonObject) json;

			String id = jobject.get("id").getAsString();
			Map<String, Category> categories = JSONUtils.gson.fromJson(
												   jobject.get("categories"), JSONUtils.stringCategoryMapType);
			boolean fixedPriors = jobject.get("fixedPriors").getAsBoolean();
			Map<String, Datum> objects = JSONUtils.gson.fromJson(
											 jobject.get("objects"), JSONUtils.stringDatumMapType);
			Map<String, Worker> workers = JSONUtils.gson.fromJson(
											  jobject.get("workers"), JSONUtils.strinWorkerMapType);
			IncrementalDSMethod dsmethod = IncrementalDSMethod.valueOf(jobject
										   .get("dsmethod").getAsString());
			double priorDenominator = jobject.get("priorDenominator")
									  .getAsDouble();

			return new IncrementalDawidSkene(id, objects, workers, categories,
											 fixedPriors, dsmethod, priorDenominator);
		}
	}

	private static final Logger logger = Logger.getLogger(IncrementalDawidSkene.class);
}
