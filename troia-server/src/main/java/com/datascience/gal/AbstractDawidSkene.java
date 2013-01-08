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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.datascience.core.storages.JSONUtils;
import com.datascience.gal.decision.DecisionEngine;
import com.datascience.gal.decision.ILabelProbabilityDistributionCalculator;
import com.datascience.gal.decision.LabelProbabilityDistributionCalculators;
import com.datascience.gal.decision.ObjectLabelDecisionAlgorithms;
import com.datascience.utils.Utils;

public abstract class AbstractDawidSkene implements DawidSkene {

	protected Map<String, Datum> objects;
	protected Map<String, Datum> objectsWithNoLabels;
	protected Map<String, Worker> workers;
	protected Map<String, Category> categories;
	protected Map<String, CorrectLabel> evaluationData;

	protected boolean fixedPriors;

	protected final String id;

	protected DecisionEngine mvDecisionEnginge;
	protected ILabelProbabilityDistributionCalculator spammerProbDistr;
	
	/**
	 * Set to true if this project was computed.
	 * Any modification to DS project will set it to false
	 */
	private boolean computed;
	
	private String getAnnotatorCostNaiveStr(Worker w){
		double cost_naive = this.getAnnotatorCostNaive(w);
		return (Double.isNaN(cost_naive)) ? "---" : Utils.round(100 * cost_naive, 2) + "%";
	}

	protected AbstractDawidSkene(String id) {
		this.id = id;
		this.evaluationData = new HashMap<String,CorrectLabel>();
		this.objects = new HashMap<String, Datum>();
		this.workers = new HashMap<String, Worker>();
		this.objectsWithNoLabels = new HashMap<String, Datum>();
		this.computed = false;
		mvDecisionEnginge = new DecisionEngine(
			new LabelProbabilityDistributionCalculators.DS(), null,
			new ObjectLabelDecisionAlgorithms.MaxProbabilityDecisionAlgorithm());
		spammerProbDistr = new LabelProbabilityDistributionCalculators.PriorBased();
	}

	protected void invalidateComputed() {
		this.computed = false;
	}

	protected void markComputed() {
		this.computed = true;
	}
	
	public void initializeOnCategories(Collection<Category> categories){
		this.fixedPriors = false;
		this.categories = new HashMap<String, Category>();

		for (Category c : categories) {
			this.categories.put(c.getName(), c);
			if (c.hasPrior()) {
				this.fixedPriors = true;
			}
		}

		// We initialize the priors to be uniform across classes
		// if the user did not pass any information about the prior values

		if (!fixedPriors)
			initializePriors();
		
		// By default, we initialize the misclassification costs
		// assuming a 0/1 loss function. The costs can be customized
		// using the corresponding file
		initializeCosts();
	}
	
	/**
	 * We initialize the misclassification costs using the 0/1 loss
	 *
	 * @param categories
	 */
	protected void initializeCosts() {

		for (String from : categories.keySet()) {
			for (String to : categories.keySet()) {
				Category c = categories.get(from);
				if (from.equals(to)) {
					c.setCost(to, 0.0);
				} else {
					c.setCost(to, 1.0);
				}
				categories.put(from, c);
			}
		}
		invalidateComputed();
	}

	protected void initializePriors() {

		for (String cat : categories.keySet()) {
			Category c = categories.get(cat);
			c.setPrior(1.0 / categories.keySet().size());
			categories.put(cat, c);
		}
		invalidateComputed();
	}

	protected Double getLogLikelihood() {
		double result = 0;
		for (Datum d : objects.values()) {
			for (AssignedLabel al: d.getAssignedLabels()) {
				String workerName = al.getWorkerName();
				String assignedLabel = al.getCategoryName();
				Map<String, Double> estimatedCorrectLabel =
					d.getCategoryProbability();
				for (String from: estimatedCorrectLabel.keySet()) {
					Worker w = workers.get(workerName);
					Double categoryProbability = estimatedCorrectLabel.get(from);
					Double labelingProbability = getErrorRateForWorker(w, from,
												 assignedLabel);
					if (categoryProbability == 0.0 || labelingProbability ==0.0 ) 
						continue; 
					else
						result += Math.log(categoryProbability) + Math.log(labelingProbability);
				}
			}
		}
		return result;
	}

