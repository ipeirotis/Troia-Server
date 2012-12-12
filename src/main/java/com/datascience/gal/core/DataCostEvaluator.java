package com.datascience.gal.core;

import java.util.HashMap;
import java.util.Map;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.Datum;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.decision.ClassificationAlgorithm;
import com.datascience.gal.decision.LabelProbabilityDistributionCalculator;
import com.datascience.gal.decision.LabelProbabilityDistributionCalulators;
import com.datascience.gal.decision.MinCostDecisionAlgorithm;
import com.datascience.gal.decision.ObjectLabelDecisionAlgorithm;

/**
 * @author Artur Ambroziak
 */
public class DataCostEvaluator {
	
	private static volatile DataCostEvaluator instance = null;

	private DataCostEvaluator() {

	}
	public static DataCostEvaluator getInstance() {
		if (instance == null) {
			synchronized (DataCostEvaluator.class) {
				if (instance == null) {
					instance = new DataCostEvaluator();
				}
			}
		}
		return instance;
	}
	
	private static Map<String, ClassificationAlgorithm> CLASSIFICATION_ALGORITHMS =
		new HashMap<String, ClassificationAlgorithm>();
	static {
		LabelProbabilityDistributionCalculator ds = new LabelProbabilityDistributionCalulators.DS();
		LabelProbabilityDistributionCalculator mv = new LabelProbabilityDistributionCalulators.MV();
		ObjectLabelDecisionAlgorithm minimalized = new MinCostDecisionAlgorithm();

		CLASSIFICATION_ALGORITHMS.put("MinCost", new ClassificationAlgorithm(ds, minimalized));
		CLASSIFICATION_ALGORITHMS.put("MinMVCost", new ClassificationAlgorithm(mv, minimalized));
	}

	public double evaluateMissclassificationCost(DawidSkene ds, String method,
			String object_id) {
		// Ugly as hell but I don't see any other way ...
		AbstractDawidSkene ads = (AbstractDawidSkene) ds;
		Datum datum = ads.getObject(object_id);
		if (method==null || method.isEmpty())
			method = "MinCost";
		return CLASSIFICATION_ALGORITHMS.get(method).predictedLabelEvalCost(datum, ads);
	}
}
