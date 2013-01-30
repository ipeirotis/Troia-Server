package com.datascience.core.base;

import com.google.common.base.Objects;

/**
  * @Author: konrad
 */
public class Label<T> {

	protected T value;

	public Label(){};

	public Label(T value){
		this.value = value;
	}

	public T getValue(){
		return value;
	}

	public void setValue(T value){
		this.value = value;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(value);
	}

	@Override
	public boolean equals(Object other){
		if (other instanceof Label){
			return Objects.equal(value, ((Label) other).value);
		}
		return false;
	}
}
