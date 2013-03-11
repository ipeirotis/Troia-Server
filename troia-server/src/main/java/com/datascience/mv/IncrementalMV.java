package com.datascience.mv;

import com.datascience.core.algorithms.IUpdatableAlgorithm;
import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;

/**
 * @Author: konrad
 */
public class IncrementalMV extends MajorityVote implements IUpdatableAlgorithm{

	@Override
	public void compute() {
		//There is nothing that we would make sense to do here
	}

	@Override
	public void newAssign(AssignedLabel assign) {
		computeResultsForObject(assign.getLobject());
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
