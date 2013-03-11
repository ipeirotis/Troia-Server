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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.datascience.core.base.*;
import org.apache.log4j.Logger;

import com.datascience.utils.Utils;

public abstract class AbstractDawidSkene extends Algorithm<String, NominalData, DatumResult, WorkerResult> {

	protected boolean fixedPriors;

	/**
	 * Set to true if this project was computed.
	 * Any modification to DS project will set it to false
	 */
	private boolean computed = false;
	
	protected void invalidateComputed() {
		this.computed = false;
	}

	protected void markComputed() {
		this.computed = true;
	}

	protected void initializePriors() {
		for (Category c : data.getCategories())
			c.setPrior(1. / data.getCategories().size());
	}

	protected double getErrorRateForWorker(Worker<String> worker, String from, String to){
		return results.getWokerResults().get(worker).getErrorRate(from, to);
	}

	public abstract double prior(String categoryName);

	@Override
	protected double getLogLikelihood() {
		double result = 0;
		for (AssignedLabel<String> al : data.getAssigns()){
			Map<String, Double> estimatedCorrectLabel = results.getDatumResults().get(al.getLobject()).getCategoryProbabilites();
			for (Map.Entry<String, Double> e: estimatedCorrectLabel.entrySet()){
				Double labelingProbability = getErrorRateForWorker(al.getWorker(), e.getKey(), al.getLabel());
				if (e.getValue() == 0. || Double.isNaN(labelingProbability) || labelingProbability == 0.)
					continue;
				else
					result += Math.log(e.getValue()) + Math.log(labelingProbability);

			}
		}
		return result;
	}

	protected void setPriors(Map<String, Double> priors) {
		for (Category c : data.getCategories()){
			c.setPrior(priors.get(c.getName()));
		}
	}

//	@Override
//	public boolean fixedPriors() {
//		return fixedPriors;
//	}

//OBJECT PROBS
//	@Override
//	public Map<String, Double> getObjectProbs(String objectName) {
//		return getObjectClassProbabilities(objectName);
//	}
//
//	@Override
//	public Map<String, Map<String, Double>> getObjectProbs() {
//		return getObjectProbs(objects.keySet());
//	}
//
//	@Override
//	public Map<String, Map<String, Double>> getObjectProbs(
//		Collection<String> objectNames) {
//		Map<String, Map<String, Double>> out = new HashMap<String, Map<String, Double>>(
//			objectNames.size());
//		for (String objectName : objectNames) {
//			out.put(objectName, getObjectProbs(objectName));
//		}
//		return out;
//	}

//	@Override
//	public void unsetFixedPriors() {
//
//		this.fixedPriors = false;
//		updatePriors();
//	}

	protected double getEntropyForObject(LObject<String> obj){
		DatumResult result = results.getDatumResults().get(obj);
		double[] p = new double[result.getCategoryProbabilites().size()];

		int i = 0;
		for (Map.Entry<String, Double> e : result.getCategoryProbabilites().entrySet()) {
			p[i] = e.getValue();
			i++;
		}
		return Utils.entropy(p);
	}

	protected void updatePriors() {
		if (fixedPriors)
			return;

		HashMap<String, Double> priors = new HashMap<String, Double>();
		for (Category c : data.getCategories()) {
			priors.put(c.getName(), 0.0);
		}

		for (LObject<String> obj : data.getObjects()){
			for (Category c : data.getCategories()){
				priors.put(c.getName(), priors.get(c.getName()) +
						results.getDatumResults().get(obj).getCategoryProbability(c.getName()) / data.getObjects().size());
			}
		}

		setPriors(priors);
		invalidateComputed();
	}

	protected Map<String, Double> getObjectClassProbabilities(LObject<String> object) {
		return getObjectClassProbabilities(object, null);
	}

