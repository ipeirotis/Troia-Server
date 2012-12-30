package com.datascience.gal.commands;

import java.util.Map;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.decision.LabelProbabilityDistributionCalculator;
import com.datascience.gal.decision.ObjectLabelDecisionAlgorithm;

/**
 *
 * @author artur
 */
public class EvaluationCommands {
	
	static public class GetCost extends ProjectCommand<Map<String, Double>> {
		
		private String labelProbabilityDistribution;
		private String labelDecisionAlgorithm;
		
		public GetCost(AbstractDawidSkene ads, String lpd, String lda){
			super(ads, false);
			labelProbabilityDistribution = lpd;
			labelDecisionAlgorithm = lda;
		}
		
		@Override
		void realExecute() {
			setResult(ads.getEvaluatedCost(
					LabelProbabilityDistributionCalculator.get(labelProbabilityDistribution),
					ObjectLabelDecisionAlgorithm.get(labelDecisionAlgorithm)));
		}
	}
}
