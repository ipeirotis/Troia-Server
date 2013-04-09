package com.datascience.mv;

import com.datascience.core.algorithms.INewDataObserver;
import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.stats.ErrorRateCalculators;

/**
 * @Author: konrad
 */
public class IncrementalMV extends MajorityVote implements INewDataObserver {

	public IncrementalMV(){
		super(new ErrorRateCalculators.BatchErrorRateCalculator());
	}

	@Override
	public void compute() {
		//There is nothing that we would make sense to do here
	}

	public void computeForNewAssign(AssignedLabel<String> assign){
		computeResultsForObject(assign.getLobject());
		for (AssignedLabel<String> al: getData().getAssignsForObject(assign.getLobject())){
			computeWorkersConfusionMatrix(al.getWorker());
		}
	}

	@Override
	public double prior(String categoryName) {
		computeCategoryPriorsIfNeeded();
		return super.prior(categoryName);
	}

	@Override
	public void newAssign(AssignedLabel assign) {
		computeForNewAssign(assign);
	}

	@Override
	public void newGoldObject(LObject object) {
		computeResultsForObject(object);
	}

	@Override
	public void newObject(LObject object) {
		computeResultsForObject(object);
	}

	@Override
	public void newWorker(Worker worker) {
	}
}
