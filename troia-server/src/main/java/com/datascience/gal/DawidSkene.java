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
import java.util.LinkedList;
import java.util.Map;

public interface DawidSkene {

	public final static double DEFAULT_EPSILON = 1E-6;

	public abstract void addAssignedLabels(Collection<AssignedLabel> als);

	public abstract void addAssignedLabel(AssignedLabel al);

	public abstract void addCorrectLabels(Collection<CorrectLabel> cls);

	public abstract void addCorrectLabel(CorrectLabel cl);

	public abstract void addEvaluationData(Collection<CorrectLabel> cl);
	
	public abstract void addObjects(Collection<String> objs);

	/**
	 * @return the fixedPriors
	 */
	public abstract boolean fixedPriors();

	public abstract void addMisclassificationCosts(
		Collection<MisclassificationCost> cls);

	public abstract void addMisclassificationCost(MisclassificationCost cl);

	/**
	 * Runs the algorithm, iterating at most the specified number of times;
	 * estimates the model log-likelihood and stop once the log-likelihood
	 * values converge (with the default value of the epsilon)
	 *
	 * @param iterations
	 */
	public abstract void estimate(int maxIterations);

	/**
	 * Runs the algorithm, iterating at most the specified number of times;
	 * estimates the model log-likelihood and stop once the log-likelihood
	 * values converge (with the specified value of the epsilon)
	 *
	 * @param iterations
	 */
	public abstract void estimate(int maxIterations, double epsilon);

	/**
	 * TODO:
	 *
	 * @param objectName
	 *            - the name of the object being queried
	 * @return the majority vote category if object name is found, else null.
	 */
	public abstract String getMajorityVote(String objectName);

	public abstract Map<String, String> getMajorityVote();

	public abstract Map<String, String> getMajorityVote(
		Collection<String> objectNames);

	public abstract Map<String, Double> getObjectProbs(String objectName);

	public abstract Map<String, Map<String, Double>> getObjectProbs();

	public abstract Map<String, Map<String, Double>> getObjectProbs(
		Collection<String> objectName);

	/**
	 * Estimates the cost for annotator k without attempting corrections of
	 * labels
	 *
	 * @param w
	 *            The worker
	 * @return The expected cost of misclassifications of worker
	 */
	public abstract double getAnnotatorCostNaive(Worker w);

	public abstract int getNumberOfWorkers();

	public abstract int getNumberOfObjects();

	/**
	 *
	 * Estimates the cost for worker using various methods: COST_NAIVE: We do
	 * not adjust the label assigned by the worker (i.e., use the "hard" label)
	 * COST_ADJUSTED: We use the error rates of the worker and compute the
	 * posterior probability vector for each object COST_MINIMIZED: Like
	 * COST_ADJUSTED but we also assign the object to the category that
	 * generates the minimum expected error
	 *
	 * @param w
	 *            The worker object
	 * @param method
	 *            One of DawidSkene.COST_NAIVE, DawidSkene.COST_ADJUSTED,
	 *            DawidSkene.COST_MINIMIZED
	 * @return The expected cost of the worker, normalized to be between 0 and
	 *         1, where 1 is the cost of a "spam" worker
	 */
	public abstract double getWorkerCost(Worker w, WorkerCostMethod method);

	public abstract String printDiffVote(Map<String, String> prior_voting,
										 Map<String, String> posterior_voting);

	public abstract String printAllWorkerScores(boolean detailed);
	
	public abstract LinkedList<Map<String, Object>> getAllWorkerScores(boolean detailed);

	/**
	 * TODO: (josh) i'm too lazy to make this more functional rather than
	 * something that returns some complex string structure.
	 *
	 * @param w
	 * @param detailed
	 * @return
	 */
	public abstract String printWorkerScore(Worker w, boolean detailed);
	
	/**
	 * Same as above function but returns result as a Map<String, Object>
	 * @param w
	 * @param detailed
	 * @return
	 */
	public abstract Map<String, Object> getWorkerScore(Worker w, boolean detailed);

	/**
	 * Prints the objects that have probability distributions with entropy
	 * higher than the given threshold
	 *
	 * @param entropy_threshold
	 */
	public abstract String printObjectClassProbabilities(
		double entropy_threshold);

	/**
	 *
	 * @param objectName
	 *            - the name of the entity being queried
	 * @return a map of class names to membership probabilities
	 */
	public abstract Map<String, Double> objectClassProbabilities(
		String objectName);

	/**
	 *
	 * @param objectName
	 *            - name of the entity being queried
	 * @param entropyThreshold
	 *            - minimum entropy required for consideration
	 * @return a map of class names to membership probabilities
	 */
	public abstract Map<String, Double> objectClassProbabilities(
		String objectName, double entropyThreshold);

	public abstract String printPriors();

	public abstract Map<String, Double> computePriors();

	public abstract double prior(String categoryName);

	public abstract String printVote();

	public abstract void setFixedPriors(Map<String, Double> priors);

	public abstract void unsetFixedPriors();

	public abstract String getId();

	public abstract Map<String, Category> getCategories();

	public abstract String toString();

	public abstract Map<String,Datum> getObjects();

	Map<String, Double> getWorkerPriors(Worker worker);

	double getErrorRateForWorker(Worker worker, String from, String to);

	public abstract boolean  isComputed();
	public abstract void  setComputed(boolean computed);
	public Worker getWorker(String name);
}
