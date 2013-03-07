package com.datascience.gal.commands;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.executor.JobCommand;
import com.datascience.gal.*;
import com.datascience.gal.decision.*;

import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author artur
 */
public class PredictionCommands {
	
	static public class Compute extends JobCommand<Object, AbstractDawidSkene> {

		private int iterations;
		
		public Compute(int iterations){
			super(true);
			this.iterations = iterations;
		}
		
		@Override
		protected void realExecute() {
			project.estimate(1e-6, iterations);
			setResult("Computation done");
		}
	}
	
	static public class GetPredictedCategory extends JobCommand<Collection<DatumClassification>, NominalProject> {
		
		private DecisionEngine decisionEngine;

		public GetPredictedCategory(ILabelProbabilityDistributionCalculator lpd,
				IObjectLabelDecisionAlgorithm lda){
			super(false);
			decisionEngine = new DecisionEngine(lpd, null, lda);
		}
		
		@Override
		protected void realExecute() {
			Collection<DatumClassification> dc = new ArrayList<DatumClassification>();
			for (Entry<String, String> e : decisionEngine.predictLabels(project).entrySet()){
				dc.add(new DatumClassification(e.getKey(), e.getValue()));
			}
			setResult(dc);
		}
	}

	static public class GetCost extends JobCommand<Collection<DatumValue>, NominalProject> {
		
		private DecisionEngine decisionEngine;
		
		public GetCost(ILabelProbabilityDistributionCalculator lpd,
				ILabelProbabilityDistributionCostCalculator lca){
			super(false);
			decisionEngine = new DecisionEngine(lpd, lca, null);
		}
		
		@Override
		protected void realExecute() {
			Collection<DatumValue> cp = new ArrayList<DatumValue>();
			for (Entry<String, Double> e : decisionEngine.estimateMissclassificationCosts(project).entrySet()){
				cp.add(new DatumValue(e.getKey(), e.getValue()));
			}
			setResult(cp);
		}
	}
	
	static public class GetQuality extends JobCommand<Collection<DatumValue>, NominalProject> {
		
		private DecisionEngine decisionEngine;
		
		public GetQuality(ILabelProbabilityDistributionCalculator lpd,
				ILabelProbabilityDistributionCostCalculator lca){
			super(false);
			decisionEngine = new DecisionEngine(lpd, lca, null);
		}
		
		@Override
		protected void realExecute() {
			Collection<DatumValue> cp = new ArrayList<DatumValue>();
			for (Entry<String, Double> e : Quality.fromCosts(project, decisionEngine.estimateMissclassificationCosts(project)).entrySet()){
				cp.add(new DatumValue(e.getKey(), e.getValue()));
			}
			setResult(cp);
		}
	}

	static public class GetPredictionZip extends com.datascience.core.commands.PredictionCommands.AbstractGetPredictionZip<NominalProject> {

		public GetPredictionZip(String path){
			super(path);
			HashMap<String, GetStatistics> files = new HashMap<String, GetStatistics>();
			files.put("prediction.tsv", new GetDataPrediction());
			files.put("workers_quality.tsv", new GetWorkersQuality());
			setStatisticsFilesMap(files);
		}

		class GetDataPrediction extends GetStatistics {

			@Override
			public List<List<Object>> call(){
				String[] lpdcs = new String[]{"DS", "MV"};
				String[] lda = new String[]{"MaxLikelihood", "MinCost"};
				List<List<Object>> ret = new ArrayList<List<Object>>();

				List<Object> header = new ArrayList<Object>();
				header.add("");
				for (String lpd : lpdcs)
					for (String la : lda)
						header.add(lpd+" "+la);
				ret.add(header);

				for (LObject<String> d : project.getData().getObjects()){
					List<Object> line = new ArrayList<Object>();
					line.add(d.getName());
					for (String lpdc : lpdcs)
						for (String la : lda){
							DecisionEngine decisionEngine = new DecisionEngine(
									LabelProbabilityDistributionCalculators.get(lpdc),
									null,
									ObjectLabelDecisionAlgorithms.get(la));
							line.add(decisionEngine.predictLabel(project, d));
						}
					ret.add(line);
				}

				return ret;
			}
		}

		class GetWorkersQuality extends GetStatistics {

			public List<List<Object>> call(){
				String[] lca = new String[]{"MaxLikelihood", "MinCost", "ExpectedCost"};
				List<List<Object>> ret = new ArrayList<List<Object>>();
				List<Object> header = new ArrayList<Object>();

				header.add("");
				for (String lc : lca)
					header.add(lc);
				ret.add(header);

				for (Worker<String> w : project.getData().getWorkers()){
					List<Object> line = new ArrayList<Object>();
					line.add(w.getName());
					for (String lc : lca){

						WorkerQualityCalculator wqc = new WorkerEstimator(
								LabelProbabilityDistributionCostCalculators.get(lc));
						line.add(Quality.fromCost(project, wqc.getCost(project, w)));
					}
					ret.add(line);
				}

				return ret;
			}
		}
	}
}
