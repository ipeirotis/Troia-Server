package com.datascience.gal.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import com.datascience.executor.JobCommand;
import com.datascience.gal.DatumValue;
import com.datascience.core.nominal.NominalProject;
import com.datascience.gal.Quality;
import com.datascience.gal.evaluation.DataEvaluator;

/**
 *
 * @author artur
 */
public class EvaluationCommands {
	
	static public class GetCost extends JobCommand<Collection<DatumValue>, NominalProject> {

		private DataEvaluator dataEvaluator;
		
		public GetCost(DataEvaluator dataEvaluator){
			super(false);
			this.dataEvaluator = dataEvaluator;
		}
		
		@Override
		protected void realExecute() {
			Collection<DatumValue> cp = new ArrayList<DatumValue>();
			for (Entry<String, Double> e : dataEvaluator.evaluate(project).entrySet()){
				cp.add(new DatumValue(e.getKey(), e.getValue()));
			}
			setResult(cp);
		}
	}
	
	static public class GetQuality extends JobCommand<Collection<DatumValue>, NominalProject> {
		
		private DataEvaluator dataEvaluator;
		
		public GetQuality(DataEvaluator dataEvaluator){
			super(false);
			this.dataEvaluator = dataEvaluator;
		}
		
		@Override
		protected void realExecute() {
			Collection<DatumValue> cp = new ArrayList<DatumValue>();
			for (Entry<String, Double> e : Quality.fromCosts(project, dataEvaluator.evaluate(project)).entrySet()){
				cp.add(new DatumValue(e.getKey(), e.getValue()));
			}
			setResult(cp);
		}
	}
}
