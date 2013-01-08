package com.datascience.gal.commands;

import java.util.Map;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.Quality;
import com.datascience.gal.evaluation.DataEvaluator;

/**
 *
 * @author artur
 */
public class EvaluationCommands {
	
	static public class GetCost extends ProjectCommand<Map<String, Double>> {

		private DataEvaluator dataEvaluator;
		
		public GetCost(AbstractDawidSkene ads,
				DataEvaluator dataEvaluator){
			super(ads, false);
			this.dataEvaluator = dataEvaluator;
		}
		
		@Override
		void realExecute() {
			setResult(dataEvaluator.evaluate(ads));
		}
	}
	
	static public class GetQuality extends ProjectCommand<Map<String, Double>> {
		
		private DataEvaluator dataEvaluator;
		
		public GetQuality(AbstractDawidSkene ads,
				DataEvaluator dataEvaluator){
			super(ads, false);
			this.dataEvaluator = dataEvaluator;
		}
		
		@Override
		void realExecute() {
			setResult(Quality.fromCosts(ads,
				dataEvaluator.evaluate(ads)));
		}
	}
}
