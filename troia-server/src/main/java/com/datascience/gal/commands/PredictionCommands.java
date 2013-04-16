package com.datascience.gal.commands;

import com.datascience.core.base.LObject;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.base.Worker;
import com.datascience.core.results.WorkerResult;
import com.datascience.core.stats.ConfusionMatrix;
import com.datascience.core.jobs.JobCommand;
import com.datascience.gal.*;
import com.datascience.core.nominal.decision.*;

import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author artur
 */
public class PredictionCommands {

	static public class GetWorkersConfusionMatrix extends JobCommand<Collection<WorkerValue<ConfusionMatrix>>, NominalProject> {

		public GetWorkersConfusionMatrix(){
			super(false);
		}

		@Override
		protected void realExecute() {
			Collection<WorkerValue<ConfusionMatrix>> wq = new ArrayList<WorkerValue<ConfusionMatrix>>();
			Map<Worker<String>, WorkerResult> allWorkersResults =
					project.getResults().getWorkerResults(project.getData().getWorkers());
			for (Entry<Worker<String>, WorkerResult> e : allWorkersResults.entrySet()){
				wq.add(new WorkerValue<ConfusionMatrix>(e.getKey().getName(), e.getValue().cm));
			}
			setResult(wq);
		}
	}

	static public class GetWorkersQuality extends JobCommand<Collection<WorkerValue<Double>>, NominalProject> {
		private WorkerQualityCalculator wqc;

		public GetWorkersQuality(WorkerQualityCalculator wqc){
			super(false);
			this.wqc = wqc;
		}

		@Override
		protected void realExecute() {
			Map<String, Double> result = new HashMap<String, Double>();
			Collection<WorkerValue<Double>> wq = new ArrayList<WorkerValue<Double>>();
			for (Worker<String> w : project.getData().getWorkers()){
				result.put(w.getName(), wqc.getCost(project, w));
			}
			for (Entry<String, Double> e : Quality.fromCosts(project, result).entrySet()){
				wq.add(new WorkerValue<Double>(e.getKey(), e.getValue()));
			}
			setResult(wq);
		}
	}

	static public class GetPredictedCategory extends JobCommand<Collection<DatumClassification>, NominalProject> {
		
		private DecisionEngine decisionEngine;

		public GetPredictedCategory(IObjectLabelDecisionAlgorithm lda){
			super(false);
			decisionEngine = new DecisionEngine(null, lda);
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

	static public class GetDataCost extends JobCommand<Collection<DatumValue>, NominalProject> {
		
		private DecisionEngine decisionEngine;
		
		public GetDataCost(ILabelProbabilityDistributionCostCalculator lca){
			super(false);
			decisionEngine = new DecisionEngine(lca, null);
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
	
	static public class GetDataQuality extends JobCommand<Collection<DatumValue>, NominalProject> {
		
		private DecisionEngine decisionEngine;
		
		public GetDataQuality(ILabelProbabilityDistributionCostCalculator lca){
			super(false);
			decisionEngine = new DecisionEngine(lca, null);
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
				String[] lda = new String[]{"MaxLikelihood", "MinCost"};
				List<List<Object>> ret = new ArrayList<List<Object>>();

				List<Object> header = new ArrayList<Object>();
				header.add("");
				for (String la : lda)
					header.add(la);
				ret.add(header);

				for (LObject<String> d : project.getData().getObjects()){
					List<Object> line = new ArrayList<Object>();
					line.add(d.getName());
					for (String la : lda){
						DecisionEngine decisionEngine = new DecisionEngine(
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
