package com.datascience.core;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.common.base.Objects;


/**
 * @author konrad
 */
public class Job<T> {
	
	T ds;
	String id;
	ReadWriteLock rwLock;
	
	public T getDs() {
		return ds;
	}

	public String getId() {
		return id;
	}

	public ReadWriteLock getRWLock() {
		return rwLock;
	}
	
	public Job(T ads, String id){
		this.id = id;
		this.ds = ads;
		rwLock = new ReentrantReadWriteLock();
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
