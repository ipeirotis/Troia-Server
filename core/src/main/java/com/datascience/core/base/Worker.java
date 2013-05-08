package com.datascience.core.base;

import com.google.common.base.Objects;

/**
 * @Author: konrad
 */
public class Worker<T> {

	protected String name;

	public Worker(String name){
		this();
		this.name = name;
	}

	public Worker(){
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(name);
	}

	@Override
	public boolean equals(Object other){
		if (other instanceof Worker) {
			return Objects.equal(name, ((Worker) other).name);
		}
		return false;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("name", name)
				// TODO: FIX -- tt causes an infinite loop.
				//.add("assigns", assigns)
				.toString();
	}
}
