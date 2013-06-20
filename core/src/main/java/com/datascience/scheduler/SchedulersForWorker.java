package com.datascience.scheduler;

import com.datascience.core.base.IData;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.core.base.Worker;
import com.datascience.core.results.DatumResult;
import com.datascience.core.results.IResults;
import com.datascience.core.results.WorkerResult;

import java.util.Collection;
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

		protected IResults<String, DatumResult, WorkerResult> results;
		protected CostBasedPriorityCalculator costCalculator;

		@Override
		public LObject<String> nextObjectForWorker(Iterator<LObject<String>> objects, Worker worker) {
			if (!results.hasWorkerResult(worker)){
				return (objects.hasNext()) ? objects.next() : null;
			}
			WorkerResult wr = results.getWorkerResult(worker);

			int objectsLeftToCheck = NUMBER_OF_FIRST_OBJECTS_TO_CHECK;
			double maxCostReduction = Double.MIN_VALUE;
			LObject<String> bestObject = null;
			while (objectsLeftToCheck-- > 0 && objects.hasNext()){
				LObject<String> object = objects.next();
				double currentCost = costCalculator.getPriority(object);
				if (currentCost < maxCostReduction) continue;
				double costReduced = computeCostReduced(object, wr);
				if (currentCost - costReduced > maxCostReduction){
					bestObject = object;
					maxCostReduction = currentCost - costReduced;
				}
			}
			return bestObject;
		}

		private double computeCostReduced(LObject<String> object, WorkerResult wr) {
			double expNewCost = 0;
			DatumResult dr = results.getOrCreateDatumResult(object);
			Collection<String> categories = dr.getCategoryProbabilites().keySet();
			// What is the probability that the datum is really in true_category
			for (String true_category: categories) {
				double datumProb = dr.getCategoryProbability(true_category);
				// If the datum is in c, then how the worker would label it?
				for (String predicted_category : categories) {
					// Calculate the probability of assigning label_to to the given datum
					double labelProb = wr.getConfusionMatrix().getNormalizedErrorRate(true_category, predicted_category);

					// TODO XXX FIXME - this involves algorithm :/
					// Assuming that the worker assigned label_to, estimate the new cost of the example
//					datum.addAssignedLabel(predicted_category);
//					datum.updateObjectProbability();
//					double costAfterLabeling = datum.calculateCost();
//					expNewCost += datumProb * labelProb * costAfterLabeling;
//					// clean up
//					datum.removeAssignedLabel(label_to)
//					datum.updateObjectProbability();
				}
			}
			return expNewCost;
		}

		@Override
		public String getId(){
			return Constants.FOR_WORKERS_CM_BASED;
		}

		public void setInitializationData(CostBasedPriorityCalculator costCalculator,
										  IResults<String, DatumResult, WorkerResult> results){
			this.costCalculator = costCalculator;
			// ^^^ I know this is dirty to use this, but it is easier in use
			this.results = results;
		}

		@Override
		public void setProject(Project<String, ?, ?, ?> project) {
			checkArgument(project.getScheduler() != null, "Wrong configuration order");
			checkArgument(!(project.getScheduler().getCalculator() instanceof CostBasedPriorityCalculator),
					"This scheduler for worker works only with cost calculator");

			setInitializationData((CostBasedPriorityCalculator) project.getScheduler().getCalculator(),
					(IResults<String, DatumResult, WorkerResult>) project.getResults());
		}
	}

	public static class FirstNotSeen<T> implements ISchedulerForWorker<T> {

		protected Project<T, ?, ?, ?> project;

		@Override
		public LObject<T> nextObjectForWorker(Iterator<LObject<T>> objects_it, Worker worker) {
			IData<T> data = project.getData();
			while (objects_it.hasNext()) {
				LObject<T> object = objects_it.next();
				if (data.hasAssign(object, worker)) {
					continue;
				}
				return object;
			}
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
