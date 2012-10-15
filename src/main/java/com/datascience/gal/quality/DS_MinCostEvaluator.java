package com.datascience.gal.quality;

import com.datascience.gal.decision.LabelProbabilityDistributionCalulators;

public class DS_MinCostEvaluator extends MinCostEvaluator {

	protected DS_MinCostEvaluator() {
		super( new LabelProbabilityDistributionCalulators.DS());
	}


}
