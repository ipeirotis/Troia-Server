package com.datascience.core.base;

/**
 * User: artur
 */
public abstract class Algorithm<T, U extends Data<T>, V, W> {

	protected U data;
	protected Results<T, V, W> results;

	public Algorithm(U data, Results<T, V, W> results){
		this.data = data;
		this.results = results;
	}

	public abstract double estimate(double eps, int iterations);

	protected abstract double getLogLikelihood();
}
