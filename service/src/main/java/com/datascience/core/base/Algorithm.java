package com.datascience.core.base;

import com.datascience.core.results.IResults;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * User: artur
 */
public abstract class Algorithm<T, U extends IData<T>, V, W> {

	protected U data;
	protected IResults<T, V, W> results;

	public void setData(U data){
		this.data = data;
	}

	public U getData(){
		return data;
	}

	public void setResults(IResults<T, V, W> results){
		this.results = results;
	}

	public IResults<T, V, W> getResults(){
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
