package com.datascience.gal.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.DatumClassification;
import com.datascience.gal.DatumValue;
import com.datascience.gal.Quality;
import com.datascience.gal.decision.DecisionEngine;
import com.datascience.gal.decision.ILabelProbabilityDistributionCalculator;
import com.datascience.gal.decision.ILabelProbabilityDistributionCostCalculator;
import com.datascience.gal.decision.IObjectLabelDecisionAlgorithm;

/**
 *
 * @author artur
 */
public class PredictionCommands {
	
	static public class Compute extends DSCommandBase<Object> {

		private int iterations;
		
		public Compute(AbstractDawidSkene ads, int iterations){
			super(ads, true);
			this.iterations = iterations;
		}
		
		@Override
		protected void realExecute() {
			ads.estimate(iterations);
			setResult("Computation done");
		}
	}
	
	static public class GetPredictedCategory extends DSCommandBase<Collection<DatumClassification>> {
		
		private DecisionEngine decisionEngine;

		
		public GetPredictedCategory(AbstractDawidSkene ads, ILabelProbabilityDistributionCalculator lpd,
				IObjectLabelDecisionAlgorithm lda){
			super(ads, false);
			decisionEngine = new DecisionEngine(lpd, null, lda);
		}
		
		@Override
		protected void realExecute() {
			Collection<DatumClassification> dc = new ArrayList<DatumClassification>();
			for (Entry<String, String> e : decisionEngine.predictLabels(ads).entrySet()){
				dc.add(new DatumClassification(e.getKey(), e.getValue()));
			}
			setResult(dc);
		}
	}
	
	
	static public class GetCost extends DSCommandBase<Collection<DatumValue>> {
		
		private DecisionEngine decisionEngine;
		
		public GetCost(AbstractDawidSkene ads,
				ILabelProbabilityDistributionCalculator lpd,
				ILabelProbabilityDistributionCostCalculator lca){
			super(ads, false);
			decisionEngine = new DecisionEngine(lpd, lca, null);
		}
		
		@Override
		protected void realExecute() {
			Collection<DatumValue> cp = new ArrayList<DatumValue>();
			for (Entry<String, Double> e : decisionEngine.estimateMissclassificationCosts(ads).entrySet()){
				cp.add(new DatumValue(e.getKey(), e.getValue()));
			}
			setResult(cp);
		}
	}
	
	static public class GetQuality extends DSCommandBase<Collection<DatumValue>> {
		
		private DecisionEngine decisionEngine;
		
		public GetQuality(AbstractDawidSkene ads,
				ILabelProbabilityDistributionCalculator lpd,
				ILabelProbabilityDistributionCostCalculator lca){
			super(ads, false);
			decisionEngine = new DecisionEngine(lpd, lca, null);
		}
		
		@Override
		protected void realExecute() {
			Collection<DatumValue> cp = new ArrayList<DatumValue>();
			for (Entry<String, Double> e : Quality.fromCosts(ads, decisionEngine.estimateMissclassificationCosts(ads)).entrySet()){
				cp.add(new DatumValue(e.getKey(), e.getValue()));
			}
			setResult(cp);
		}
	}
}