	protected void validateCategory(String categoryName) {
		if (!categories.containsKey(categoryName)) {
			String message = "attempting to add invalid category: " + categoryName;
			logger.warn(message);
			throw new IllegalArgumentException(message);
		}
	}

	@Override
	public Map<String, String> getInfo() {
		Map<String, String> ret = new HashMap<String, String>();
		ret.put("DS kind", String.valueOf(this.getClass()));
		int a = 0, g = 0;
		for (Datum d : this.objects.values()){
			if (d.isGold())
				g++;
			a += d.getAssignedLabels().size();
		}
		ret.put("Number of assigns", String.valueOf(a));
		ret.put("Number of objects", String.valueOf(this.getNumberOfObjects()));
		ret.put("Number of gold objects", String.valueOf(g));
		ret.put("Number of workers", String.valueOf(this.workers.size()));
		return ret;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return JSONUtils.toJson(this);
	}

	@Override
	public void setFixedPriors(Map<String, Double> priors) {

		this.fixedPriors = true;
		setPriors(priors);
	}

	protected void setPriors(Map<String, Double> priors) {

		for (String c : this.categories.keySet()) {
			Category category = this.categories.get(c);
			Double prior = priors.get(c);
			category.setPrior(prior);
			this.categories.put(c, category);
		}
	}
	
	@Override
	public LinkedList<Map<String, Object>> getAllWorkerScores(boolean detailed) {

		LinkedList<Map<String, Object>> workerScores = new LinkedList<Map<String, Object>>();
		for (String workername : new TreeSet<String>(this.workers.keySet())) {
			Worker w = this.workers.get(workername);
			workerScores.add(getWorkerScore(w, detailed));
		}
		return workerScores;
	}
	
	@Override
	public Map<String, Object> getWorkerScore(Worker w, boolean detailed) {
		String workerName = w.getName();
		String s_cost_naive = this.getAnnotatorCostNaiveStr(w);
//		String s_cost_adj = this.getWorkerCostStr(w, WorkerCostMethod.COST_ADJUSTED);
//		String s_cost_min = this.getWorkerCostStr(w, WorkerCostMethod.COST_ADJUSTED_MINIMIZED);
		int contributions = w.getAssignedLabels().size();
		int gold_tests = countGoldTests(w.getAssignedLabels());

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("Worker", workerName);
		m.put("Error rate", s_cost_naive);
//		m.put("Quality (Expected)", s_cost_adj);
//		m.put("Quality (Optimized)", s_cost_min);
		m.put("Number of Annotations", contributions);
		m.put("Number of Gold Tests", gold_tests);
		if (detailed){
			LinkedList<Map<String, Object>> matrix = new LinkedList<Map<String, Object>>();
			for (String correct_name : this.categories.keySet()) {
				for (String assigned_name : this.categories.keySet()) {
					Map<String, Object> val = new HashMap<String, Object>();
					val.put("from", correct_name);
					val.put("to", assigned_name);
					double cm_entry = getErrorRateForWorker(w, correct_name, assigned_name);
					String s_cm_entry = Double.isNaN(cm_entry) ? "---" : Utils.round(100 * cm_entry, 3).toString();
					val.put("value", s_cm_entry);
					matrix.add(val);
				}
			}
			m.put("Confusion Matrix", matrix);
		}
		return m;
	}

	private int countGoldTests(Set<AssignedLabel> labels) {

		int result = 0;
		for (AssignedLabel al : labels) {
			String name = al.getObjectName();
			Datum d = this.objects.get(name);
			if (d.isGold())
				result++;

		}
		return result;
	}

	@Override
	public int getNumberOfObjects() {
		return this.objects.size();
	}

	@Override
	public int getNumberOfWorkers() {
		return this.workers.size();
	}
	
	@Override
	public int getNumberOfUnassignedObjects() {
		return this.objectsWithNoLabels.size();
	}
	
	@Override
	public double getAnnotatorCostNaive(Worker w) {

		double c = 0.0;
		double s = 0.0;
		for (Category from : this.categories.values()) {
			for (Category to : this.categories.values()) {
				double fromPrior = prior(from.getName());
				c += fromPrior
					 * from.getCost(to.getName())
					 * getErrorRateForWorker(w, from.getName(), to.getName());
				s += fromPrior * from.getCost(to.getName());
			}
		}
		return (s > 0) ? c / s : 0.0;
	}

	@Override
	public boolean fixedPriors() {
		return fixedPriors;
	}

