package com.datascience.galc;

import java.util.Map;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;

/**
 * @Author: konrad
 */
public class ContinuousProject {

	protected Data<ContValue> data;
	protected ContinuousIpeirotis algorithm;
	
	public ContinuousProject(){
		data = new Data<ContValue>();
	}

	public Data<ContValue> getData(){
		return data;
	}
	
	public void compute(int iterations, double epsilon){
		algorithm = new ContinuousIpeirotis();
		algorithm.setData(data);
		algorithm.estimate(epsilon, iterations);
	}

	protected void checkComputed(){
		if (algorithm == null) {
			throw new IllegalStateException("Run compute first!");
		}
	}

	public Map<LObject<ContValue>, DatumContResults> getDataPrediction(){
		checkComputed();
		return algorithm.getObjectsResults();
	}

	public Map<Worker<ContValue>, WorkerContResults> getWorkerPrediction(){
		checkComputed();
		return algorithm.getWorkersResults();
	}
}
