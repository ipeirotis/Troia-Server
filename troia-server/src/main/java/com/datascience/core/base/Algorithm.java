package com.datascience.core.base;

/**
 * User: artur
 */
public abstract class Algorithm<T, U extends Data<T>, V, W> {

	protected U data;
	protected Results<T, V, W> results;

	public Algorithm(){
		results = new Results<T, V, W>();
	}

	public void setData(U data){
		this.data = data;
	}

	public abstract double estimate(double eps, int iterations);

	protected abstract double getLogLikelihood();
}
