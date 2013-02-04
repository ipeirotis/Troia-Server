package com.datascience.core.base;

public class ContValue {

	protected Double value;
	protected Double zeta;

	public ContValue(Double v, Double z){
		this.value = v;
		this.zeta = z;
	}

	public ContValue(Double v){
		this(v, null);
	}

	public Double getValue(){
		return value;
	}

	public Double getZeta(){
		return zeta;
	}
}
