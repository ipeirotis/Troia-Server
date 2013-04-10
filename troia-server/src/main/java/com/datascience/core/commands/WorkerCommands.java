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

	protected static <T> Map<String, Object> getWorkerStats(Project project, Worker<T> worker){
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("name", worker.getName());
		ret.put("assigns", project.getData().getWorkerAssigns(worker));
		return ret;
	}

	static public class GetWorkers<T> extends JobCommand<Collection<Map<String, Object>>, Project<T, ?, ?, ?>> {
		
		public GetWorkers(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			Collection<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
			for (Worker<T> w : project.getData().getWorkers()){
				ret.add(getWorkerStats(project, w));
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
			setResult(getWorkerStats(project, w));
		}
	}
}
