package com.datascience.core.base;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

@XmlRootElement(name="label")
public class ContValue {

	@XmlElement
	protected Double value;
	
	@XmlElement
	protected Double zeta;

	public ContValue(){
		
	}
	
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
	
	@Override
	public String toString(){
		return String.format("value: %f, zeta: %f", value, zeta);
	}
}
