package com.datascience.mv;

import com.datascience.core.base.LObject;

/**
 * @Author: konrad
 */
public class BatchMV extends MajorityVote {

	public BatchMV(){
		super(null); // TODO FIXME XXX set proper error rate calculator
	}

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

}
