package com.datascience.gal.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import com.datascience.core.base.LObject;
import com.datascience.datastoring.jobs.JobCommand;
import com.datascience.gal.DatumValue;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.nominal.Quality;
import com.datascience.gal.evaluation.DataEvaluator;

/**
 *
 * @author artur
 */
public class EvaluationCommands {
	
	static public class GetDataCost extends JobCommand<Collection<DatumValue>, NominalProject> {

		private DataEvaluator dataEvaluator;
		
		public GetDataCost(DataEvaluator dataEvaluator){
			super(false);
			this.dataEvaluator = dataEvaluator;
		}
		
		@Override
		protected void realExecute() {
			Collection<DatumValue> cp = new ArrayList<DatumValue>();
			for (Entry<LObject<String>, Double> e : dataEvaluator.evaluate(project).entrySet()){
				cp.add(new DatumValue(e.getKey().getName(), e.getValue()));
			}
			setResult(cp);
		}
	}
	
	static public class GetDataQuality extends JobCommand<Collection<DatumValue>, NominalProject> {
		
		private DataEvaluator dataEvaluator;
		
		public GetDataQuality(DataEvaluator dataEvaluator){
			super(false);
			this.dataEvaluator = dataEvaluator;
		}
		
		@Override
		protected void realExecute() {
			Collection<DatumValue> cp = new ArrayList<DatumValue>();
			for (Entry<LObject<String>, Double> e : Quality.fromCosts(project, dataEvaluator.evaluate(project)).entrySet()){
				cp.add(new DatumValue(e.getKey().getName(), e.getValue()));
			}
			setResult(cp);
		}
	}
}
