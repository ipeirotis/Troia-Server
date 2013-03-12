package com.datascience.mv;

import com.datascience.core.base.LObject;
import com.datascience.core.base.ResultsFactory;
import com.datascience.gal.ErrorRateCalculators;

/**
 * @Author: konrad
 */
public class BatchMV extends MajorityVote {

	@Override
	public void compute() {
		computeForObjects();
		computeForWorkers();
	}

	public void computeForObjects(){
		for (LObject<String> object: getData().getObjects()){
			computeResultsForObject(object);
		}
	}

	public void computeForWorkers(){
		// TODO XXX FIXME what it should do here?
	}

	@Override
	public ResultsFactory.IWorkerResultCreator getWorkerResultCreator() {
		return new ResultsFactory.WorkerResultNominalFactory(
				new ErrorRateCalculators.BatchErrorRateCalculator(),
				data.getCategories());
	}
}
