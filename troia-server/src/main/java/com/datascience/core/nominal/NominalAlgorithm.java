package com.datascience.core.nominal;

import com.datascience.core.base.Algorithm;
import com.datascience.core.base.Category;
import com.datascience.core.results.WorkerResult;
import com.datascience.core.stats.IErrorRateCalculator;
import com.datascience.gal.DatumResult;

import java.util.Collection;

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