	@Override
	public Map<String, Double> getObjectProbs(String objectName) {
		return getObjectClassProbabilities(objectName);
	}

	@Override
	public Map<String, Map<String, Double>> getObjectProbs() {
		return getObjectProbs(objects.keySet());
	}

	@Override
	public Map<String, Map<String, Double>> getObjectProbs(
		Collection<String> objectNames) {
		Map<String, Map<String, Double>> out = new HashMap<String, Map<String, Double>>(
			objectNames.size());
		for (String objectName : objectNames) {
			out.put(objectName, getObjectProbs(objectName));
		}
		return out;
	}

	@Override
	public void unsetFixedPriors() {

		this.fixedPriors = false;
		updatePriors();
	}

	protected void updatePriors() {

		if (fixedPriors)
			return;

		HashMap<String, Double> priors = new HashMap<String, Double>();
		for (String c : this.categories.keySet()) {
			priors.put(c, 0.0);
		}

		int totalObjects = this.objects.size();
		for (Datum d : this.objects.values()) {
			for (String c : this.categories.keySet()) {
				Double prior = priors.get(c);
				Double objectProb = d.getCategoryProbability(c);
				prior += objectProb / totalObjects;
				priors.put(c, prior);
			}
		}
		setPriors(priors);
		invalidateComputed();
	}

	protected Map<String, Double> getObjectClassProbabilities(String objectName) {
		return getObjectClassProbabilities(objectName, null);
	}

