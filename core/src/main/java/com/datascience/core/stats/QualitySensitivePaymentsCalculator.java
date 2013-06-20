package com.datascience.core.stats;

import com.datascience.core.base.Worker;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.nominal.decision.DecisionEngine;
import com.datascience.core.nominal.decision.LabelProbabilityDistributionCostCalculators;
import com.datascience.core.nominal.decision.ObjectLabelDecisionAlgorithms;
import com.datascience.core.results.WorkerResult;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: artur
 * Date: 5/23/13
 */
public class QualitySensitivePaymentsCalculator {

	Collection<String> categories;
	NominalProject project;
	WorkerResult workerResult;

	public QualitySensitivePaymentsCalculator(NominalProject project, Worker worker){
		this.project = project;
		categories = this.project.getData().getCategories();
		workerResult = this.project.getResults().getWorkerResult(worker);
	}

	private double getErrorRate(String c1, String c2){
		return workerResult.getErrorRate(project.getAlgorithm().getErrorRateCalculator(), c1, c2);
	}

	private HashMap<String, Double> getPosterior(Map<String, Double> priors, Map<String, Integer> draw) {

		// Now compute the posterior, given the draw
		HashMap<String, Double> posterior = new  HashMap<String, Double>();
		for (String s: this.categories) {
			posterior.put(s, 0.0);
		}

		Double sum = 0.0;
		for (String from : this.categories) {
			double pi_c = priors.get(from);
			double evidence = pi_c;
			for (String to : this.categories) {
				Integer n  = draw.get(to);
				double p = getErrorRate(from, to);
				if (n!=0 && p!=0) {
					evidence *= Math.pow(p, n);
				}
				if (p==0) {
					evidence = 0;
				}
			}
			posterior.put(from, evidence);
			sum += evidence;
		}
		for (Map.Entry<String, Double> c: posterior.entrySet()) {
			c.setValue(c.getValue() / sum);
		}
		return posterior;
	}

	private HashMap<String, Integer> getRandomLabelAssignment(int m, String objectCategory) {

		Double total = 0.0;
		// Get the total sum of the corresponding row of the confusion matrix
		for (String to : this.categories) {
			total += getErrorRate(objectCategory, to);
		}

		HashMap<String, Integer> draw = new HashMap<String, Integer>();
		for (String s: this.categories) {
			draw.put(s, 0);
		}

		for (int i=0; i<m; i++) {
			// We pick now the label assigned by worker i
			Double r = Math.random() * total;

			for (String to : this.categories) {
				if (r < getErrorRate(objectCategory, to)) {
					Integer existing = draw.get(to);
					draw.put(to, existing+1);
					break;
				} else {
					r -= getErrorRate(objectCategory, to);
				}
			}
		}

		// Double check that we assigned exactly m elements in the draw
		int sum = 0;
		for (Integer d : draw.values()) {
			sum += d;
		}
		if (sum == m)
			return draw;
		else
			return getRandomLabelAssignment(m, objectCategory);
	}

	private Double getWorkerCost(int m, Map<String, Double> priors, int sample) {

		Double cost = 0.0;

		for (Map.Entry<String, Double> objectCategory: priors.entrySet()) {

			Double pi = objectCategory.getValue();

			Double c = 0.0;
			for (int i = 0; i<sample; i++) {
				Map<String, Integer> draw = getRandomLabelAssignment(m, objectCategory.getKey());
				Map<String, Double> posterior = getPosterior(priors, draw);
				c += getMinCostLabelCost(project, posterior);
			}
			cost += pi * c / sample;
		}
		return cost;
	}

	private double getMinCostLabelCost(NominalProject project, Map<String, Double> distribution){
		DecisionEngine de = new DecisionEngine(
				new LabelProbabilityDistributionCostCalculators.SelectedLabelBased(new ObjectLabelDecisionAlgorithms.MinCostDecisionAlgorithm()),
				null);
		return de.estimateMissclassificationCost(project, distribution);
	}

	public Double getWorkerWage(double qualifiedWage, double costThreshold, Map<String, Double> priors) {
		int m = 0;
		double cost;
		do {
			m++;
			cost = getWorkerCost(m, priors, 100*m);
		} while (cost > costThreshold);

		return qualifiedWage / m;
	}

	public Double getWorkerWage(double qualifiedWage, double costThreshold){
		return getWorkerWage(qualifiedWage, costThreshold, project.getAlgorithm().getCategoryPriors());
	}
}
