package com.datascience.gal.quality;


import com.datascience.gal.decision.LabelProbabilityDistributionCalulators;

public class DS_MaxLikelihoodCostEvaluator extends
	MaxLikelihoodCostEvaluator {

	public DS_MaxLikelihoodCostEvaluator() {
		super( new LabelProbabilityDistributionCalulators.DS());
	}




}
