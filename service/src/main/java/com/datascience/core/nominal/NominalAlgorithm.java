package com.datascience.core.nominal;

import com.datascience.core.base.Algorithm;
import com.datascience.core.results.WorkerResult;
import com.datascience.core.stats.IErrorRateCalculator;
import com.datascience.core.results.DatumResult;

/**
 * @Author: konrad
 */
public abstract class NominalAlgorithm extends Algorithm<String, INominalData, DatumResult, WorkerResult> {

	protected IErrorRateCalculator errorRateCalculator;
	protected ICategoryPriorCalculator priorCalculator;
	protected NominalModel model;

	public NominalAlgorithm(IErrorRateCalculator errorRateCalculator, ICategoryPriorCalculator priorCalculator){
		this.errorRateCalculator = errorRateCalculator;
		this.priorCalculator = priorCalculator;
		model = new NominalModel();
	}

	public IErrorRateCalculator getErrorRateCalculator(){
		return errorRateCalculator;
	}

	public void initializeOnCategories(){
		if (!data.arePriorsFixed()) {
			priorCalculator.initializeModelPriors(data, getModel());
		}
	}

	public double prior(String categoryName) {
		return priorCalculator.getPrior(data, getModel(), categoryName);
	}

	@Override
	public NominalModel getModel(){
		return model;
	}
}
