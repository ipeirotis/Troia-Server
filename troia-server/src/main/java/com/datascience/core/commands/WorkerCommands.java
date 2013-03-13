package com.datascience.core.commands;

import com.datascience.core.base.Project;
import com.datascience.core.base.Worker;
import com.datascience.executor.JobCommand;

import java.util.Collection;

/**
 *
 * @author artur
 */
public class WorkerCommands {
	
	static public class GetWorkers<T> extends JobCommand<Collection<Worker<T>>, Project> {
		
		public GetWorkers(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getData().getWorkers());
		}
	}
	
	static public class GetWorker<T> extends JobCommand<Worker<T>, Project> {
				
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
