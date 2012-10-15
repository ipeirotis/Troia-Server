package com.datascience.gal.quality;

import com.datascience.gal.decision.LabelProbabilityDistributionCalulators;



public class MV_SoftCostEvaluator extends SoftCostEvaluator {

	protected MV_SoftCostEvaluator() {
		super( new LabelProbabilityDistributionCalulators.MV());
	}

}
