package com.datascience.gal.commands;

import java.util.Map;

import com.datascience.gal.AbstractDawidSkene;
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
		
		private ILabelProbabilityDistributionCalculator labelProbabilityDistributionCalculator;
		private IObjectLabelDecisionAlgorithm labelDecisionAlgorithm;
		
		public GetPredictedCategory(AbstractDawidSkene ads, ILabelProbabilityDistributionCalculator lpd,
				IObjectLabelDecisionAlgorithm lda){
			super(ads, false);
			labelProbabilityDistributionCalculator = lpd;
			labelDecisionAlgorithm = lda;
		}
		
		@Override
		void realExecute() {
			setResult(ads.getPredictedCategory(
				labelProbabilityDistributionCalculator, labelDecisionAlgorithm));
		}
	}
	
	
	static public class GetCost extends ProjectCommand<Map<String, Double>> {
		
		private ILabelProbabilityDistributionCalculator labelProbabilityDistributionCalculator;
		private ILabelProbabilityDistributionCostCalculator labelingCostAlgorithm;
		
		public GetCost(AbstractDawidSkene ads,
				ILabelProbabilityDistributionCalculator lpd,
				ILabelProbabilityDistributionCostCalculator lca){
			super(ads, false);
			labelProbabilityDistributionCalculator = lpd;
			labelingCostAlgorithm = lca;
		}
		
		@Override
		void realExecute() {
			setResult(ads.getEstimatedCost(
				labelProbabilityDistributionCalculator, labelingCostAlgorithm));
		}
	}
}
