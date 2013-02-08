package com.datascience.core.base;

import com.google.common.base.Objects;

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

	public boolean equals(Object other){
		if (other instanceof ContValue) {
			ContValue ot = (ContValue) other;
			return Objects.equal(value, ot.value) &&
					Objects.equal(zeta, ot.zeta);
		}
		return false;
	}
}
