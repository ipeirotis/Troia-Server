package com.datascience.gal.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.datascience.gal.Datum;
import com.datascience.gal.Quality;
import com.datascience.gal.Worker;
import com.datascience.gal.WorkerValue;
import com.datascience.gal.decision.WorkerQualityCalculator;

/**
 *
 * @author konrad
 */
public class WorkerCommands {
	
	static public class GetWorker extends DSCommandBase<Map<String, Object>> {

		private String workerId;
		
		public GetWorker(String workerId){
			super(false);
			this.workerId = workerId;
		}
		
		@Override
		protected void realExecute() {
			Worker w = ads.getWorker(workerId);
			setResult(w.getInfo(ads.getObjects(), ads.getCategories().keySet()));
		}	
	}
	
	
	static public class GetWorkers extends DSCommandBase<Map<String, Map<String, Object>>> {
		
		public GetWorkers(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			Map<String, Map<String, Object>> workerInfos = new HashMap<String, Map<String,Object>>();
			Map<String, Datum> objects = ads.getObjects();
			Collection<String> categories = ads.getCategories().keySet();
			for (Worker w : ads.getWorkers()){
				workerInfos.put(w.getName(), w.getInfo(objects, categories));
			}
			setResult(workerInfos);
		}
	}
	
	static public class GetWorkersQuality extends DSCommandBase<Collection<WorkerValue>> {
		private WorkerQualityCalculator wqc;
		
		public GetWorkersQuality(WorkerQualityCalculator wqc){
			super(false);
			this.wqc = wqc;
		}
		
		@Override
		protected void realExecute() {
			Map<String, Double> result = new HashMap<String, Double>();
			Collection<WorkerValue> wq = new ArrayList<WorkerValue>();
			for (Worker w : ads.getWorkers()){
				result.put(w.getName(), wqc.getCost(ads, w));
			}
			for (Entry<String, Double> e : Quality.fromCosts(ads, result).entrySet()){
				wq.add(new WorkerValue(e.getKey(), e.getValue()));
			}
			setResult(wq);
		}
	}
}
