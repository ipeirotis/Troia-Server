package com.datascience.gal.commands;

import java.util.Map;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.decision.LabelProbabilityDistributionCalculator;
import com.datascience.gal.decision.ObjectLabelDecisionAlgorithm;

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
			ads.setComputed(true);
			setResult("Compute started");
		}
	}
	
	static public class GetPredictedCategory extends ProjectCommand<Map<String, String>> {
		
		private String labelProbabilityDistribution;
		private String labelDecisionAlgorithm;
		
		public GetPredictedCategory(AbstractDawidSkene ads, String lpd, String lda){
			super(ads, false);
			labelProbabilityDistribution = lpd;
			labelDecisionAlgorithm = lda;
		}
		
		@Override
		void realExecute() {
			setResult(ads.getPredictedCategory(
					LabelProbabilityDistributionCalculator.get(labelProbabilityDistribution),
					ObjectLabelDecisionAlgorithm.get(labelDecisionAlgorithm)));
		}
	}
	
	
	static public class GetCost extends ProjectCommand<Double> {
		
		private String datumId;
		private String algorithm;
		private String costDecisionAlg;
		
		public GetCost(AbstractDawidSkene ads, String datumId, String algorithm, String costDecisionAlgorithm){
			super(ads, false);
			this.datumId = datumId;
			this.algorithm = algorithm;
			this.costDecisionAlg = costDecisionAlgorithm;
		}
		
		@Override
		void realExecute() {
			//TODO:
		}
	}
}
