package com.datascience.core.nominal;

import com.datascience.core.base.Algorithm;
import com.datascience.core.results.WorkerResult;
import com.datascience.core.stats.IErrorRateCalculator;
import com.datascience.core.results.DatumResult;
import com.datascience.datastoring.datamodels.memory.NominalModel;

import java.util.Map;

/**
 * @Author: konrad
 */
public abstract class NominalAlgorithm extends Algorithm<String, INominalData, DatumResult, WorkerResult> {

	protected IErrorRateCalculator errorRateCalculator;
	protected ICategoryPriorCalculator priorCalculator;
	protected INominalModel model;

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

	public Map<String, Double> getCategoryPriors() {
		return priorCalculator.getPriors(data, getModel());
	}

	@Override
	public INominalModel getModel(){
		return model;
	}

	@Override
	public void setModel(Object o){
		model = (INominalModel) o;
	}

}
