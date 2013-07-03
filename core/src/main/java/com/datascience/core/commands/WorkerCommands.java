package com.datascience.core.commands;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.core.base.Worker;
import com.datascience.datastoring.jobs.JobCommand;
import com.datascience.gal.WorkerValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author artur
 */
public class WorkerCommands {

	protected static WorkerValue<Map<String, Object>> getWorkerStats(Project project, Worker worker){
		Map<String, Object> map = new HashMap<String, Object>();
		WorkerValue<Map<String, Object>> ret = new WorkerValue<Map<String, Object>>(worker.getName(), map);
		map.put("assigns", project.getData().getWorkerAssigns(worker).size());
		int goldTests = 0;
		int correctGoldTests = 0;
		for (AssignedLabel al : (Collection<AssignedLabel>) project.getData().getWorkerAssigns(worker)){
			LObject<String> obj = project.getData().getObject(al.getLobject().getName());
			if (obj.isGold()){
				goldTests++;
				if (al.getLabel().equals(obj.getGoldLabel()))
					correctGoldTests++;
			}
		}
		map.put("goldTests", goldTests);
		map.put("correctGoldTests", correctGoldTests);
		return ret;
	}

	static public class GetWorkers extends JobCommand<Collection<WorkerValue<Map<String, Object>>>, Project<?, ?, ?, ?>> {
		
		public GetWorkers(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			Collection<WorkerValue<Map<String, Object>>> ret = new ArrayList<WorkerValue<Map<String, Object>>>();
			for (Worker w : project.getData().getWorkers()){
				ret.add(getWorkerStats(project, w));
			}
			setResult(ret);
		}
	}
	
	static public class GetWorker extends JobCommand<WorkerValue<Map<String, Object>>, Project> {
				
		String workerId;
		public GetWorker(String workerId){
			super(false);
			this.workerId = workerId;
		}
		
		@Override
		protected void realExecute() {
			Worker w = ParamChecking.worker(project.getData(), workerId);
			setResult(getWorkerStats(project, w));
		}
	}
}
