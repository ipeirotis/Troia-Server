package com.datascience.core.base;

import com.google.common.base.Objects;

import java.util.HashSet;
import java.util.Set;

/**
 * @Author: konrad
 */
public class Worker<T> {

	protected Set<AssignedLabel<T>> assigns;
	protected String name;

	public Worker(String name){
		this();
		this.name = name;
	}

	public Worker(){
		assigns = new HashSet<AssignedLabel<T>>();
	}

	public void addAssign(AssignedLabel<T> assign){
		assigns.add(assign);
	}

	public Set<AssignedLabel<T>> getAssigns(){
		return assigns;
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
				//.add("assigns", assigns)
				.toString();
	}
}
