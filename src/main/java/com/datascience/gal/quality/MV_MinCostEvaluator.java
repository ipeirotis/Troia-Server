package com.datascience.gal.quality;

import com.datascience.gal.decision.LabelProbabilityDistributionCalulators;

public class MV_MinCostEvaluator extends MinCostEvaluator {

	protected MV_MinCostEvaluator() {
		super( new LabelProbabilityDistributionCalulators.MV());
	}

}
