package com.datascience.gal.quality;

import java.util.Map;

import com.datascience.gal.Category;
import com.datascience.gal.Datum;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.decision.LabelProbabilityDistributionCalculator;



public abstract class ClassificationCostEvaluator {

	
	protected ClassificationCostEvaluator(LabelProbabilityDistributionCalculator calculator){
		this.calculator = calculator;
	}
	
	public double EvaluateClassificationCost(DawidSkene ds,String evaluationCategory,Datum datum){
		
		Map<String, Double>	dest_probabilities = this.getProbabilityVector(ds,datum);
		
		Category fromCostVector = ds.getCategories().get(evaluationCategory);
		Double cost = 0.0;
		
		for (String to : dest_probabilities.keySet()) {
			Double prob = dest_probabilities.get(to);
			Double misclassification_cost = fromCostVector.getCost(to);
			cost += prob * misclassification_cost;
		}

		return cost;
	}
	
	protected abstract Map<String, Double> getProbabilityVector(DawidSkene ds,Datum datum);

	protected LabelProbabilityDistributionCalculator calculator;
}
