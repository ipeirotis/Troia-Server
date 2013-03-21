package com.datascience.mv;

import com.datascience.core.algorithms.INewDataObserver;
import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;

/**
 * @Author: konrad
 */
public class IncrementalMV extends MajorityVote implements INewDataObserver {

	public IncrementalMV(){
		super(null); // TODO FIXME XXX set proper error rate calculator
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
