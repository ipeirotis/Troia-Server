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

	public abstract void addEvaluationDatums(Collection<CorrectLabel> cl);
	
	public abstract void addObjects(Collection<String> objs);
	
	public abstract void markObjectsAsGold(Collection<CorrectLabel> cl);

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

	public abstract int getNumberOfUnassignedObjects();
	
	public abstract LinkedList<Map<String, Object>> getAllWorkerScores(boolean detailed);

	public abstract Map<String, Object> getWorkerScore(Worker w, boolean detailed);

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

	public abstract void setFixedPriors(Map<String, Double> priors);

	public abstract void unsetFixedPriors();

	public abstract String getId();

	public abstract Map<String, Category> getCategories();

	public abstract Map<String,Datum> getObjects();

	public abstract Collection<CorrectLabel> getGoldDatums();
	
	public abstract Collection<CorrectLabel> getEvaluationDatums();
	
	public abstract Map<String, String> getInfo();
	
	Map<String, Double> getCategoryPriors();
	double prior(String categoryName);

	double getErrorRateForWorker(Worker worker, String from, String to);

	public abstract boolean  isComputed();
	public Worker getWorker(String name);
	public Collection<Worker> getWorkers();
}
