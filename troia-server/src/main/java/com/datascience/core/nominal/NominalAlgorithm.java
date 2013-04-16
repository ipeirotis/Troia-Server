package com.datascience.core.nominal;

import com.datascience.core.base.Algorithm;
import com.datascience.core.results.WorkerResult;
import com.datascience.core.stats.IErrorRateCalculator;
import com.datascience.core.results.DatumResult;

import java.util.Collection;

/**
 * @Author: konrad
 */
public abstract class NominalAlgorithm extends Algorithm<String, INominalData, DatumResult, WorkerResult> {

	protected IErrorRateCalculator errorRateCalculator;
	protected NominalModel model;

	public NominalAlgorithm(IErrorRateCalculator errorRateCalculator){
		this.errorRateCalculator = errorRateCalculator;
		model = new NominalModel();
	}

	public IErrorRateCalculator getErrorRateCalculator(){
		return errorRateCalculator;
	}

	public abstract void initializeOnCategories(Collection<String> categories);

	@Override
	public NominalModel getModel(){
		return model;
	}

	public double prior(String categoryName) {
		if (data.arePriorsFixed())
			return data.getCategoryPrior(categoryName);
		else
			return model.categoryPriors.get(categoryName);
	}
}
