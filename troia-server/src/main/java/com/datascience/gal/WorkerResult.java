package com.datascience.gal;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: artur
 */
public class WorkerResult {

	// The error matrix for the worker
	public ConfusionMatrix cm;

	//The confusion matrix for the worker based on evaluation data
	private ConfusionMatrix eval_cm;

	public void empty() {
		cm.empty();
	}

	public Map<String, Double> getPrior(Collection<AssignedLabel<String>> workerAssigns, Collection<String> categories){
		int sum = workerAssigns.size();
		HashMap<String, Double> worker_prior = new HashMap<String, Double>();
		for (String category : categories) {
			if (sum>0) {
				double cnt = 0;
				for (AssignedLabel<String> al : workerAssigns)
					if (al.getLabel().equals(category))
						cnt += 1.;
				Double prob = cnt / sum;
				worker_prior.put(category, prob);
			} else {
				worker_prior.put(category, 1.0/categories.size());
			}
		}
		return worker_prior;
	}

	public void addError(String source, String destination, double error) {
		cm.addError(source, destination, error);
	}

	public void removeError(String source, String destination, double error) {
		cm.removeError(source, destination, error);
	}

	public void normalize(ConfusionMatrixNormalizationType type) {
		switch (type) {
			case UNIFORM:
				cm.normalize();
				break;
			case LAPLACE:
				cm.normalizeLaplacean();
				break;
		}
	}

	public double getErrorRateIncremental(String categoryFrom,
										  String categoryTo, ConfusionMatrixNormalizationType type) {
		switch (type) {
			case UNIFORM:
				return cm.getNormalizedErrorRate(categoryFrom, categoryTo);
			case LAPLACE:
			default:
				return cm.getLaplaceNormalizedErrorRate(categoryFrom, categoryTo);
		}
	}

	public double getErrorRateBatch(String categoryFrom, String categoryTo) {
		return cm.getErrorRateBatch(categoryFrom, categoryTo);
	}

	public void computeEvalConfusionMatrix(Map<String, LObject<String>> evalData,
										   Collection<Category> categories,
										   Collection<AssignedLabel<String>> workerAssigns) {
		eval_cm = new MultinomialConfusionMatrix(categories, new HashMap<CategoryPair, Double>());
		for (AssignedLabel<String> l : workerAssigns) {
			String objectName = l.getLobject().getName();
			LObject<String> d = evalData.get(objectName);
			if (d != null){
				String assignedCategory = l.getLabel();
				String correctCategory = d.getGoldLabel();
				eval_cm.addError(correctCategory, assignedCategory, 1.0);
			}
		}
		eval_cm.normalize();
	}

	public double getEvalErrorRate(String from, String to){
		return eval_cm.getErrorRateBatch(from, to);
	}
}