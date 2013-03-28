package com.datascience.scheduler;

import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.decision.DecisionEngine;
import com.datascience.core.results.DatumResult;
import com.datascience.core.results.Results;
import com.datascience.core.results.WorkerResult;

import java.util.Iterator;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @Author: konrad
 */
public class SchedulersForWorker {

	/**
	 * The goal of this scheduling is to maximalize predicted cost decrease using
	 * workers confusion matrix
	 */
	public static class ConfusionMatrixBased implements ISchedulerForWorker<String> {

		protected int NUMBER_OF_FIRST_OBJECTS_TO_CHECK = 100;

		protected Results<String, DatumResult, WorkerResult> results;
		protected DecisionEngine decisionEngine;

		@Override
		public LObject<String> nextObjectForWorker(Iterator<LObject<String>> objects, Worker<String> worker) {
			if (!results.hasWorkerResult(worker)){
				return (objects.hasNext()) ? objects.next() : null;
			}
			WorkerResult wr = results.getWorkerResult(worker);

			// TODO: finish this
			return null;
		}

		@Override
		public String getId(){
			return Constants.FOR_WORKERS_CM_BASED;
		}

		@Override
		public void setProject(Project<String, ?, ?, ?> project) {
			checkArgument(project.getScheduler() != null, "Wrong configuration order");
			checkArgument(!(project.getScheduler().getCalculator() instanceof CostBasedPriorityCalculator),
					"This scheduler for worker works only with cost calculator");


			decisionEngine = ((CostBasedPriorityCalculator)
					project.getScheduler().getCalculator()).dEngine;
			results = (Results<String, DatumResult, WorkerResult>) project.getResults();
		}
	}

	public static class FirstNotSeen<T> implements ISchedulerForWorker<T> {

		protected Project<T, ?, ?, ?> project;

		@Override
		public LObject<T> nextObjectForWorker(Iterator<LObject<T>> objects_it, Worker<T> worker) {
			Data<T> data = project.getData();
			while (objects_it.hasNext()) {
				LObject<T> object = objects_it.next();
				if (data.hasAssign(object, worker)) {
					continue;
				}
				return object;
			}
			// this worker has given assign for all objects known to the system ...
			return null;
		}

		@Override
		public String getId(){
			return Constants.FOR_WORKERS_FIRST_NOT_SEEN;
		}

		@Override
		public void setProject(Project<T, ?, ?, ?> project) {
			this.project = project;
		}
	}
}
