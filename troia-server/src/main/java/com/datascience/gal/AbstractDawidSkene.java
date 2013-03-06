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

import com.datascience.gal.decision.DecisionEngine;
import com.datascience.gal.decision.ILabelProbabilityDistributionCalculator;
import com.datascience.gal.decision.LabelProbabilityDistributionCalculators;
import com.datascience.gal.decision.ObjectLabelDecisionAlgorithms;
import com.datascience.utils.Utils;
import com.google.common.math.DoubleMath;

public abstract class AbstractDawidSkene<T extends WorkerResult> extends Algorithm<String, NominalData, DatumResult, T> {

	protected boolean fixedPriors;
	protected DecisionEngine mvDecisionEnginge;
	protected ILabelProbabilityDistributionCalculator spammerProbDistr;
	
	/**
	 * Set to true if this project was computed.
	 * Any modification to DS project will set it to false
	 */
	private boolean computed = false;
	
	protected AbstractDawidSkene() {
		mvDecisionEnginge = new DecisionEngine(
			new LabelProbabilityDistributionCalculators.DS(), null,
			new ObjectLabelDecisionAlgorithms.MaxProbabilityDecisionAlgorithm());
		spammerProbDistr = new LabelProbabilityDistributionCalculators.PriorBased();
	}
	
	public AbstractDawidSkene(Collection<Category> categories){
		double priorSum = 0.;
		int priorCnt = 0;
		
		if (categories.size() < 2){
			throw new IllegalArgumentException("There should be at least two categories");
		}
		for (Category c : categories) {
			data.addCategory(c);
			if (c.hasPrior()) {
				priorCnt += 1;
				priorSum += c.getPrior();
			}
		}
		if (!(priorCnt == 0 || (priorCnt == categories.size() && DoubleMath.fuzzyEquals(1., priorSum, 1e-6)))){
			throw new IllegalArgumentException(
					"Priors should sum up to 1. or not to be given (therefore we initialize the priors to be uniform across classes)");
		}
		if (priorCnt == 0){
			initializePriors();
		}
		if (priorCnt == categories.size() && DoubleMath.fuzzyEquals(1., priorSum, 1e-6))
			fixedPriors = true;
		
		//set cost matrix values if not provided
		for (Category from : data.getCategories()) {
			for (Category to : data.getCategories()) {
				if (from.getCost(to.getName()) == null){
					from.setCost(to.getName(), from.getName().equals(to.getName()) ? 0. : 1.);
				}
			}
		}
		
		invalidateComputed();
	}

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

	protected abstract double prior(String categoryName);

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

//	protected void validateCategory(String categoryName) {
//		if (!categories.containsKey(categoryName)) {
//			String message = "attempting to add invalid category: " + categoryName;
//			logger.warn(message);
//			throw new IllegalArgumentException(message);
//		}
//	}

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

	protected Map<String, Double> getObjectClassProbabilities(String objectName) {
		return getObjectClassProbabilities(objectName, null);
	}

	protected Map<String, Double> getObjectClassProbabilities(String objectName, String workerToIgnore) {

		Map<String, Double> result = new HashMap<String, Double>();

		LObject<String> d = data.getObject(objectName);

		// If this is a gold example, just put the probability estimate to be
		// 1.0
		// for the correct class
		if (d.isGold()) {
			for (Category c : data.getCategories()) {
				String correctCategory = d.getGoldLabel();
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
		Set<AssignedLabel<String>> labels = data.getAssignsForObject(d);

		if (labels.isEmpty())
			return null;
		if (workerToIgnore != null && labels.size() == 1) {
			for (AssignedLabel al : labels) {
				if (al.getWorker().getName().equals(workerToIgnore))
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
			for (AssignedLabel<String> al : data.getAssignsForObject(d)) {
				Worker w = data.getWorker(al.getWorker().getName());

				// If we are trying to estimate the category probability
				// distribution
				// to estimate the quality of a given worker, then we need to
				// ignore
				// the labels submitted by this worker.
				if (workerToIgnore != null
						&& w.getName().equals(workerToIgnore))
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

//	public Map<String, Double> objectClassProbabilities(String objectName) {
//		return objectClassProbabilities(objectName, 0.);
//	}
	
	// josh- over ride the proceeding with incremental methods.

	/**
	 * @param w
	 * @param objectCategory
	 * @return
	 */
	protected Map<String, Double> getNaiveSoftLabel(Worker<String> w,
			String objectCategory) {

		HashMap<String, Double> naiveSoftLabel = new HashMap<String, Double>();
		for (Category cat : data.getCategories()) {
			naiveSoftLabel.put(cat.getName(),
							   getErrorRateForWorker(w, objectCategory, cat.getName()));
		}
		return naiveSoftLabel;
	}

	/**
	 * Gets as input a "soft label" (i.e., a distribution of probabilities over
	 * classes) and returns the expected cost of this soft label.
	 *
	 * @return The expected cost of this soft label
	 */
	protected double getNaiveSoftLabelCost(String source,
										   Map<String, Double> destProbabilities) {

		double c = 0.0;
		for (String destination : destProbabilities.keySet()) {
			double p = destProbabilities.get(destination);
			Double cost = data.getCategory(source).getCost(destination);
			c += p * cost;
		}

		return c;
	}

	/**
	 * Gets as input a "soft label" (i.e., a distribution of probabilities over
	 * classes) and returns the expected cost of this soft label.
	 *
	 * @return The expected cost of this soft label
	 */
	private double getSoftLabelCost(Map<String, Double> probabilities) {

		double c = 0.0;
		for (String c1 : probabilities.keySet()) {
			for (String c2 : probabilities.keySet()) {
				double p1 = probabilities.get(c1);
				double p2 = probabilities.get(c2);
				Double cost = data.getCategory(c1).getCost(c2);
				c += p1 * p2 * cost;
			}
		}

		return c;
	}

	/**
	 * Gets as input a "soft label" (i.e., a distribution of probabilities over
	 * classes) and returns the smallest possible cost for this soft label.
	 *
	 * @return The expected cost of this soft label
	 */
	private double getMinSoftLabelCost(Map<String, Double> probabilities) {

		double min_cost = Double.NaN;

		for (String c1 : probabilities.keySet()) {
			// So, with probability p1 it belongs to class c1
			// Double p1 = probabilities.get(c1);

			// What is the cost in this case?
			double costfor_c2 = 0.0;
			for (String c2 : probabilities.keySet()) {
				// With probability p2 it actually belongs to class c2
				double p2 = probabilities.get(c2);
				Double cost = data.getCategory(c1).getCost(c2);
				costfor_c2 += p2 * cost;

			}

			if (Double.isNaN(min_cost) || costfor_c2 < min_cost) {
				min_cost = costfor_c2;
			}

		}

		return min_cost;
	}

	/**
	 * Returns the minimum possible cost of a "spammer" worker, who assigns
	 * completely random labels.
	 *
	 * @return The expected cost of a spammer worker
	 */
	public double getMinSpammerCost() {
		Map<String, Double> prior =
			spammerProbDistr.calculateDistribution(null, this);

		return getMinSoftLabelCost(prior);
	}

	/**
	 * Returns the cost of a "spammer" worker, who assigns completely random
	 * labels.
	 *
	 * @return The expected cost of a spammer worker
	 */
	public double getSpammerCost() {
		Map<String, Double> prior =
			spammerProbDistr.calculateDistribution(null, this);
		return getSoftLabelCost(prior);
	}

	public boolean isComputed() {
		return this.computed;
	}

	// Log-likelihod stop condition.

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