	protected Map<String, Double> getObjectClassProbabilities(LObject<String> object, Worker<String> workerToIgnore) {
		Map<String, Double> result = new HashMap<String, Double>();

		// If this is a gold example, just put the probability estimate to be
		// 1.0
		// for the correct class
		if (object.isGold()) {
			for (Category c : data.getCategories()) {
				String correctCategory = object.getGoldLabel();
				if (c.getName().equals(correctCategory)) {
					result.put(c.getName(), 1.0);
				} else {
					result.put(c.getName(), 0.0);
				}
			}
			return result;
		}

		// Let's check first if we have any workers who have labeled this item,
		// except for the worker that we ignore
		Set<AssignedLabel<String>> labels = data.getAssignsForObject(object);

		if (labels.isEmpty())
			return null;
		if (workerToIgnore != null && labels.size() == 1) {
			for (AssignedLabel al : labels) {
				if (al.getWorker().equals(workerToIgnore))
					// if only the ignored labeler has labeled
					return null;
			}
		}

		// If it is not gold, then we proceed to estimate the class
		// probabilities using the method of Dawid and Skene and we proceed as
		// usual with the M-phase of the EM-algorithm of Dawid&Skene

		// Estimate denominator for Eq 2.5 of Dawid&Skene, which is the same
		// across all categories
		double denominator = 0.0;

		// To compute the denominator, we also compute the nominators across
		// all categories, so it saves us time to save the nominators as we
		// compute them
		Map<String, Double> categoryNominators = new HashMap<String, Double>();

		for (Category category : data.getCategories()) {

			// We estimate now Equation 2.5 of Dawid & Skene
			double categoryNominator = prior(category.getName());

			// We go through all the labels assigned to the d object
			for (AssignedLabel<String> al : data.getAssignsForObject(object)) {
				Worker<String> w = al.getWorker();

				// If we are trying to estimate the category probability
				// distribution
				// to estimate the quality of a given worker, then we need to
				// ignore
				// the labels submitted by this worker.
				if (workerToIgnore != null
						&& w.equals(workerToIgnore))
					continue;

				String assigned_category = al.getLabel();
				double evidence_for_category = getErrorRateForWorker(w,
					category.getName(), assigned_category);
				if (Double.isNaN(evidence_for_category))
					continue;
				categoryNominator *= evidence_for_category;
			}

			categoryNominators.put(category.getName(), categoryNominator);
			denominator += categoryNominator;
		}

		for (Category c : data.getCategories()) {
			double nominator = categoryNominators.get(c.getName());
			if (denominator == 0.0) {
				// result.put(category, 0.0);
				return null;
			} else {
				double probability = Utils.round(nominator / denominator, 5);
				result.put(c.getName(), probability);
			}
		}

		return result;

	}

	public void addMisclassificationCost(MisclassificationCost cl) {
		data.getCategory(cl.getCategoryFrom()).setCost(cl.getCategoryTo(), cl.getCost());
		invalidateComputed();
	}
	

	public void addMisclassificationCosts(Collection<MisclassificationCost> cls) {
		for (MisclassificationCost cl : cls)
			addMisclassificationCost(cl);
	}

	public boolean isComputed() {
		return this.computed;
	}

	// One pass of the incremental algorithm.
	protected abstract void estimateInner();

	@Override
	public double estimate(double epsilon, int maxIterations) {
		double prevLogLikelihood = Double.POSITIVE_INFINITY;
		double currLogLikelihood = 0d;
		int iteration = 0;
		for (;iteration < maxIterations && Math.abs(currLogLikelihood -
				prevLogLikelihood) > epsilon; iteration++) {
			prevLogLikelihood = currLogLikelihood;
			estimateInner();
			currLogLikelihood = getLogLikelihood();
		}
		double diffLogLikelihood = Math.abs(currLogLikelihood - prevLogLikelihood);
		logger.info("Estimated: performed " + iteration  + " / " +
					maxIterations + " with log-likelihood difference " +
					diffLogLikelihood);
		markComputed();

		return 0;
	}
	
	protected static Logger logger = null; // will be initialized in subclasses
}
