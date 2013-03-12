package com.datascience.gal.evaluation;

import com.datascience.core.base.LObject;
import com.datascience.core.stats.Category;
import com.datascience.core.base.NominalProject;
import com.datascience.gal.decision.DecisionEngine;
import com.datascience.gal.decision.ILabelProbabilityDistributionCalculator;
import com.datascience.gal.decision.IObjectLabelDecisionAlgorithm;
import com.datascience.gal.decision.LabelProbabilityDistributionCalculators;
import com.datascience.gal.decision.ObjectLabelDecisionAlgorithms;
import com.google.common.base.Strings;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author konrad
 */
public class DataEvaluator {
	
	protected ILabelProbabilityDistributionCalculator labelProbabilityDistributionCalculator;
	
	public DataEvaluator(ILabelProbabilityDistributionCalculator lpdc) {
		this.labelProbabilityDistributionCalculator = lpdc;
	}
	
	protected double evaluate(NominalProject project, LObject<String> datum) {
		Map<String, Double> dest_probabilities = labelProbabilityDistributionCalculator.calculateDistribution(datum, project);
		Category fromCostVector = project.getData().getCategory(datum.getEvaluationLabel());
		double cost = 0.0;
		for (Map.Entry<String, Double> e : dest_probabilities.entrySet()) {
			Double misclassification_cost = fromCostVector.getCost(e.getKey());
			cost += e.getValue() * misclassification_cost;
		}
		return cost;
	}
	
	public Map<String, Double> evaluate(NominalProject project){
		Collection<LObject<String>> evalData = project.getData().getEvaluationObjects();
		Map<String, Double> ret = new HashMap<String, Double>();
		for (LObject<String> cl: evalData) {
			ret.put(cl.getName(), evaluate(project, cl));
		}
		return ret;
	}

	public static DataEvaluator get(String labelChoosingMethod,
			ILabelProbabilityDistributionCalculator lpdc){
		if (Strings.isNullOrEmpty(labelChoosingMethod)) {
			labelChoosingMethod = "soft";
		}
		labelChoosingMethod = labelChoosingMethod.toLowerCase();
		if ("soft".equals(labelChoosingMethod)) {
			return new DataEvaluator(lpdc);
		}
		IObjectLabelDecisionAlgorithm olda = ObjectLabelDecisionAlgorithms.get(labelChoosingMethod);
		DecisionEngine decisionEngine = new DecisionEngine(lpdc, null, olda);
		return new DataEvaluator(new LabelProbabilityDistributionCalculators.OnlyOneLabel(decisionEngine));
	}
}
