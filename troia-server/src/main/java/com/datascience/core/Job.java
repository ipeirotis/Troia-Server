package com.datascience.core;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.datascience.gal.AbstractDawidSkene;
import com.google.common.base.Objects;


/**
 * Make it generic on AbstractDawidSkene
 * @author konrad
 */
public class Job {
	
	AbstractDawidSkene ds;
	String id;
	ReadWriteLock rwLock;
	
	public AbstractDawidSkene getDs() {
		return ds;
	}

	public String getId() {
		return id;
	}

	public ReadWriteLock getRWLock() {
		return rwLock;
	}
	
	public Job(AbstractDawidSkene ads, String id){
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
