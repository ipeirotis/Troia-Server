package com.datascience.galc.commands;

import java.util.Collection;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.Worker;
import com.datascience.galc.ContinuousProject;

/**
 *
 * @author artur
 */
public class WorkerCommands {
	
	static public class GetWorkers extends GALCommandBase<Collection<Worker<ContValue>>> {
		
		public GetWorkers(ContinuousProject cp){
			super(cp, false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getData().getWorkers());
		}
	}
	
	static public class GetWorker extends GALCommandBase<Worker<ContValue>> {
				
		String workerId;
		public GetWorker(ContinuousProject cp, String workerId){
			super(cp, false);
			this.workerId = workerId;
		}
		
		@Override
		protected void realExecute() {
			setResult(ParamChecking.worker(project.getData(), workerId));
		}
	}
}
