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
		algorithm.estimate(epsilon, iterations);
	}
	
	public Map<LObject<ContValue>, DatumContResults> getDataPrediction(){
		return algorithm.getObjectsResults();
	}

	public Map<Worker<ContValue>, WorkerContResults> getWorkerPrediction(){
		return algorithm.getWorkersResults();
	}
}
