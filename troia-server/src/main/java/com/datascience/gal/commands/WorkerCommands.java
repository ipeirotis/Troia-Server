package com.datascience.gal.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.Quality;
import com.datascience.gal.Worker;
import com.datascience.gal.WorkerValue;
import com.datascience.gal.decision.WorkerEstimator;
import com.datascience.gal.decision.WorkerQualityCalculator;

/**
 *
 * @author konrad
 */
public class WorkerCommands {
	
	static public class GetWorker extends DSCommandBase<Object> {

		private String workerId;
		
		public GetWorker(AbstractDawidSkene ads, String workerId){
			super(ads, false);
			this.workerId = workerId;
		}
		
		@Override
		protected void realExecute() {
			setResult(ads.getWorker(workerId));
		}
	}
	
	static public class GetWorkers extends DSCommandBase<Collection<Worker>> {
		
		public GetWorkers(AbstractDawidSkene ads){
			super(ads, false);
		}
		
		@Override
		protected void realExecute() {
			setResult(ads.getWorkers());
		}
	}
	
	static public class GetWorkersScores extends DSCommandBase<Collection<Map<String, Object>>> {
		private AbstractDawidSkene ds;
		private WorkerEstimator we;
		
		public GetWorkersScores(AbstractDawidSkene ads, WorkerEstimator we){
			super(ads, false);
			this.we = we;
			this.ds = ads;
		}
		
		@Override
		protected void realExecute() {
			Collection<Map<String, Object>> result = new LinkedList<Map<String, Object>>();
			for (Worker w : ds.getWorkers()){
				result.add(we.getScore(ds, w));
			}
			setResult(result);
		}
	}
	
	static public class GetWorkersQuality extends DSCommandBase<Collection<WorkerValue>> {
		private AbstractDawidSkene ds;
		private WorkerQualityCalculator wqc;
		
		public GetWorkersQuality(AbstractDawidSkene ads, WorkerQualityCalculator wqc){
			super(ads, false);
			this.ds = ads;
			this.wqc = wqc;
		}
		
		@Override
		protected void realExecute() {
			Map<String, Double> result = new HashMap<String, Double>();
			Collection<WorkerValue> wq = new ArrayList<WorkerValue>();
			for (Worker w : ds.getWorkers()){
				result.put(w.getName(), wqc.getCost(ds, w));
			}
			for (Entry<String, Double> e : Quality.fromCosts(ads, result).entrySet()){
				wq.add(new WorkerValue(e.getKey(), e.getValue()));
			}
			setResult(wq);
		}
	}
}
