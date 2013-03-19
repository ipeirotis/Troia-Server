package com.datascience.scheduler;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.core.base.Worker;
import com.datascience.executor.JobCommand;

/**
 * @Author: konrad
 */
public class SchedulerCommands {

	static public class GetNextObject<T> extends JobCommand<LObject<T>, Project>{

		public GetNextObject(){
			super(true); // because we modify scheduler state
		}

		protected Scheduler<T> checkAndgetScheduler(){
			Scheduler<T> scheduler = project.getScheduler();
			if (scheduler == null) {
				throw new IllegalArgumentException("This job don't have scheduler enabled!");
			}
			return scheduler;
		}

		@Override
		protected void realExecute() {
			setResult(checkAndgetScheduler().nextObject());
		}
	}

	static public class GetNextObjectForWorker<T> extends GetNextObject<T>{

		protected String workerName;

		public GetNextObjectForWorker(String worker){
			super();
			this.workerName = worker;
		}

		@Override
		protected void realExecute(){
			Worker<T> worker = project.getData().getOrCreateWorker(workerName);
			setResult(checkAndgetScheduler().nextObject(worker));
		}
	}
}
