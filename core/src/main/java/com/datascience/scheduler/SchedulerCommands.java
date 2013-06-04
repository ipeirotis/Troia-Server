package com.datascience.scheduler;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.core.base.Worker;
import com.datascience.datastoring.jobs.JobCommand;

/**
 * @Author: konrad
 * If no object could be found than empty results is returned
 */
public class SchedulerCommands {

	static public class GetNextObject<T> extends JobCommand<LObject<T>, Project>{

		public GetNextObject(){
			super(true); // because we modify scheduler state
		}

		protected IScheduler<T> checkAndgetScheduler(){
			IScheduler<T> scheduler = project.getScheduler();
			if (scheduler == null) {
				throw new IllegalArgumentException("This job don't have scheduler enabled!");
			}
			return scheduler;
		}

		protected LObject<T> getNextObject(){
			return checkAndgetScheduler().nextObject();
		}

		@Override
		protected void realExecute() {
			setResult(getNextObject());
		}
	}

	static public class GetNextObjectForWorker<T> extends GetNextObject<T>{

		protected String workerName;

		public GetNextObjectForWorker(String worker){
			super();
			this.workerName = worker;
		}

		@Override
		protected LObject<T> getNextObject(){
			Worker worker = project.getData().getOrCreateWorker(workerName);
			return checkAndgetScheduler().nextObject(worker);
		}
	}
}