	protected Map<String, Double> getObjectClassProbabilities(
		String objectName, String workerToIgnore) {

		Map<String, Double> result = new HashMap<String, Double>();

		Datum d = this.objects.get(objectName);

		// If this is a gold example, just put the probability estimate to be
		// 1.0
		// for the correct class
		if (d.isGold()) {
			for (String category : this.categories.keySet()) {
				String correctCategory = d.getCorrectCategory();
				if (category.equals(correctCategory)) {
					result.put(category, 1.0);
				} else {
					result.put(category, 0.0);
				}
			}
			return result;
		}

		// Let's check first if we have any workers who have labeled this item,
		// except for the worker that we ignore
		Set<AssignedLabel> labels = new HashSet<AssignedLabel>(
			d.getAssignedLabels());
		if (labels.isEmpty())
			return null;
		if (workerToIgnore != null && labels.size() == 1) {
			for (AssignedLabel al : labels) {
				if (al.getWorkerName().equals(workerToIgnore))
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

		for (Category category : categories.values()) {

			// We estimate now Equation 2.5 of Dawid & Skene
			double categoryNominator = prior(category.getName());

			// We go through all the labels assigned to the d object
			for (AssignedLabel al : d.getAssignedLabels()) {
				Worker w = workers.get(al.getWorkerName());

				// If we are trying to estimate the category probability
				// distribution
				// to estimate the quality of a given worker, then we need to
				// ignore
				// the labels submitted by this worker.
				if (workerToIgnore != null
						&& w.getName().equals(workerToIgnore))
					continue;

				String assigned_category = al.getCategoryName();
				double evidence_for_category = getErrorRateForWorker(w,
					category.getName(), assigned_category);
				if (Double.isNaN(evidence_for_category))
					continue;
				categoryNominator *= evidence_for_category;
			}

			categoryNominators.put(category.getName(), categoryNominator);
			denominator += categoryNominator;
		}

		for (String category : categories.keySet()) {
			double nominator = categoryNominators.get(category);
			if (denominator == 0.0) {
				// result.put(category, 0.0);
				return null;
			} else {
				double probability = Utils.round(nominator / denominator, 5);
				result.put(category, probability);
			}
		}

		return result;

	}

	@Override
	public void addMisclassificationCost(MisclassificationCost cl) {

		String from = cl.getCategoryFrom();
		String to = cl.getCategoryTo();
		Double cost = cl.getCost();

		Category c = this.categories.get(from);
		c.setCost(to, cost);
		this.categories.put(from, c);
		invalidateComputed();
	}

	@Override
	public void addAssignedLabels(Collection<AssignedLabel> als) {
		for (AssignedLabel al : als) {
			addAssignedLabel(al);
		}
	}

	@Override
	public void addCorrectLabels(Collection<CorrectLabel> cls) {
		for (CorrectLabel cl : cls) {
			addCorrectLabel(cl);
		}
	}
	
	@Override
	public void markObjectsAsGold(Collection<CorrectLabel> cls) {
		for (CorrectLabel cl : cls) {
			if (!objects.containsKey(cl.getObjectName()))
				throw new IllegalArgumentException(String.format("{} is not present in objects map", cl.getObjectName()));
		}
		addCorrectLabels(cls);
	}

	@Override
	public void addMisclassificationCosts(Collection<MisclassificationCost> cls) {
		for (MisclassificationCost cl : cls)
			addMisclassificationCost(cl);
	}

	@Override
	public Map<String, Double> objectClassProbabilities(String objectName) {
		return objectClassProbabilities(objectName, 0.);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ipeirotis.gal.DawidSkene#addAssignedLabel(com.ipeirotis.gal.AssignedLabel
	 * )
	 */
	@Override
	public void addAssignedLabel(AssignedLabel al) {

		String workerName = al.getWorkerName();
		String objectName = al.getObjectName();

		String categoryName = al.getCategoryName();
		this.validateCategory(categoryName);
		// If we already have the object, then just add the label
		// in the set of labels for the object.
		// If it is the first time we see the object, then create
		// the appropriate entry in the objects hashmap
		Datum d;
		if (objects.containsKey(objectName)) {
			d = objects.get(objectName);
		} else {
			Set<Category> datumCategories = new HashSet<Category>(
				categories.values());
			d = new Datum(objectName, datumCategories);
		}
		if (objectsWithNoLabels.containsKey(objectName)) {
			objectsWithNoLabels.remove(objectName);
		}
		d.addAssignedLabel(al);
		objects.put(objectName, d);

		// If we already have the worker, then just add the label
		// in the set of labels assigned by the worker.
		// If it is the first time we see the object, then create
		// the appropriate entry in the objects hashmap
		Worker w;
		if (workers.containsKey(workerName)) {
			w = workers.get(workerName);
		} else {
			Set<Category> workerCategories = new HashSet<Category>(
				categories.values());
			w = new Worker(workerName, workerCategories);
		}
		w.addAssignedLabel(al);
		workers.put(workerName, w);
		invalidateComputed();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ipeirotis.gal.DawidSkene#addCorrectLabel(com.ipeirotis.gal.CorrectLabel
	 * )
	 */
	@Override
	public void addCorrectLabel(CorrectLabel cl) {

		String objectName = cl.getObjectName();
		String correctCategory = cl.getCorrectCategory();

		Datum d;
		this.validateCategory(correctCategory);
		if (this.objects.containsKey(objectName)) {
			d = this.objects.get(objectName);
		} else {
			Set<Category> categories = new HashSet<Category>(
				this.categories.values());
			d = new Datum(objectName, categories);
		}
		if (objectsWithNoLabels.containsKey(objectName)) {
			objectsWithNoLabels.remove(objectName);
		}
		d.setGold(true);
		d.setCorrectCategory(correctCategory);
		this.objects.put(objectName, d);
		invalidateComputed();
	}
	
	@Override
	public void addObjects(Collection<String> objs){
		Set<Category> categories = new HashSet<Category>(this.categories.values());
		for (String obj : objs){
			if (!this.objects.containsKey(obj) && !this.objectsWithNoLabels.containsKey(obj)) {
				this.objectsWithNoLabels.put(obj, new Datum(obj, categories));
			}
		}
		invalidateComputed();
	}

	// josh- over ride the proceeding with incremental methods.

	/**
	 * @param w
	 * @param objectCategory
	 * @return
	 */
	protected Map<String, Double> getNaiveSoftLabel(Worker w,
			String objectCategory) {

		HashMap<String, Double> naiveSoftLabel = new HashMap<String, Double>();
		for (String cat : this.categories.keySet()) {
			naiveSoftLabel.put(cat,
							   getErrorRateForWorker(w, objectCategory, cat));
		}
		return naiveSoftLabel;
	}

	/**
	 * Gets as input a "soft label" (i.e., a distribution of probabilities over
	 * classes) and returns the expected cost of this soft label.
	 *
	 * @param p
	 * @return The expected cost of this soft label
	 */
	protected double getNaiveSoftLabelCost(String source,
										   Map<String, Double> destProbabilities) {

		double c = 0.0;
		for (String destination : destProbabilities.keySet()) {
			double p = destProbabilities.get(destination);
			double cost = this.categories.get(source).getCost(destination);
			c += p * cost;
		}

		return c;
	}

	/**
	 * Gets as input a "soft label" (i.e., a distribution of probabilities over
	 * classes) and returns the expected cost of this soft label.
	 *
	 * @param p
	 * @return The expected cost of this soft label
	 */
	private double getSoftLabelCost(Map<String, Double> probabilities) {

		double c = 0.0;
		for (String c1 : probabilities.keySet()) {
			for (String c2 : probabilities.keySet()) {
				double p1 = probabilities.get(c1);
				double p2 = probabilities.get(c2);
				double cost = categories.get(c1).getCost(c2);
				c += p1 * p2 * cost;
			}
		}

		return c;
	}

	public Map<String, Double> getSoftLabelForHardCategoryLabel(Worker w, String label, boolean evaluate) {

		// Pr(c | label) = Pr(label | c) * Pr (c) / Pr(label)

		Map<String, Double> worker_prior = w.getPrior(getCategoryPriors());

		Map<String, Double> result = new HashMap<String, Double>();
		for (Category source : categories.values()) {
			double soft = 0.;
			if (worker_prior.get(label) > 0){
				double error = 0.;
				if (evaluate){
					w.computeEvalConfusionMatrix(evaluationData, categories.values());
					error = w.getEvalErrorRate(source.getName(), label);
				} else
					error = getErrorRateForWorker(w, source.getName(), label);
				soft = prior(source.getName()) * error / worker_prior.get(label);
			}
			result.put(source.getName(), soft);
		}

		return result;
	}

	/**
	 * Gets as input a "soft label" (i.e., a distribution of probabilities over
	 * classes) and returns the smallest possible cost for this soft label.
	 *
	 * @param p
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
				double cost = categories.get(c1).getCost(c2);
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

	public Datum getObject(String object_id) {
		Datum ret = objects.get(object_id);
		if (ret == null)
			return objectsWithNoLabels.get(object_id);
		return ret;
	}

	@Override
	public Map<String,Datum> getObjects() {
		return objects;
	}
	
	public Map<String,Datum> getObjectsWithNoLabels() {
		return objectsWithNoLabels;
	}

	@Override
	public Map<String,Category> getCategories() {
		return categories;
	}

	public Category getCategory(String category) {
		return categories.get(category);
	}
	
	@Override
	public Collection<CorrectLabel> getGoldDatums() {
		Collection<CorrectLabel> ret = new ArrayList<CorrectLabel>();
		for (Datum d : objects.values()){
			if (d.isGold())
				ret.add(new CorrectLabel(d.getName(), d.getCorrectCategory()));
		}
		return ret;
	}

	@Override
	public void addEvaluationDatums(Collection<CorrectLabel> cl) {
		for (CorrectLabel correctLabel : cl) {
			this.evaluationData.put(correctLabel.getObjectName(),correctLabel);
		}
	}
	
	@Override
	public Collection<CorrectLabel> getEvaluationDatums() {
		return this.evaluationData.values();
	}
        
	public CorrectLabel getEvaluationDatum(String name) {
		return this.evaluationData.get(name);
	}

	@Override
	public boolean isComputed() {
		return this.computed;
	}

	@Override
	public Worker getWorker(String name) {
		return this.workers.get(name);
	}
	
	@Override
	public Collection<Worker> getWorkers() {
		return this.workers.values();
	}

	// Log-likelihod stop condition.

	// One pass of the incremental algorithm.
	protected abstract void estimateInner();

	@Override
	public void estimate(int maxIterations) {
		estimate(maxIterations, DEFAULT_EPSILON);
	}

	@Override
	public void estimate(int maxIterations, double epsilon) {
		double prevLogLikelihood = Double.POSITIVE_INFINITY;
		double currLogLikelihood = 0d;
		int iterations = 0;
		for (int i = 0; i < maxIterations && Math.abs(currLogLikelihood -
				prevLogLikelihood) > epsilon; i++, iterations++) {
			prevLogLikelihood = currLogLikelihood;
			estimateInner();
			currLogLikelihood = getLogLikelihood();
		}
		double diffLogLikelihood = Math.abs(currLogLikelihood -
											prevLogLikelihood);
		logger.info("Estimated: performed " + iterations  + " / " +
					maxIterations + " with log-likelihood difference " +
					diffLogLikelihood);
		markComputed();
	}
	

	protected static Logger logger = null; // will be initialized in subclasses
}
