package com.datascience.gal.evaluation;

import com.datascience.gal.Category;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.Datum;
import com.datascience.gal.DawidSkene;
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
	
	public double evaluate(DawidSkene ds, CorrectLabel ed) {
		String correctLabel = ed.getCorrectCategory();
		if (correctLabel != null){
			Datum datum = ds.getObjects().get(ed.getObjectName());
			if (datum == null) {
				throw new IllegalArgumentException("Evaluation object doesn't match any datum: " + correctLabel);
			}
			return evaluate(ds, datum, correctLabel);
		}
		return 1.; // FIXME: in previous version was 1. - maybe we should throw exception if correctLabel== null ?
	}
	
	protected double evaluate(DawidSkene ds, Datum datum, String correctLabel) {
		Map<String, Double> dest_probabilities = labelProbabilityDistributionCalculator.calculateDistribution(datum, ds);
		Category fromCostVector = ds.getCategories().get(correctLabel);
		double cost = 0.0;
		for (Map.Entry<String, Double> e : dest_probabilities.entrySet()) {
			double misclassification_cost = fromCostVector.getCost(e.getKey());
			cost += e.getValue() * misclassification_cost;
		}
		return cost;
	}
	
	public Map<String, Double> evaluate(DawidSkene ds){
		Collection<CorrectLabel> evalData = ds.getEvaluationDatums().values();
		Map<String, Double> ret = new HashMap<String, Double>();
		for (CorrectLabel cl: evalData) {
			ret.put(cl.getObjectName(), evaluate(ds, cl));
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
