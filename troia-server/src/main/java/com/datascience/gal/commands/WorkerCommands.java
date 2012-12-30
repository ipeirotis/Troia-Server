package com.datascience.gal.commands;

import java.util.Collection;
import java.util.Map;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.Worker;
import com.datascience.gal.WorkerCostMethod;

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
	
	static public class GetWorkerCost extends ProjectCommand<Double> {
		
		private String workerId;
		private WorkerCostMethod workerCostMethod;
		
		public GetWorkerCost(AbstractDawidSkene ads, String wid, WorkerCostMethod wcm){
			super(ads, false);
			workerId = wid;
			workerCostMethod = wcm;
		}
		
		@Override
		void realExecute() {
			setResult(ads.getWorkerCost(ads.getWorker(workerId), workerCostMethod));
		}
	}
}
