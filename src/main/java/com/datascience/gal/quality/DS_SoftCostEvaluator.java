package com.datascience.gal.quality;

import com.datascience.gal.decision.LabelProbabilityDistributionCalulators;

public class DS_SoftCostEvaluator extends SoftCostEvaluator {

	protected DS_SoftCostEvaluator() {
		super( new LabelProbabilityDistributionCalulators.DS());
	}

}
