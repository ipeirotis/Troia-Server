package com.datascience.gal.commands;

import com.datascience.core.base.LObject;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.Quality;
import com.datascience.core.results.WorkerResult;
import com.datascience.core.stats.ConfusionMatrix;
import com.datascience.core.stats.QSPCalculators;
import com.datascience.core.stats.QualitySensitivePaymentsCalculator;
import com.datascience.datastoring.jobs.JobCommand;
import com.datascience.core.stats.MatrixValue;
import com.datascience.gal.*;
import com.datascience.core.nominal.decision.*;
import com.datascience.utils.MathHelpers;

import java.util.*;
import java.util.Map.Entry;

import static com.datascience.core.nominal.Quality.getMinSpammerCost;

/**
 * NOTE: avg Qualit = Qualit (avg Cost)
 * @author artur
 */
public class PredictionCommands {

	static public Collection<MatrixValue<String>> getWorkerConfusionMatrix(ConfusionMatrix confusionMatrix){
		Collection<MatrixValue<String>> matrix = new ArrayList<MatrixValue<String>>();
		for (String c1 : confusionMatrix.getCategories())
			for (String c2 : confusionMatrix.getCategories())
				matrix.add(new MatrixValue<String>(c1, c2, confusionMatrix.getNormalizedErrorRate(c1, c2)));
		return matrix;
	}

	static public class GetWorkersConfusionMatrix extends JobCommand<Collection<WorkerValue<Collection<MatrixValue<String>>>>, NominalProject> {

		public GetWorkersConfusionMatrix(){
			super(false);
		}

		@Override
		protected void realExecute() {
			Collection<WorkerValue<Collection<MatrixValue<String>>>> wq = new ArrayList<WorkerValue<Collection<MatrixValue<String>>>>();
			for (Entry<Worker, WorkerResult> e : project.getResults().getWorkerResults(project.getData().getWorkers()).entrySet()){
				wq.add(new WorkerValue<Collection<MatrixValue<String>>>(e.getKey().getName(), getWorkerConfusionMatrix(e.getValue().getConfusionMatrix())));
			}
			setResult(wq);
		}
	}

	static public class GetWorkerConfusionMatrix extends JobCommand<WorkerValue<Collection<MatrixValue<String>>>, NominalProject> {

		String wid;
		public GetWorkerConfusionMatrix(String wid){
			super(false);
			this.wid = wid;
		}

		@Override
		protected void realExecute() {
			setResult(new WorkerValue<Collection<MatrixValue<String>>>(wid,
					getWorkerConfusionMatrix(project.getResults().getWorkerResult(project.getData().getWorker(wid)).getConfusionMatrix())));
		}
	}

	static public class GetWorkersPayments extends JobCommand<Collection<WorkerValue<Double>>, NominalProject> {

		double qualifiedWage;
		double costThreshold;
		public GetWorkersPayments(double qualifiedWage, double costThreshold){
			super(false);
			this.qualifiedWage = qualifiedWage;
			this.costThreshold = costThreshold;
		}

		@Override
		protected void realExecute() {
			Collection<WorkerValue<Double>> wq = new LinkedList<WorkerValue<Double>>();
			for (Worker w : project.getData().getWorkers()){
				QualitySensitivePaymentsCalculator wspq = new QSPCalculators.Linear(project, w);
				wq.add(new WorkerValue<Double>(w.getName(), wspq.getWorkerWage(qualifiedWage, costThreshold)));
			}
			setResult(wq);
		}
	}

	static public class GetWorkerPayment extends JobCommand<WorkerValue<Double>, NominalProject> {

		double qualifiedWage;
		double costThreshold;
		String wid;

		public GetWorkerPayment(String wid, double qualifiedWage, double costThreshold){
			super(false);
			this.wid = wid;
			this.qualifiedWage = qualifiedWage;
			this.costThreshold = costThreshold;
		}

		@Override
		protected void realExecute() {
			QualitySensitivePaymentsCalculator wspq = new QSPCalculators.Linear(project, project.getData().getWorker(wid));
			setResult(new WorkerValue<Double>(wid, wspq.getWorkerWage(qualifiedWage, costThreshold)));
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
			for (Worker w : project.getData().getWorkers()){
				wq.add(new WorkerValue<Double>(w.getName(), wqc.getQuality(project, w)));
			}
			setResult(wq);
		}
	}

	static public class GetWorkersCost extends JobCommand<Collection<WorkerValue<Double>>, NominalProject> {
		private WorkerQualityCalculator wqc;

		public GetWorkersCost(WorkerQualityCalculator wqc){
			super(false);
			this.wqc = wqc;
		}

		@Override
		protected void realExecute() {
			Collection<WorkerValue<Double>> wq = new LinkedList<WorkerValue<Double>>();
			for (Worker w : project.getData().getWorkers()){
				wq.add(new WorkerValue<Double>(w.getName(), wqc.getCost(project, w)));
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
				ret.put(s, Quality.fromCost(project, MathHelpers.getAverageNotNaN(wqc.getCosts(project))));
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
			for (Entry<LObject<String>, String> e : decisionEngine.predictLabels(project).entrySet()){
				dc.add(new DatumClassification(e.getKey().getName(), e.getValue()));
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
			for (Entry<LObject<String>, Double> e : decisionEngine.estimateMissclassificationCosts(project).entrySet()){
				cp.add(new DatumValue(e.getKey().getName(), e.getValue()));
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
			for (Entry<LObject<String>, Double> e : Quality.fromCosts(project, decisionEngine.estimateMissclassificationCosts(project)).entrySet()){
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
			for (String s : new String[] {"ExpectedCost", "MinCost", "MaxLikelihood"}){
				ILabelProbabilityDistributionCostCalculator lpdcc = LabelProbabilityDistributionCostCalculators.get(s);
				DecisionEngine de = new DecisionEngine(lpdcc, null);
				ret.put(s, MathHelpers.getAverage(de.estimateMissclassificationCosts(project)));
			}
			ret.put("Spammer", getMinSpammerCost(project));
			setResult(ret);
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
				ret.put(s, Quality.fromCost(project, MathHelpers.getAverage(de.estimateMissclassificationCosts(project))));
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
				String[] lda = new String[]{"MinCost"}; // TROIA-393 {"MaxLikelihood", "MinCost"}
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
				String[] lca = new String[]{"MinCost", "ExpectedCost"}; // TROIA-393 {"MaxLikelihood", "MinCost", "ExpectedCost"}
				List<List<Object>> ret = new ArrayList<List<Object>>();
				List<Object> header = new ArrayList<Object>();

				header.add("");
				for (String lc : lca)
					header.add(lc);
				ret.add(header);

				for (Worker w : project.getData().getWorkers()){
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
