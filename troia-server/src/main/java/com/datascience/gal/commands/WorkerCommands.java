package com.datascience.gal.commands;

import java.util.Collection;
import java.util.Map;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.Quality;
import com.datascience.gal.Worker;
import com.datascience.gal.decision.DecisionEngine;
import com.datascience.gal.decision.ILabelProbabilityDistributionCostCalculator;

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
		private DecisionEngine decisionEngine;
		
		public GetWorkersQuality(AbstractDawidSkene ads,
				ILabelProbabilityDistributionCostCalculator lca){
			super(ads, false);
			decisionEngine = new DecisionEngine(null, lca, null);
		}
		
		@Override
		void realExecute() {
			setResult(Quality.fromCosts(ads, decisionEngine.estimateWorkersCost(ads)));
		}
	}
	
	static public class GetWorkersCost extends ProjectCommand<Map<String, Double>> {
		private DecisionEngine decisionEngine;
		
		public GetWorkersCost(AbstractDawidSkene ads,
				ILabelProbabilityDistributionCostCalculator lca){
			super(ads, false);
			decisionEngine = new DecisionEngine(null, lca, null);
		}
		
		@Override
		void realExecute() {
			setResult(decisionEngine.estimateWorkersCost(ads));
		}
	}
}
