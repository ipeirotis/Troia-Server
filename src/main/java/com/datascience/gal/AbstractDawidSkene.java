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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.datascience.gal.service.JSONUtils;
import com.datascience.utils.Utils;

public abstract class AbstractDawidSkene implements DawidSkene {
	protected static Logger logger = Logger.getLogger(DawidSkene.class);

	protected Map<String, Datum> objects;
	protected Map<String, Worker> workers;
	protected Map<String, Category> categories;
	protected Map<String,CorrectLabel> evaluationData;

	protected boolean fixedPriors;

	protected final String id;
	
	protected double quality;

	protected AbstractDawidSkene(String id) {
		this.id = id;
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
	}

	protected void initializePriors() {

		for (String cat : categories.keySet()) {
			Category c = categories.get(cat);
			c.setPrior(1.0 / categories.keySet().size());
			categories.put(cat, c);
		}
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return JSONUtils.gson.toJson(this);
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
	public String printPriors() {

		StringBuilder sb = new StringBuilder();
		for (Category c : this.categories.values()) {
			sb.append("Prior[" + c.getName() + "]=" + prior(c.getName()) + "\n");
		}
		return sb.toString();
	}

	@Override
	public String printObjectClassProbabilities(double entropy_threshold) {

		StringBuilder sb = new StringBuilder();
		sb.append("Object\t");
		for (String c : this.categories.keySet()) {
			sb.append("Pr[" + c + "]\t");
		}
		// TODO: Also print majority label and the min-cost label, pre-DS and
		// post-DS
		sb.append("Pre-DS Majority Label\tPre-DS Min Cost Label\tPost-DS Majority Label\tPost-DS Min Cost Label\n");

		for (String object_name : new TreeSet<String>(this.objects.keySet())) {
			Datum d = this.objects.get(object_name);

			double entropy = d.getEntropy();
			if (entropy < entropy_threshold)
				continue;

			sb.append(object_name + "\t");
			for (String c : this.categories.keySet()) {
				sb.append(d.getCategoryProbability(c) + "\t");
			}
			sb.append("\n");
		}

		return sb.toString();

	}

	@Override
	public String printWorkerScore(Worker w, boolean detailed) {

		StringBuilder sb = new StringBuilder();
		String workerName = w.getName();

		double cost_naive = this.getAnnotatorCostNaive(w);
		String s_cost_naive = (Double.isNaN(cost_naive)) ? "---" : Utils.round(
								  100 * cost_naive, 2) + "%";

		double cost_adj = this.getWorkerCost(w, WorkerCostMethod.COST_ADJUSTED);
		String s_cost_adj = (Double.isNaN(cost_adj)) ? "---" : Math
							.round(100 * (1 - cost_adj)) + "%";

		double cost_min = this.getWorkerCost(w,
											 WorkerCostMethod.COST_ADJUSTED_MINIMIZED);
		String s_cost_min = (Double.isNaN(cost_min)) ? "---" : Math
							.round(100 * (1 - cost_min)) + "%";

		int contributions = w.getAssignedLabels().size();
		int gold_tests = countGoldTests(w.getAssignedLabels());

		if (detailed) {
			sb.append("Worker: " + workerName + "\n");
			sb.append("Error Rate: " + s_cost_naive + "\n");
			sb.append("Quality (Expected): " + s_cost_adj + "\n");
			sb.append("Quality (Optimized): " + s_cost_min + "\n");
			sb.append("Number of Annotations: " + contributions + "\n");
			sb.append("Number of Gold Tests: " + gold_tests + "\n");

			sb.append("Confusion Matrix: \n");
			for (String correct_name : this.categories.keySet()) {
				for (String assigned_name : this.categories.keySet()) {
					double cm_entry = getErrorRateForWorker(w, correct_name,
															assigned_name);
					String s_cm_entry = Double.isNaN(cm_entry) ? "---" : Utils
										.round(100 * cm_entry, 3).toString();
					sb.append("P[" + correct_name + "->" + assigned_name + "]="
							  + s_cm_entry + "%\t");
				}
				sb.append("\n");
			}
			sb.append("\n");
		} else {
			sb.append(workerName + "\t" + s_cost_naive + "\t" + s_cost_adj
					  + "\t" + s_cost_min + "\t" + contributions + "\t"
					  + gold_tests + "\n");
		}

		return sb.toString();
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
	public String printVote() {

		StringBuilder sb = new StringBuilder();

		Map<String, String> vote = getMajorityVote();

		for (String obj : (new TreeSet<String>(vote.keySet()))) {
			String majority_vote = vote.get(obj);
			sb.append(obj + "\t" + majority_vote + "\n");
		}
		return sb.toString();
	}

	@Override
	public String printAllWorkerScores(boolean detailed) {

		StringBuilder sb = new StringBuilder();

		if (!detailed) {
			sb.append("Worker\tError Rate\tQuality (Expected)\tQuality (Optimized)\tNumber of Annotations\tGold Tests\n");
		}
		for (String workername : new TreeSet<String>(this.workers.keySet())) {
			Worker w = this.workers.get(workername);
			sb.append(printWorkerScore(w, detailed));
		}
		return sb.toString();
	}

	@Override
	public String printDiffVote(Map<String, String> prior_voting,
								Map<String, String> posterior_voting) {

		StringBuilder sb = new StringBuilder();

		for (String obj : (new TreeSet<String>(prior_voting.keySet()))) {
			String prior_vote = prior_voting.get(obj);
			String posterior_vote = posterior_voting.get(obj);

			if (prior_vote.equals(posterior_vote)) {
				sb.append("SAME\t" + obj + "\t" + prior_vote);
			} else {
				sb.append("DIFF\t" + obj + "\t" + prior_vote + "->"
						  + posterior_vote);
			}
			sb.append("\n");
		}
		return sb.toString();
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
	public Map<String, String> getMajorityVote() {

		Map<String, String> result = new HashMap<String, String>();

		for (String objectName : this.objects.keySet()) {
			Datum d = this.objects.get(objectName);
			String category = d.getMajorityCategory();
			result.put(objectName, category);
		}
		return result;
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
	public String getMajorityVote(String objectName) {

		if (!objects.containsKey(objectName)) {
			logger.warn("attempting to get majority vote label for a non-existant entity: "
						+ objectName);
			return null;

		} else {
			return objects.get(objectName).getMajorityCategory();
		}
	}

	@Override
	public Map<String, String> getMajorityVote(Collection<String> objectNames) {
		Map<String, String> out = new HashMap<String, String>(
			objectNames.size());
		for (String objectName : objectNames) {
			out.put(objectName, getMajorityVote(objectName));
		}
		return out;
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
	}

	protected Map<String, Double> getObjectClassProbabilities(String objectName) {
		return getObjectClassProbabilities(objectName, null);
	}

	@Override
	public double getErrorRateForWorker(Worker worker, String from, String to) {
		return worker.getErrorRateBatch(from, to);
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

	}

	@Override
	public Map<String, Double> computePriors() {
		Map<String, Double> out = new HashMap<String, Double>();
		for (Category cat : categories.values())
			out.put(cat.getName(), prior(cat.getName()));
		return out;
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
	public void addMisclassificationCosts(Collection<MisclassificationCost> cls) {
		for (MisclassificationCost cl : cls)
			addMisclassificationCost(cl);
	}

	@Override
	public Map<String, Double> objectClassProbabilities(String objectName) {
		return objectClassProbabilities(objectName, 0.);
	}

	@Override
	public double prior(String categoryName) {
		return categories.get(categoryName).getPrior();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ipeirotis.gal.DawidSkene#addAssignedLabel(com.ipeirotis.gal.AssignedLabel
	 * )
	 */
	public void addAssignedLabel(AssignedLabel al) {

		String workerName = al.getWorkerName();
		String objectName = al.getObjectName();

		String categoryName = al.getCategoryName();
		if (!categories.containsKey(categoryName)) {
			logger.warn("attempting ot add invalid category: " + categoryName);
			return;
		}

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

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ipeirotis.gal.DawidSkene#addCorrectLabel(com.ipeirotis.gal.CorrectLabel
	 * )
	 */
	public void addCorrectLabel(CorrectLabel cl) {

		String objectName = cl.getObjectName();
		String correctCategory = cl.getCorrectCategory();

		Datum d;
		if (this.objects.containsKey(objectName)) {
			d = this.objects.get(objectName);
			d.setGold(true);
			d.setCorrectCategory(correctCategory);
		} else {
			Set<Category> categories = new HashSet<Category>(
				this.categories.values());
			d = new Datum(objectName, categories);
			d.setGold(true);
			d.setCorrectCategory(correctCategory);
		}
		this.objects.put(objectName, d);
	}

	@Override
	public double getWorkerCost(Worker w, WorkerCostMethod method) {

		double cost = 0.0;

		// We estimate first how often the worker assigns each category label

		// If we do not have a fixed prior, we can just use the data about the
		// worker
		// TODO: josh: problem with worker priors
		Map<String, Double> worker_prior = getWorkerPriors(w);

		// We now know the frequency with which we will see a label
		// "assigned_label" from worker
		// Each of this "hard" labels from the annotator k will corresponds to a
		// corrected
		// "soft" label
		for (Category assigned : this.categories.values()) {
			// Let's find the soft label that corresponds to assigned_label
			String assignedCategory = assigned.getName();

			if (method == WorkerCostMethod.COST_NAIVE) {
				// TODO: Check this for correctness. Compare results wth tested
				// implementation first
				Map<String, Double> naiveSoftLabel = getNaiveSoftLabel(w,
													 assignedCategory);
				cost += getNaiveSoftLabelCost(assigned.getName(),
											  naiveSoftLabel) * prior(assignedCategory);
			} else if (method == WorkerCostMethod.COST_NAIVE_MINIMIZED) {
				// TODO: Check this for correctness. Compare results wth tested
				// implementation first
				Map<String, Double> naiveSoftLabel = getNaiveSoftLabel(w,
													 assignedCategory);
				cost += getNaiveSoftLabelCost(assigned.getName(),
											  naiveSoftLabel) * prior(assignedCategory);
			} else if (method == WorkerCostMethod.COST_ADJUSTED) {
				Map<String, Double> softLabel = getSoftLabelForHardCategoryLabel(
													w, assignedCategory);
				cost += getSoftLabelCost(softLabel)
						* worker_prior.get(assignedCategory);
			} else if (method == WorkerCostMethod.COST_ADJUSTED_MINIMIZED) {
				Map<String, Double> softLabel = getSoftLabelForHardCategoryLabel(
													w, assignedCategory);
				cost += getMinSoftLabelCost(softLabel)
						* worker_prior.get(assignedCategory);
			} else {
				// We should never reach this
				System.err.println("Error: Incorrect method for cost");
			}

			// And add the cost of this label, weighted with the prior of seeing
			// this label.

		}

		if (method == WorkerCostMethod.COST_NAIVE
				|| method == WorkerCostMethod.COST_NAIVE_MINIMIZED) {
			return cost;
		} else if (method == WorkerCostMethod.COST_ADJUSTED) {
			return cost / getSpammerCost();
		} else if (method == WorkerCostMethod.COST_ADJUSTED_MINIMIZED) {
			return cost / getMinSpammerCost();
		} else {
			// We should never reach this
			System.err
			.println("Error: We should have never reached this in getWorkerCost");
			return Double.NaN;
		}

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

	private Map<String, Double> getSoftLabelForHardCategoryLabel(Worker w,
			String label) {

		// Pr(c | label) = Pr(label | c) * Pr (c) / Pr(label)

		Map<String, Double> worker_prior = getWorkerPriors(w);

		Map<String, Double> result = new HashMap<String, Double>();
		for (Category source : categories.values()) {
			double error = getErrorRateForWorker(w, source.getName(), label);
			double soft = prior(source.getName()) * error
						  / worker_prior.get(label);
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
	private double getMinSpammerCost() {

		HashMap<String, Double> prior = new HashMap<String, Double>();
		for (Category c : this.categories.values()) {
			prior.put(c.getName(), prior(c.getName()));
		}
		return getMinSoftLabelCost(prior);
	}

	/**
	 * Returns the cost of a "spammer" worker, who assigns completely random
	 * labels.
	 *
	 * @return The expected cost of a spammer worker
	 */
	private double getSpammerCost() {

		Map<String, Double> prior = new HashMap<String, Double>();
		for (Category c : categories.values()) {
			prior.put(c.getName(), prior(c.getName()));
		}
		return getSoftLabelCost(prior);
	}

	public Datum getObject(String object_id) {
		return objects.get(object_id);
	}

	public Collection<Datum> getObjects() {
		return objects.values();
	}

	public Collection<Category> getCategories() {
		return categories.values();
	}

	public Category getCategory(String category) {
		return categories.get(category);
	}
	
	/**
	 * @return the quality
	 */
	public double getQuality() {
		return quality;
	}

	/**
	 * This function calculates quality of this project and it's workers quality.
	 */
	public void computeProjectQuality(){
		int correctEvaluationLabels = 0;
		int totalEvaluationLabels = 0;
		
		//Calculating quality for whole project
		for (String evaluationObjectName : this.evaluationData.keySet()) {
			CorrectLabel correctLabel = this.evaluationData.get(evaluationObjectName); 
			Datum object = this.objects.get(correctLabel.getObjectName());
			if(object!=null){
				totalEvaluationLabels++;
				if(correctLabel.getCorrectCategory().equals(object.getMajorityCategory())){
					correctEvaluationLabels++;
				}
			}
		}
		this.quality = (correctEvaluationLabels*100)/totalEvaluationLabels;
		
		//Calculating quality for workers
		for (String workerName : this.workers.keySet()) {
			Worker worker = this.workers.get(workerName);
			Collection<AssignedLabel> labels = worker.getAssignedLabels();
			correctEvaluationLabels = 0;
			totalEvaluationLabels = 0;
			for (AssignedLabel label : labels) {
				if(this.evaluationData.containsKey(label.getObjectName())){
					totalEvaluationLabels++;
					if(evaluationData.get(label.getObjectName()).equals(label.getCategoryName())){
						correctEvaluationLabels++;
					}
				}
			}
			worker.setQuality((correctEvaluationLabels*100)/totalEvaluationLabels);
		}
	}
	
	public  void addEvaluationData(CorrectLabel cl){
		this.evaluationData.put(cl.getObjectName(),cl);
	}
}
