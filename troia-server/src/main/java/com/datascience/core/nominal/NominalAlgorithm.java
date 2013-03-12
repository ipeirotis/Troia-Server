package com.datascience.core.nominal;

import com.datascience.core.base.Algorithm;
import com.datascience.core.base.WorkerResult;
import com.datascience.core.stats.IErrorRateCalculator;
import com.datascience.gal.DatumResult;

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
