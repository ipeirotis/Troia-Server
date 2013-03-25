package com.datascience.core.commands;

import com.datascience.core.base.Project;
import com.datascience.core.base.Worker;
import com.datascience.executor.JobCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author artur
 */
public class WorkerCommands {
	
	static public class GetWorkers<T> extends JobCommand<Collection<Map<String, Object>>, Project> {
		
		public GetWorkers(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			Collection<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
			for (Worker<T> w : (Collection<Worker<T>>) project.getData().getWorkers()){
				Map<String, Object> workerMap = new HashMap<String, Object>();
				workerMap.put("name", w.getName());
				workerMap.put("assigns", w.getAssigns());
				ret.add(workerMap);
			}
			setResult(ret);
		}
	}
	
	static public class GetWorker<T> extends JobCommand<Map<String, Object>, Project> {
				
		String workerId;
		public GetWorker(String workerId){
			super(false);
			this.workerId = workerId;
		}
		
		@Override
		protected void realExecute() {
			Worker<T> w = ParamChecking.worker(project.getData(), workerId);
			Map<String, Object> ret = new HashMap<String, Object>();
			ret.put("name", w.getName());
			ret.put("assigns", w.getAssigns());
			setResult(ret);
		}
	}
}
