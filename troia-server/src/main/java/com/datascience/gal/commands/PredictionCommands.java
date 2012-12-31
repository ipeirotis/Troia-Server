package com.datascience.gal.commands;

import java.util.Map;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.decision.DecisionEngine;
import com.datascience.gal.decision.ILabelProbabilityDistributionCalculator;
import com.datascience.gal.decision.ILabelProbabilityDistributionCostCalculator;
import com.datascience.gal.decision.IObjectLabelDecisionAlgorithm;

/**
 *
 * @author artur
 */
public class PredictionCommands {
	
	static public class Compute extends ProjectCommand<Object> {

		private int iterations;
		
		public Compute(AbstractDawidSkene ads, int iterations){
			super(ads, true);
			this.iterations = iterations;
		}
		
		@Override
		void realExecute() {
			ads.estimate(iterations);
			setResult("Computation done");
		}
	}
	
	static public class GetPredictedCategory extends ProjectCommand<Map<String, String>> {
		
		private DecisionEngine decisionEngine;

		
		public GetPredictedCategory(AbstractDawidSkene ads, ILabelProbabilityDistributionCalculator lpd,
				IObjectLabelDecisionAlgorithm lda){
			super(ads, false);
			decisionEngine = new DecisionEngine(lpd, null, lda);
		}
		
		@Override
		void realExecute() {
			setResult(decisionEngine.predictLabels(ads));
		}
	}
	
	
	static public class GetCost extends ProjectCommand<Map<String, Double>> {
		
		private DecisionEngine decisionEngine;
		
		public GetCost(AbstractDawidSkene ads,
				ILabelProbabilityDistributionCalculator lpd,
				ILabelProbabilityDistributionCostCalculator lca){
			super(ads, false);
			decisionEngine = new DecisionEngine(lpd, lca, null);
		}
		
		@Override
		void realExecute() {
			setResult(decisionEngine.estimateMissclassificationCosts(ads));
		}
	}
	
	static public class GetQuality extends ProjectCommand<Map<String, Double>> {
		
		private DecisionEngine decisionEngine;
		
		public GetQuality(AbstractDawidSkene ads,
				ILabelProbabilityDistributionCalculator lpd,
				ILabelProbabilityDistributionCostCalculator lca){
			super(ads, false);
			decisionEngine = new DecisionEngine(lpd, lca, null);
		}
		
		@Override
		void realExecute() {
			setResult(decisionEngine.costToQuality(ads,
				decisionEngine.estimateMissclassificationCosts(ads)));
		}
	}
}
