package com.datascience.core.base;

/**
 * User: artur
 */
public abstract class Algorithm<T, U extends Data<T>, V, W> {

	protected U data;
	protected Results<T, V, W> results;

	public void setData(U data){
		this.data = data;
	}

	public U getData(){
		return data;
	}

	public void setResults(Results<T, V, W> results){
		this.results = results;
	}

	public Results<T, V, W> getResults(){
		return  results;
	}
	public abstract void compute();

	public abstract ResultsFactory.IWorkerResultCreator getWorkerResultCreator();

	public abstract ResultsFactory.IDatumResultCreator getDatumResultCreator();
}
