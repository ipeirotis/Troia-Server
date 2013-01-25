package com.datascience.gal.commands;

import org.apache.log4j.Logger;

import com.datascience.core.Job;
import com.datascience.core.storages.IJobStorage;
import com.datascience.gal.executor.SynchronizedJobCommand;

public abstract class JobStorageCommand extends SynchronizedJobCommand {
		
	static Logger logger = Logger.getLogger(JobStorageCommands.class);
	
	public String commandId;
	protected IJobStorage jobStorage;
	protected Job job;
	protected CommandStatusesContainer statusContainer;
	
	public JobStorageCommand(IJobStorage jobStorage, Job job, 
			String commandId, CommandStatusesContainer statusContainer,
			boolean modifies) {
		super(job.getRWLock(), modifies);
		this.jobStorage = jobStorage;
		this.job = job;
		this.commandId = commandId;
		this.statusContainer = statusContainer;
	}
	
}