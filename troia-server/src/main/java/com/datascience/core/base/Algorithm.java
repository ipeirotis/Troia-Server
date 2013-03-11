package com.datascience.core.base;

import com.datascience.gal.DatumResult;
import com.datascience.gal.WorkerResult;

/**
 * User: artur
 */
public abstract class Algorithm<T, U extends Data<T>, V, W> {

	protected U data;
	protected Results<T, V, W> results;

	public void setData(U data){
		this.data = data;
	}

	public void setResults(Results<T, V, W> results){
		this.results = results;
	}

	public abstract double estimate(double eps, int iterations);

	protected abstract double getLogLikelihood();
}
