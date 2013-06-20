package com.datascience.core.results;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.CategoryPair;
import com.datascience.core.stats.*;
import com.google.common.base.Objects;

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
	protected ConfusionMatrix eval_cm;

	public WorkerResult(Collection<String> categories){
		cm = new MultinomialConfusionMatrix(categories);
	}

	public double getErrorRate(IErrorRateCalculator erc, String categoryFrom, String categoryTo){
		return erc.getErrorRate(cm, categoryFrom, categoryTo);
	}

	public double getEvalErrorRate(String from, String to){
		return eval_cm.getErrorRateBatch(from, to);
	}

	public void empty() {
		cm.empty();
	}

	public ConfusionMatrix getConfusionMatrix(){
		return cm;
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

	public void computeEvalConfusionMatrix(Collection<String> categories,
										   Collection<AssignedLabel<String>> workerAssigns) {
		eval_cm = new MultinomialConfusionMatrix(categories, new HashMap<CategoryPair, Double>());
		for (AssignedLabel<String> l : workerAssigns) {
			if (l.getLobject().getEvaluationLabel() != null){
				String assignedCategory = l.getLabel();
				String correctCategory = l.getLobject().getEvaluationLabel();
				eval_cm.addError(correctCategory, assignedCategory, 1.0);
			}
		}
		eval_cm.normalize();
	}

	@Override
	public boolean equals(Object other){
		if (other instanceof WorkerResult) {
			return Objects.equal(cm, ((WorkerResult) other).cm);
		}
		return false;
	}
}
