package com.datascience.gal.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import com.datascience.gal.DatumValue;
import com.datascience.gal.Quality;
import com.datascience.gal.evaluation.DataEvaluator;

/**
 *
 * @author artur
 */
public class EvaluationCommands {
	
	static public class GetCost extends DSCommandBase<Collection<DatumValue>> {

		private DataEvaluator dataEvaluator;
		
		public GetCost(DataEvaluator dataEvaluator){
			super(false);
			this.dataEvaluator = dataEvaluator;
		}
		
		@Override
		protected void realExecute() {
			Collection<DatumValue> cp = new ArrayList<DatumValue>();
			for (Entry<String, Double> e : dataEvaluator.evaluate(ads).entrySet()){
				cp.add(new DatumValue(e.getKey(), e.getValue()));
			}
			setResult(cp);
		}
	}
	
	static public class GetQuality extends DSCommandBase<Collection<DatumValue>> {
		
		private DataEvaluator dataEvaluator;
		
		public GetQuality(DataEvaluator dataEvaluator){
			super(false);
			this.dataEvaluator = dataEvaluator;
		}
		
		@Override
		protected void realExecute() {
			Collection<DatumValue> cp = new ArrayList<DatumValue>();
			for (Entry<String, Double> e : Quality.fromCosts(ads, dataEvaluator.evaluate(ads)).entrySet()){
				cp.add(new DatumValue(e.getKey(), e.getValue()));
			}
			setResult(cp);
		}
	}
}
