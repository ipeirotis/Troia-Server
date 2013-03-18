package com.datascience.core.nominal;

import com.datascience.core.base.Algorithm;
import com.datascience.core.base.Category;
import com.datascience.core.base.LObject;
import com.datascience.core.results.WorkerResult;
import com.datascience.core.stats.IErrorRateCalculator;
import com.datascience.core.results.DatumResult;

import java.util.Collection;
import java.util.Map;

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

	public abstract void initializeOnCategories(Collection<Category> categories);

}
