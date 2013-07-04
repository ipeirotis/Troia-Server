package com.datascience.gal.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.datascience.core.base.LObject;
import com.datascience.core.nominal.decision.LabelProbabilityDistributionCostCalculators;
import com.datascience.core.nominal.decision.WorkerEstimator;
import com.datascience.core.nominal.decision.WorkerQualityCalculator;
import com.datascience.datastoring.jobs.JobCommand;
import com.datascience.gal.DatumValue;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.nominal.Quality;
import com.datascience.gal.evaluation.DataEvaluator;
import com.datascience.gal.evaluation.WorkerEvaluator;
import com.datascience.utils.MathHelpers;

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

	static public class GetDataCostSummary extends JobCommand<Map<String, Object>, NominalProject> {

		public GetDataCostSummary(){
			super(false);
		}

		@Override
		protected void realExecute() throws Exception {
			HashMap<String, Object> ret = new HashMap<String, Object>();
			for (String s : new String[] {"MinCost", "MaxLikelihood"}){
				DataEvaluator de = new DataEvaluator(s);
				ret.put(s, MathHelpers.getAverage(de.evaluate(project)));
			}
			setResult(ret);
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

	static public class GetDataQualitySummary extends JobCommand<Map<String, Object>, NominalProject> {

		public GetDataQualitySummary(){
			super(false);
		}

		@Override
		protected void realExecute() throws Exception {
			HashMap<String, Object> ret = new HashMap<String, Object>();
			for (String s : new String[] {"MinCost", "MaxLikelihood"}){
				DataEvaluator de = new DataEvaluator(s);
				ret.put(s, Quality.fromCost(project, MathHelpers.getAverage(de.evaluate(project))));
			}
			setResult(ret);
		}
	}

	static public class GetWorkersQualitySummary extends JobCommand<Map<String, Object>, NominalProject> {

		public GetWorkersQualitySummary(){
			super(false);
		}

		@Override
		protected void realExecute() throws Exception {
			HashMap<String, Object> ret = new HashMap<String, Object>();
			for (String s : new String[] {"MinCost", "MaxLikelihood"}){
				WorkerQualityCalculator wqc = new WorkerEvaluator(LabelProbabilityDistributionCostCalculators.get(s));
				ret.put(s, Quality.fromCost(project, MathHelpers.getAverage(wqc.getCosts(project))));
			}
			setResult(ret);
		}
	}
}
