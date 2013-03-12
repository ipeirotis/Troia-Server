package com.datascience.core.nominal;

import com.datascience.core.base.*;
import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.DatumResult;
import com.datascience.gal.decision.DecisionEngine;
import com.datascience.gal.decision.ILabelProbabilityDistributionCalculator;
import com.datascience.gal.decision.LabelProbabilityDistributionCalculators;
import com.datascience.gal.decision.ObjectLabelDecisionAlgorithms;
import com.datascience.utils.ProbabilityDistributions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: artur
 */
public class NominalProject extends Project<String, NominalData, DatumResult, WorkerResult> {

	protected DecisionEngine mvDecisionEnginge;
	protected ILabelProbabilityDistributionCalculator spammerProbDistr;

	public NominalProject(Algorithm algorithm1){
		super(algorithm1);
		mvDecisionEnginge = new DecisionEngine(
				new LabelProbabilityDistributionCalculators.DS(), null,
				new ObjectLabelDecisionAlgorithms.MaxProbabilityDecisionAlgorithm());
		spammerProbDistr = new LabelProbabilityDistributionCalculators.PriorBased();
		data = new NominalData();
		algorithm.setData(data);
	}

	public void initializeCategories(Collection<Category> categories){
		boolean fp = data.addCategories(categories);
		if (!fp)
			((AbstractDawidSkene)algorithm).initializePriors();
		results = createResultsInstance(categories);
		algorithm.setResults(results);
	}

	public Results<String, DatumResult, WorkerResult> createResultsInstance(Collection<Category> categories){
		return new Results<String, DatumResult, WorkerResult>(
				new ResultsFactory.DatumResultFactory(),
				new ResultsFactory.WorkerResultNominalFactory(categories));
	}

	/**
	 * @param w
	 * @param objectCategory
	 * @return
	 */
	protected Map<String, Double> getNaiveSoftLabel(Worker<String> w,
													String objectCategory) {

		HashMap<String, Double> naiveSoftLabel = new HashMap<String, Double>();
		for (Category cat : data.getCategories()) {
			naiveSoftLabel.put(cat.getName(), ((AbstractDawidSkene) algorithm).getErrorRateForWorker(w, objectCategory, cat.getName()));
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
		Map<String, Double> prior = ProbabilityDistributions.getSpammerDistribution(data);

		return getMinSoftLabelCost(prior);
	}

	/**
	 * Returns the cost of a "spammer" worker, who assigns completely random
	 * labels.
	 *
	 * @return The expected cost of a spammer worker
	 */
	public double getSpammerCost() {
		Map<String, Double> prior = ProbabilityDistributions.getSpammerDistribution(data);
		return getSoftLabelCost(prior);
	}
}
