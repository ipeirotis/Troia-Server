package com.datascience.gal.quality;

public class EvaluatorManager {

	public static ClassificationCostEvaluator getEvaluatorForMethod(String methodName) {
		if("MV_SoftEvaluator".equals(methodName)) {
			return MV_SoftEvaluator;
		} else if("DS_SoftEvaluator".equals(methodName)) {
			return DS_SoftEvaluator;
		} else if("MV_MaxLikelihoodEvaluator".equals(methodName)) {
			return MV_MaxLikelihoodEvaluator;
		} else if("DS_MaxLikelihoodEvaluator".equals(methodName)) {
			return DS_MaxLikelihoodEvaluator;
		} else if("MV_MinCost".equals(methodName)) {
			return MV_MinCost;
		} else if("DS_MinCost".equals(methodName)) {
			return DS_MinCost;
		} else {
			return null;
		}
	}

	private static final ClassificationCostEvaluator MV_SoftEvaluator = new MV_SoftCostEvaluator();
	private static final ClassificationCostEvaluator DS_SoftEvaluator = new DS_SoftCostEvaluator();
	private static final ClassificationCostEvaluator MV_MaxLikelihoodEvaluator = new MV_MaxLikelihoodCostEvaluator();
	private static final ClassificationCostEvaluator DS_MaxLikelihoodEvaluator = new DS_MaxLikelihoodCostEvaluator();
	private static final ClassificationCostEvaluator MV_MinCost = new MV_MinCostEvaluator();
	private static final ClassificationCostEvaluator DS_MinCost = new DS_MinCostEvaluator();
}
