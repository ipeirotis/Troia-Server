package com.datascience.mv;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.stats.ErrorRateCalculators;

/**
 * @Author: konrad
 */
public class BatchMV extends MajorityVote {

	public BatchMV(){
		super(new ErrorRateCalculators.BatchErrorRateCalculator());
	}

	@Override
	public void compute() {
		computeForObjects();
		computeForWorkers();
		computeCategoryPriorsIfNeeded();
	}

	public void computeForObjects(){
		for (LObject<String> object: getData().getObjects()){
			computeResultsForObject(object);
		}
	}

	public void computeForWorkers(){
		for (Worker<String> worker: getData().getWorkers()){
			computeWorkersConfusionMatrix(worker);
		}
	}
}
