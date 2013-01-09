package com.datascience.gal.commands;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.Quality;
import com.datascience.gal.Worker;
import com.datascience.gal.decision.ILabelProbabilityDistributionCostCalculator;
import com.datascience.gal.decision.WorkerEstimator;
import com.datascience.gal.decision.WorkerQualityCalculator;
import com.datascience.gal.evaluation.WorkerEvaluator;

/**
 *
 * @author konrad
 */
public class WorkerCommands {
	
	static public class GetWorker extends ProjectCommand<Object> {

		private String workerId;
		
		public GetWorker(AbstractDawidSkene ads, String workerId){
			super(ads, false);
			this.workerId = workerId;
		}
		
		@Override
		void realExecute() {
			setResult(ads.getWorker(workerId));
		}
	}
	
	static public class GetWorkers extends ProjectCommand<Collection<Worker>> {
		
		public GetWorkers(AbstractDawidSkene ads){
			super(ads, false);
		}
		
		@Override
		void realExecute() {
			setResult(ads.getWorkers());
		}
	}
	
	static public class GetWorkersScores extends ProjectCommand<Collection<Map<String, Object>>> {
		public GetWorkersScores(AbstractDawidSkene ads){
			super(ads, false);
		}
		
		@Override
		void realExecute() {
			setResult(ads.getAllWorkerScores(true));
		}
	}
	
	static public class GetWorkerScores extends ProjectCommand<Map<String, Object>> {
		
		private String workerId;
		public GetWorkerScores(AbstractDawidSkene ads, String wid){
			super(ads, false);
			workerId = wid;
		}
		
		@Override
		void realExecute() {
			setResult(ads.getWorkerScore(ads.getWorker(workerId), true));
		}
	}
	
	static public class GetWorkersQuality extends ProjectCommand<Map<String, Double>> {
		private AbstractDawidSkene ds;
		private WorkerQualityCalculator wqc;
		
		public GetWorkersQuality(AbstractDawidSkene ads, WorkerQualityCalculator wqc){
			super(ads, false);
			this.ds = ads;
			this.wqc = wqc;
		}
		
		@Override
		void realExecute() {
			Map<String, Double> result = new HashMap<String, Double>();
			for (Worker w : ds.getWorkers()){
				result.put(w.getName(), wqc.getCost(ds, w));
			}
			setResult(Quality.fromCosts(ads, result));
		}
	}
}
