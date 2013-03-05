package com.datascience.gal.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.datascience.executor.JobCommand;
import com.datascience.gal.*;
import com.datascience.gal.decision.WorkerQualityCalculator;

/**
 *
 * @author konrad
 */
public class WorkerCommands {
	
	static public class GetWorker extends JobCommand<Map<String, Object>, AbstractDawidSkene> {

		private String workerId;
		
		public GetWorker(String workerId){
			super(false);
			this.workerId = workerId;
		}
		
		@Override
		protected void realExecute() {
			Worker w = project.getWorker(workerId);
			setResult(w.getInfo(project.getObjects(), project.getCategories().keySet()));
		}	
	}
	
	
	static public class GetWorkers extends JobCommand<Map<String, Map<String, Object>>, AbstractDawidSkene> {
		
		public GetWorkers(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			Map<String, Map<String, Object>> workerInfos = new HashMap<String, Map<String,Object>>();
			Map<String, Datum> objects = project.getObjects();
			Collection<String> categories = project.getCategories().keySet();
			for (Worker w : project.getWorkers()){
				workerInfos.put(w.getName(), w.getInfo(objects, categories));
			}
			setResult(workerInfos);
		}
	}
	
	static public class GetWorkersQuality extends JobCommand<Collection<WorkerValue>, AbstractDawidSkene> {
		private WorkerQualityCalculator wqc;
		
		public GetWorkersQuality(WorkerQualityCalculator wqc){
			super(false);
			this.wqc = wqc;
		}
		
		@Override
		protected void realExecute() {
			Map<String, Double> result = new HashMap<String, Double>();
			Collection<WorkerValue> wq = new ArrayList<WorkerValue>();
			for (Worker w : project.getWorkers()){
				result.put(w.getName(), wqc.getCost(project, w));
			}
			for (Entry<String, Double> e : Quality.fromCosts(project, result).entrySet()){
				wq.add(new WorkerValue(e.getKey(), e.getValue()));
			}
			setResult(wq);
		}
	}
}
