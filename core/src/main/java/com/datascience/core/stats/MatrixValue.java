package com.datascience.core.stats;

public class MatrixValue<T> {

	public T from;
	public T to;
	public Double value;
	
	public MatrixValue(T from, T to, Double v){
		this.from = from;
		this.to = to;
		value = v;
	}
}
