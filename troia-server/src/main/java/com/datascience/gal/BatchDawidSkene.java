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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.datascience.gal.service.JSONUtils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class BatchDawidSkene extends AbstractDawidSkene {

	public static final BatchDawidSkeneDeserializer deserializer = new BatchDawidSkeneDeserializer();

	public BatchDawidSkene(String id, Collection<Category> categories) {
		super(id, categories);
		super.logger = this.logger;
	}
	
	private BatchDawidSkene(String id, Map<String, Datum> objects, Map<String, Datum> objectsWithNoLabels,
							Map<String, Worker> workers, Map<String, Category> categories,
							boolean fixedPriors) {
		this(id, new ArrayList<Category> ());
		this.objects = objects;
		this.objectsWithNoLabels = objectsWithNoLabels;
		this.workers = workers;
		this.categories = categories;
		this.fixedPriors = fixedPriors;
	}

	private Map<String, Double> getCategoryPriors() {
		Map<String, Double> out = new HashMap<String, Double>(categories.size());
		for (Category cat : categories.values())
			out.put(cat.getName(), cat.getPrior());
		return out;
	}

	@Override
	public Map<String, Double> getWorkerPriors(Worker worker) {
		return worker.getPrior(getCategoryPriors());
	}

	@Override
	public double getErrorRateForWorker(Worker worker, String from, String to) {
		return worker.getErrorRateBatch(from, to);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ipeirotis.gal.DawidSkene#objectClassProbabilities(java.lang.String,
	 * double)
	 */
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

	private void updateObjectClassProbabilities() {

		for (String objectName : this.objects.keySet()) {
			this.updateObjectClassProbabilities(objectName);
		}
	}

	private void updateObjectClassProbabilities(String objectName) {

		Datum d = objects.get(objectName);
		Map<String, Double> probabilities = getObjectClassProbabilities(
												objectName, null);
		if (probabilities == null)
			return;
		for (String category : probabilities.keySet()) {
			double probability = probabilities.get(category);
			d.setCategoryProbability(category, probability);
		}
	}

	private void rebuildWorkerConfusionMatrices() {

		for (String workerName : this.workers.keySet()) {
			rebuildWorkerConfusionMatrix(workerName);
		}
	}

	/**
	 * @param lid
	 */
	private void rebuildWorkerConfusionMatrix(String workerName) {

		Worker w = this.workers.get(workerName);
		w.empty();

		// Scan all objects and change the confusion matrix for each worker
		// using the class probability for each object
		for (AssignedLabel al : w.getAssignedLabels()) {

			// Get the name of the object and the category it
			// is classified from this worker.
			String objectName = al.getObjectName();
			String destination = al.getCategoryName();
			// We get the classification of the object
			// based on the votes of all the other workers
			// We treat this classification as the "correct" one
			Map<String, Double> probabilities = this
												.getObjectClassProbabilities(objectName, workerName);
			if (probabilities == null)
				continue; // No other worker labeled the object

			for (String source : probabilities.keySet()) {
				double error = probabilities.get(source);
				w.addError(source, destination, error);
			}
		}
//        System.out.println("before: "
//                + ((MultinomialConfusionMatrix) w.cm).rowDenominator);
		w.normalize(ConfusionMatrixNormalizationType.UNIFORM);
////        System.out.println("after: "
//                + ((MultinomialConfusionMatrix) w.cm).rowDenominator);

	}

	@Override
	protected void estimateInner() {
		updateObjectClassProbabilities();
		updatePriors();
		rebuildWorkerConfusionMatrices();
	}

	public static class BatchDawidSkeneDeserializer implements
		JsonDeserializer<BatchDawidSkene> {

		@Override
		public BatchDawidSkene deserialize(JsonElement json, Type type,
										   JsonDeserializationContext context) throws JsonParseException {
			JsonObject jobject = (JsonObject) json;

			String id = jobject.get("id").getAsString();
			Map<String, Category> categories = JSONUtils.gson.fromJson(
					jobject.get("categories"), JSONUtils.stringCategoryMapType);
			boolean fixedPriors = jobject.get("fixedPriors").getAsBoolean();
			Map<String, Datum> objects = JSONUtils.gson.fromJson(
					jobject.get("objects"), JSONUtils.stringDatumMapType);
			Map<String, Datum> objectsWithNoLabels = JSONUtils.gson.fromJson(
					jobject.get("objectsWithNoLabels"), JSONUtils.stringDatumMapType);
			Map<String, Worker> workers = JSONUtils.gson.fromJson(
					jobject.get("workers"), JSONUtils.strinWorkerMapType);

			return new BatchDawidSkene(id, objects, objectsWithNoLabels, workers, categories,
									   fixedPriors);
		}

	}

	private static final Logger logger = Logger.getLogger(BatchDawidSkene.class);
}
