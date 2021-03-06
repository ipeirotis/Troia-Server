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

import com.datascience.core.base.*;
import com.datascience.core.nominal.NominalAlgorithm;
import com.datascience.core.results.DatumResult;
import com.datascience.core.nominal.ICategoryPriorCalculator;
import com.datascience.core.results.WorkerResult;
import com.datascience.core.stats.IErrorRateCalculator;
import com.datascience.utils.ProbabilityDistributions;

public abstract class AbstractDawidSkene extends NominalAlgorithm {

	protected int iterations = 10;
	protected double epsilon = 1e-6;

	public AbstractDawidSkene(IErrorRateCalculator errorRateCalculator, ICategoryPriorCalculator categoryPriorCalculator){
		super(errorRateCalculator, categoryPriorCalculator);
	}

	@Override
	public IErrorRateCalculator getErrorRateCalculator(){
		return errorRateCalculator;
	}

	public double getErrorRateForWorker(Worker worker, String from, String to){
		WorkerResult wr = results.uncheckedGetWorkerResult(worker);
		if (wr == null){
			wr = results.createEmptyWorkerResult(worker);
			results.addWorkerResult(worker, wr);
		}
		return wr.getErrorRate(errorRateCalculator, from, to);
	}

	protected double getLogLikelihood() {
		double result = 0;
		for (LObject<String> obj : data.getObjects()){
			Map<String, Double> estimatedCorrectLabel = results.getDatumResult(obj).getCategoryProbabilites();
			for (AssignedLabel<String> al : data.getAssignsForObject(obj)){
				for (Map.Entry<String, Double> e: estimatedCorrectLabel.entrySet()){
					Double labelingProbability = getErrorRateForWorker(al.getWorker(), e.getKey(), al.getLabel());
					if (e.getValue() == 0. || Double.isNaN(labelingProbability) || labelingProbability == 0.)
						continue;
					else
						result += Math.log(e.getValue()) + Math.log(labelingProbability);
				}
			}
		}
		return result;
	}

	protected double getEntropyForObject(LObject<String> obj){
		DatumResult result = results.getDatumResult(obj);
		double[] p = new double[result.getCategoryProbabilites().size()];

		int i = 0;
		for (Map.Entry<String, Double> e : result.getCategoryProbabilites().entrySet()) {
			p[i] = e.getValue();
			i++;
		}
		return com.datascience.utils.Utils.entropy(p);
	}

	protected void updatePriors() {
		if (data.arePriorsFixed())
			return;

		HashMap<String, Double> priors = new HashMap<String, Double>();
		for (String c : data.getCategories()) {
			priors.put(c, 0.);
		}

		for (LObject<String> obj : data.getObjects()){
			if (!data.getAssigns().isEmpty()){
				DatumResult dr = results.getOrCreateDatumResult(obj);
				for (String c : data.getCategories()){
					priors.put(c, priors.get(c) +
						dr.getCategoryProbability(c) / data.getObjects().size());
				}
			}
		}

		model.setCategoryPriors(priors);
	}

	protected Map<String, Double> getObjectClassProbabilities(LObject<String> object) {
		return getObjectClassProbabilities(object, null);
	}

	protected Map<String, Double> getObjectClassProbabilities(LObject<String> object, Worker workerToIgnore) {
		Map<String, Double> result = new HashMap<String, Double>();

		// If this is a gold example, just put the probability estimate to be
		// 1.0
		// for the correct class
		if (object.isGold()) {
			return ProbabilityDistributions.generateGoldDistribution(data.getCategories(), object.getGoldLabel());
		}

		// Let's check first if we have any workers who have labeled this item,
		// except for the worker that we ignore
		Collection<AssignedLabel<String>> labels = data.getAssignsForObject(object);

		if (workerToIgnore != null && labels.size() == 1 && labels.iterator().next().getWorker().equals(workerToIgnore)) {
			return null;
		}

		if (labels.isEmpty()){
			return getCategoryPriors();
		}

		// If it is not gold, then we proceed to estimate the class
		// probabilities using the method of Dawid and Skene and we proceed as
		// usual with the M-phase of the EM-algorithm of Dawid&Skene

		// Estimate denominator for Eq 2.5 of Dawid&Skene, which is the same
		// across all categories

		// To compute the denominator, we also compute the nominators across
		// all categories, so it saves us time to save the nominators as we
		// compute them
		Map<String, Double> categoryNominators = new HashMap<String, Double>();

		for (String category : data.getCategories()) {

			// We estimate now Equation 2.5 of Dawid & Skene
			double categoryNominator = prior(category);

			// We go through all the labels assigned to the d object
			for (AssignedLabel<String> al : data.getAssignsForObject(object)) {
				Worker w = al.getWorker();

				// If we are trying to estimate the category probability
				// distribution
				// to estimate the quality of a given worker, then we need to
				// ignore
				// the labels submitted by this worker.
				if (workerToIgnore != null
						&& w.equals(workerToIgnore))
					continue;

				String assigned_category = al.getLabel();
				double evidence_for_category = getErrorRateForWorker(w, category, assigned_category);
				if (Double.isNaN(evidence_for_category))
					continue;

				if (evidence_for_category == 0.){
					categoryNominator = Double.NEGATIVE_INFINITY;
					break;
				}
				categoryNominator += Math.log(evidence_for_category);
			}

			categoryNominators.put(category, categoryNominator);
		}
		double M = Double.NEGATIVE_INFINITY;
		for (double log_ci: categoryNominators.values()){
			M = Math.max(log_ci, M);
		}
		if (M == Double.NEGATIVE_INFINITY) {
			return null; // all returned zero so denominator would be 0
		}
		double denominator = 0.;
		for (double log_ci: categoryNominators.values()){
			denominator += Math.exp(log_ci - M);
		}
		denominator = Math.log(denominator) + M;

		for (String c : data.getCategories()) {
			double log_nominator = categoryNominators.get(c);
			double probability = Math.exp(log_nominator - denominator);
			result.put(c, probability);
		}

		return result;

	}

	public void setIterations(int iterations){
		this.iterations = iterations;
	}

	public void setEpsilon(double epsilon){
		this.epsilon = epsilon;
	}
}
