package com.datascience.core;

import com.google.common.base.Objects;


/**
 * @author konrad
 */
public class Job<T> {
	
	T project;
	String id;
	
	public T getProject() {
		return project;
	}

	public String getId() {
		return id;
	}

	public Job(T project, String id){
		this.id = id;
		this.project = project;
	}

	@Override
	public boolean equals(Object other){
		if (other instanceof Job) {
			return Objects.equal(id, ((Job) other).id);
		}
		return false;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(id);
	}
	
	@Override
	public String toString(){
		return "Job_" + id;
	}
}
