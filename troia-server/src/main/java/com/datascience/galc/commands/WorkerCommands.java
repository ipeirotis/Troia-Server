package com.datascience.galc.commands;

import java.util.Collection;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.Worker;
import com.datascience.executor.JobCommand;
import com.datascience.galc.ContinuousProject;

/**
 *
 * @author artur
 */
public class WorkerCommands {
	
	static public class GetWorkers extends JobCommand<Collection<Worker<ContValue>>, ContinuousProject> {
		
		public GetWorkers(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getData().getWorkers());
		}
	}
	
	static public class GetWorker extends JobCommand<Worker<ContValue>, ContinuousProject> {
				
		String workerId;
		public GetWorker(String workerId){
			super(false);
			this.workerId = workerId;
		}
		
		@Override
		protected void realExecute() {
			setResult(ParamChecking.worker(project.getData(), workerId));
		}
	}
}
