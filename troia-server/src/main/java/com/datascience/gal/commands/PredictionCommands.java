package com.datascience.gal.commands;

import com.datascience.gal.*;
import com.datascience.gal.decision.*;
import com.google.common.base.Joiner;
import org.joda.time.DateTime;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author artur
 */
public class PredictionCommands {
	
	static public class Compute extends DSCommandBase<Object> {

		private int iterations;
		
		public Compute(int iterations){
			super(true);
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

		public GetPredictedCategory(ILabelProbabilityDistributionCalculator lpd,
				IObjectLabelDecisionAlgorithm lda){
			super(false);
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
		
		public GetCost(ILabelProbabilityDistributionCalculator lpd,
				ILabelProbabilityDistributionCostCalculator lca){
			super(false);
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
		
		public GetQuality(ILabelProbabilityDistributionCalculator lpd,
				ILabelProbabilityDistributionCostCalculator lca){
			super(false);
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

	static public class GetPredictionZip extends DSCommandBase<String> {

		private String[] lpdc = new String[]{"DS", "MV"};
		private String[] lda = new String[]{"MaxLikelihood", "MinCost"};
		private String[] lca = new String[]{"MaxLikelihood", "MinCost", "ExpectedCost"};
		private String path;

		public GetPredictionZip(String path){
			super(false);
			this.path = path;
		}

		abstract class GetStatistics {

			public List<List<Object>> call() {
				return null;
			}
			public void generateToStream(List<List<Object>> data, String separator, OutputStream os) throws IOException {
				Joiner j = Joiner.on(separator);
				for (List<Object> lo : data){
					os.write((j.join(lo) + "\n").getBytes());
				}
			}
		}

		class GetDataPrediction extends GetStatistics {

			@Override
			public List<List<Object>> call(){
				List<List<Object>> ret = new ArrayList<List<Object>>();
				List<Object> header = new ArrayList<Object>();

				header.add("");
				for (String lpd : lpdc)
					for (String la : lda)
						header.add(lpd+" "+la);
				ret.add(header);

				for (Entry<String, Datum> d : ads.getObjects().entrySet()){
					List<Object> line = new ArrayList<Object>();
					line.add(d.getKey());
					for (String lpd : lpdc)
						for (String la : lda){
							ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
							IObjectLabelDecisionAlgorithm olda = ObjectLabelDecisionAlgorithms.get(la);
							DecisionEngine decisionEngine = new DecisionEngine(lpdc, null, olda);
							line.add(decisionEngine.predictLabel(ads, d.getValue()));
						}
					ret.add(line);
				}

				return ret;
			}
		}

		class GetWorkersQuality extends GetStatistics {

			public List<List<Object>> call(){
				List<List<Object>> ret = new ArrayList<List<Object>>();
				List<Object> header = new ArrayList<Object>();

				header.add("");
				for (String lc : lca)
					header.add(lc);
				ret.add(header);

				for (Worker w : ads.getWorkers()){
					List<Object> line = new ArrayList<Object>();
					line.add(w.getName());
					for (String lc : lca){
						ILabelProbabilityDistributionCostCalculator lpdcc = LabelProbabilityDistributionCostCalculators.get(lc);
						WorkerQualityCalculator wqc = new WorkerEstimator(lpdcc);
						line.add(wqc.getCost(ads, w));
					}
					ret.add(line);
				}

				return ret;
			}
		}

		@Override
		protected void realExecute(){

			String fileName = "";
			try{
				fileName = DateTime.now() + "_" + ads.getId() + ".zip";
				FileOutputStream fos = new FileOutputStream(path + fileName);
				ZipOutputStream zos = new ZipOutputStream(fos);

				HashMap<String, GetStatistics> files = new HashMap<String, GetStatistics>();
				files.put("prediction.tsv", new GetDataPrediction());
				files.put("workers_quality.tsv", new GetWorkersQuality());

				for(Entry<String, GetStatistics> e : files.entrySet()){
					ZipEntry ze= new ZipEntry(e.getKey());
					zos.putNextEntry(ze);
					e.getValue().generateToStream(e.getValue().call(), "\t", zos);
				}
				zos.closeEntry();
				zos.close();
			} catch (FileNotFoundException e) {
				Logger.getAnonymousLogger().warning(e.getLocalizedMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Logger.getAnonymousLogger().warning(e.getLocalizedMessage());
				e.printStackTrace();
			}
			setResult("/media/downloads/"+fileName);
		}
	}
}
