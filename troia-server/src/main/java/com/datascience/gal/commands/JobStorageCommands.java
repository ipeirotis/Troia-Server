package com.datascience.gal.commands;

import org.apache.log4j.Logger;

import com.datascience.core.Job;
import com.datascience.core.storages.IJobStorage;

/**
 *
 * @author konrad & artur
 */
public class JobStorageCommands{
	
	static Logger logger = Logger.getLogger(JobStorageCommands.class);
	
	public static class Adder extends JobStorageCommand {
		
		public Adder(IJobStorage jobStorage, Job job, 
				String commandId, CommandStatusesContainer statusContainer) {
			super(jobStorage, job, commandId, statusContainer, false);
		}

		@Override
		public void run() {
			CommandStatus status;
			try {
				jobStorage.add(job);
				status = CommandStatus.okCommandStatus("New job created with ID: " + job.getId());
			} catch (Exception e) {
				status = CommandStatus.errorCommandStatus(e);
				logger.fatal("When adding job to storage", e);
			}
			statusContainer.addCommandStatus(commandId, status);
		}
	}
	
	public static class Remover extends JobStorageCommand {

		public Remover(IJobStorage jobStorage, Job job, 
				String commandId, CommandStatusesContainer statusContainer) {
			super(jobStorage, job, commandId, statusContainer, true);
		}

		@Override
		public void run() {
			CommandStatus status;
			try {
				jobStorage.remove(job);
				status = CommandStatus.okCommandStatus("Removed job with ID: " + job.getId());
			} catch (Exception e) {
				status = CommandStatus.errorCommandStatus(e);
				logger.fatal("When removing job to storage", e);
			}
			statusContainer.addCommandStatus(commandId, status);
		}
	}
}
