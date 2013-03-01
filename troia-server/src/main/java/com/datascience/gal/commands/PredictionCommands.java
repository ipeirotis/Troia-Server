package com.datascience.gal.commands;

import com.datascience.gal.*;
import com.datascience.gal.decision.*;
import org.joda.time.DateTime;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
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

		interface IGetTSVFile{
			public String call();
		}

		class GetPredictionTSV implements IGetTSVFile{

			public String call(){
				StringBuilder sb = new StringBuilder();

				sb.append("\t");
				for (String lpd : lpdc)
					for (String la : lda)
						sb.append(lpd+" "+la+"\t");
				sb.append("\n");

				for (Entry<String, Datum> d : ads.getObjects().entrySet()){
					sb.append(d.getKey()+"\t");
					for (String lpd : lpdc)
						for (String la : lda){
							ILabelProbabilityDistributionCalculator lpdc = LabelProbabilityDistributionCalculators.get(lpd);
							IObjectLabelDecisionAlgorithm olda = ObjectLabelDecisionAlgorithms.get(la);
							DecisionEngine decisionEngine = new DecisionEngine(lpdc, null, olda);
							sb.append(decisionEngine.predictLabel(ads, d.getValue()) + "\t");
						}
					sb.append("\n");
				}

				return sb.toString();
			}
		}

		class GetWorkersQualityTSV implements  IGetTSVFile{

			public String call(){
				StringBuffer sb = new StringBuffer();

				sb.append("\t");
				for (String lc : lca)
					sb.append(lc+"\t");
				sb.append("\n");

				for (Worker w : ads.getWorkers()){
					sb.append(w.getName()+"\t");
					for (String lc : lca){
						ILabelProbabilityDistributionCostCalculator lpdcc = LabelProbabilityDistributionCostCalculators.get(lc);
						WorkerQualityCalculator wqc = new WorkerEstimator(lpdcc);
						sb.append(wqc.getCost(ads, w) + "\t");
					}
					sb.append("\n");
				}

				return sb.toString();
			}
		}

		@Override
		protected void realExecute(){

			byte[] buffer = new byte[1024];
			String fileName = "";
			try{
				fileName = DateTime.now() + "_" + ads.getId() + ".zip";
				FileOutputStream fos = new FileOutputStream(path + fileName);
				ZipOutputStream zos = new ZipOutputStream(fos);

				HashMap<String, IGetTSVFile> files = new HashMap<String, IGetTSVFile>();
				files.put("prediction.tsv", new GetPredictionTSV());
				files.put("workers_quality.tsv", new GetWorkersQualityTSV());

				for(Entry<String, IGetTSVFile> e : files.entrySet()){
					ZipEntry ze= new ZipEntry(e.getKey());
					zos.putNextEntry(ze);
					InputStream is = new ByteArrayInputStream(e.getValue().call().getBytes());
					int len;
					while ((len = is.read(buffer)) > 0) {
						zos.write(buffer, 0, len);
					}
					is.close();
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
