package com.datascience.core.base;

import com.datascience.core.stats.IErrorRateCalculator;
import com.datascience.gal.DatumResult;
import com.datascience.gal.WorkerResult;

/**
 * @Author: konrad
 */
public abstract class NominalAlgorithm extends Algorithm<String, NominalData, DatumResult, WorkerResult> {

	protected IErrorRateCalculator errorRateCalculator;

	public NominalAlgorithm(IErrorRateCalculator errorRateCalculator){
		this.errorRateCalculator = errorRateCalculator;
	}

	public IErrorRateCalculator getErrorRateCalculator(){
		return errorRateCalculator;
	}
}
