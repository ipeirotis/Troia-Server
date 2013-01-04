package com.datascience.gal.commands;

import java.util.Map;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.decision.DecisionEngine;
import com.datascience.gal.decision.ILabelProbabilityDistributionCalculator;
import com.datascience.gal.decision.IObjectLabelDecisionAlgorithm;

/**
 *
 * @author artur
 */
public class EvaluationCommands {
	
	static public class GetCost extends ProjectCommand<Map<String, Double>> {

		private DecisionEngine decisionEngine;
		
		public GetCost(AbstractDawidSkene ads, ILabelProbabilityDistributionCalculator lpd,
				IObjectLabelDecisionAlgorithm lda){
			super(ads, false);
			decisionEngine = new DecisionEngine(lpd, null, lda);
		}
		
		@Override
		void realExecute() {
			setResult(decisionEngine.evaluateMissclassificationCosts(ads));
		}
	}
	
	static public class GetQuality extends ProjectCommand<Map<String, Double>> {
		
		private DecisionEngine decisionEngine;
		
		public GetQuality(AbstractDawidSkene ads, ILabelProbabilityDistributionCalculator lpd,
				IObjectLabelDecisionAlgorithm lda){
			super(ads, false);
			decisionEngine = new DecisionEngine(lpd, null, lda);
		}
		
		@Override
		void realExecute() {
			setResult(decisionEngine.costToQuality(ads,
				decisionEngine.evaluateMissclassificationCosts(ads)));
		}
	}
}
