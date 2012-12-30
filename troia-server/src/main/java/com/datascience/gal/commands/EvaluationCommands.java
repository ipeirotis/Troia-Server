package com.datascience.gal.commands;

import java.util.Map;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.decision.ILabelProbabilityDistributionCalculator;
import com.datascience.gal.decision.IObjectLabelDecisionAlgorithm;

/**
 *
 * @author artur
 */
public class EvaluationCommands {
	
	static public class GetCost extends ProjectCommand<Map<String, Double>> {
		
		private ILabelProbabilityDistributionCalculator labelProbabilityDistributionCalculator;
		private IObjectLabelDecisionAlgorithm labelDecisionAlgorithm;
		
		public GetCost(AbstractDawidSkene ads, ILabelProbabilityDistributionCalculator lpd,
				IObjectLabelDecisionAlgorithm lda){
			super(ads, false);
			labelProbabilityDistributionCalculator = lpd;
			labelDecisionAlgorithm = lda;
		}
		
		@Override
		void realExecute() {
			setResult(ads.getEvaluatedCost(
				labelProbabilityDistributionCalculator, labelDecisionAlgorithm));
		}
	}
}
