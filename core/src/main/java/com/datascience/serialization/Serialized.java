package com.datascience.serialization;

/**
 * @Author: konrad
 */
public class Serialized {

	protected Object object;

	public Serialized(Object object){
		this.object = object;
	}

	public Object getObject(){
		return object;
	}
}
