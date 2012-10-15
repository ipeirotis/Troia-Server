package com.datascience.gal.quality;

import com.datascience.gal.decision.LabelProbabilityDistributionCalulators;

public class MV_MaxLikelihoodCostEvaluator extends
MaxLikelihoodCostEvaluator {

	protected MV_MaxLikelihoodCostEvaluator() {
		super( new LabelProbabilityDistributionCalulators.DS());
	}

	




}
