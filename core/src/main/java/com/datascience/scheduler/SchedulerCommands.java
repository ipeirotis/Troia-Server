package com.datascience.scheduler;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.core.base.Worker;
import com.datascience.core.jobs.JobCommand;

import java.util.NoSuchElementException;

/**
 * @Author: konrad
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

		protected LObject<T> getAndVerifyObject(){
			LObject<T> object = getNextObject();
			if (object == null){
				throw new NoSuchElementException(getEmptyErrorMsg());
			}
			return object;
		}

		protected String getEmptyErrorMsg(){
			return "No object could be found";
		}

		@Override
		protected void realExecute() {
			setResult(getAndVerifyObject());
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
			Worker<T> worker = project.getData().getOrCreateWorker(workerName);
			return checkAndgetScheduler().nextObject(worker);
		}

		@Override
		protected String getEmptyErrorMsg(){
			return "No object could be found for worker " + workerName;
		}
	}
}
