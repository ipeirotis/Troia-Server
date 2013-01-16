package com.datascience.core;

import com.datascience.gal.commands.CommandStatusesContainer;
import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.service.RandomUniqIDGenerators;
import com.google.common.base.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * Make it generic on AbstractDawidSkene
 * @author konrad
 */
public class Job {
	
	AbstractDawidSkene ds;
	String id;
	ReadWriteLock rwLock;
	CommandStatusesContainer commandsResults;
	
	public AbstractDawidSkene getDs() {
		return ds;
	}

	public String getId() {
		return id;
	}

	public ReadWriteLock getRWLock() {
		return rwLock;
	}
	
	public CommandStatusesContainer getCommandStatusesContainer(){
		return commandsResults;
	}

	public Job(AbstractDawidSkene ads, String id){
		this.id = id;
		this.ds = ads;
		rwLock = new ReentrantReadWriteLock();
		commandsResults = new CommandStatusesContainer(
			new RandomUniqIDGenerators.Numbers());
			// TODO: this should be changed to contain date
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
