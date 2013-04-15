package com.datascience.gal.commands;

import com.datascience.core.base.LObject;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.base.Worker;
import com.datascience.core.results.WorkerResult;
import com.datascience.core.stats.ConfusionMatrix;
import com.datascience.core.stats.MatrixValue;
import com.datascience.executor.JobCommand;
import com.datascience.gal.*;
import com.datascience.core.nominal.decision.*;

import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author artur
 */
public class PredictionCommands {

	static public class GetWorkersConfusionMatrix extends JobCommand<Collection<WorkerValue<Collection<MatrixValue<String>>>>, NominalProject> {

		public GetWorkersConfusionMatrix(){
			super(false);
		}

		@Override
		protected void realExecute() {
			Collection<WorkerValue<Collection<MatrixValue<String>>>> wq = new ArrayList<WorkerValue<Collection<MatrixValue<String>>>>();
			for (Entry<Worker<String>, WorkerResult> e : project.getResults().getWorkerResults().entrySet()){
				Collection<MatrixValue<String>> matrix = new ArrayList<MatrixValue<String>>();
				ConfusionMatrix confusionMatrix = e.getValue().getConfusionMatrix();
				for (String c1 : confusionMatrix.getCategories())
					for (String c2 : confusionMatrix.getCategories())
						matrix.add(new MatrixValue<String>(c1, c2, confusionMatrix.getNormalizedErrorRate(c1, c2)));
				wq.add(new WorkerValue<Collection<MatrixValue<String>>>(e.getKey().getName(), matrix));
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
			Collection<WorkerValue<Double>> wq = new LinkedList<WorkerValue<Double>>();
			for (Worker<String> w : project.getData().getWorkers()){
				wq.add(new WorkerValue<Double>(w.getName(), wqc.getQuality(project, w)));
			}
			setResult(wq);
		}
	}

	static public class GetWorkersQualitySummary extends JobCommand<Map<String, Object>, NominalProject> {

		public GetWorkersQualitySummary(){
			super(false);
		}

		@Override
		protected void realExecute() throws Exception {
			HashMap<String, Object> ret = new HashMap<String, Object>();
			for (String s : new String[] {"ExpectedCost", "MinCost", "MaxLikelihood"}){
				WorkerQualityCalculator wqc = new WorkerEstimator(LabelProbabilityDistributionCostCalculators.get(s));
				ret.put(s, Quality.getAverage(project, wqc.getCosts(project)) );
			}
			setResult(ret);
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

	static public class GetDataQualitySummary extends JobCommand<Map<String, Object>, NominalProject> {

		public GetDataQualitySummary(){
			super(false);
		}

		@Override
		protected void realExecute() throws Exception {
			HashMap<String, Object> ret = new HashMap<String, Object>();
			for (String s : new String[] {"ExpectedCost", "MinCost", "MaxLikelihood"}){
				ILabelProbabilityDistributionCostCalculator lpdcc = LabelProbabilityDistributionCostCalculators.get(s);
				DecisionEngine de = new DecisionEngine(lpdcc, null);
				ret.put(s, Quality.getAverage(project, de.estimateMissclassificationCosts(project)));
			}
			setResult(ret);
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
