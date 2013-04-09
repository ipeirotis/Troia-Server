package com.datascience.core.base;

import com.datascience.core.results.Results;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

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

	public Object getModel(){
		return null;
	}

	public Type getModelType(){
		return new TypeToken<Object>() {} .getType();
	}

	public void setModel(Object o){

	}

	public abstract void compute();
}
