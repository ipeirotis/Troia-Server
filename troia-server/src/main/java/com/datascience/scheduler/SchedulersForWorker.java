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
		protected CostBasedPriorityCalculator costCalculator;

		protected void setInitializationData(Results<String, DatumResult, WorkerResult> results, CostBasedPriorityCalculator priorityCalculator){

			costCalculator = priorityCalculator;
			// ^^ I know that this is nasty to use this as cost calculator ...
			decisionEngine = priorityCalculator.dEngine;
			this.results = results;
		}

		@Override
		public LObject<String> nextObjectForWorker(Iterator<LObject<String>> objects, Worker<String> worker) {
			if (!results.hasWorkerResult(worker)){
				return (objects.hasNext()) ? objects.next() : null;
			}
			WorkerResult wr = results.getWorkerResult(worker);
			int i = NUMBER_OF_FIRST_OBJECTS_TO_CHECK;
			double maxCostReduction = Double.MIN_VALUE;
			LObject<String> bestObject = null;
			while (i-- > 0 && objects.hasNext()) {
				LObject<String> object = objects.next();
				double currentObjectCost = costCalculator.getPriority(object);
				if (currentObjectCost <= maxCostReduction) break;
				double predictedNewCost = predictedNewCost(object, wr);
				if (currentObjectCost - predictedNewCost > maxCostReduction) {
					bestObject = object;
					maxCostReduction = currentObjectCost - predictedNewCost;
				}
			}
			return bestObject;
		}

		protected double predictedNewCost(LObject<String> object, WorkerResult wr){
			//TODO: implement this
			return 0.;
		}
	}

	public static class FirstNotSeen<T> implements ISchedulerForWorker<T> {

		protected Project<T, ?, ?, ?> project;

		public FirstNotSeen(Project<T, ?, ?, ?> project){
			this.project = project;
		}

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
	}
}
