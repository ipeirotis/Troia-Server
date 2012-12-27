package com.datascience.gal.commands;

import java.util.Map;

import com.datascience.gal.AbstractDawidSkene;

/**
 *
 * @author artur
 */
public class PredictionCommands {
	
	static public class Compute extends ProjectCommand<Object> {

		private int iterations;
		
		public Compute(AbstractDawidSkene ads, int iterations){
			super(ads, true);
			this.iterations = iterations;
		}
		
		@Override
		void realExecute() {
			ads.estimate(iterations);
			ads.setComputed(true);
			setResult("Compute started");
		}
	}
	
	static public class GetData extends ProjectCommand<Map<String, String>> {
		
		private String algorithm;
		
		public GetData(AbstractDawidSkene ads, String algorithm){
			super(ads, false);
			this.algorithm = algorithm;
		}
		
		@Override
		void realExecute() {
			if (algorithm.equals("MV")){
				setResult(ads.getMajorityVote());
			}
			else {
				//TODO:
			}
		}
	}
	
	static public class GetDatum extends ProjectCommand<String> {
		
		private String datumId;
		private String algorithm;
		
		public GetDatum(AbstractDawidSkene ads, String datumId, String algorithm){
			super(ads, false);
			this.datumId = datumId;
			this.algorithm = algorithm;
		}
		
		@Override
		void realExecute() {
			if (algorithm.equals("MV")){
				setResult(ads.getMajorityVote(datumId));
			}
			else {
				//TODO:
			}
		}
	}
	
	static public class GetCost extends ProjectCommand<Double> {
		
		private String datumId;
		private String algorithm;
		private String costDecisionAlg;
		
		public GetCost(AbstractDawidSkene ads, String datumId, String algorithm, String costDecisionAlgorithm){
			super(ads, false);
			this.datumId = datumId;
			this.algorithm = algorithm;
			this.costDecisionAlg = costDecisionAlgorithm;
		}
		
		@Override
		void realExecute() {
			//TODO:
		}
	}
}
