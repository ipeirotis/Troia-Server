package com.datascience.core.commands;

import org.apache.log4j.Logger;

import com.datascience.core.Job;
import com.datascience.core.storages.IJobStorage;
import com.datascience.executor.SynchronizedCommand;
import com.datascience.service.JobsManager;

/**
 *
 * @author konrad
 */
public class JobStorageCommands{
	
	static Logger logger = Logger.getLogger(JobStorageCommands.class);
	
	public static class Adder extends SynchronizedCommand {
		
		protected IJobStorage jobStorage;
		protected Job job;
		
		public Adder(IJobStorage jobStorage, JobsManager jm, Job job) {
			super(jm.getLock(job.getId()), false);
			this.jobStorage = jobStorage;
			this.job = job;
		}

		@Override
		public void run() {
			logger.info("EX_JS: adder " + job.getId());
			try {
				jobStorage.add(job);
			} catch (Exception e) {
				logger.fatal("When adding job to storage", e);
			}
		}
	}
	
	public static class Remover extends SynchronizedCommand {

		protected IJobStorage jobStorage;
		protected Job job;
		
		public Remover(IJobStorage jobStorage, JobsManager jm, Job job) {
			super(jm.getLock(job.getId()), true);
			this.jobStorage = jobStorage;
			this.job = job;
		}

		@Override
		public void run() {
			logger.info("EX_JS: remover " + job.getId());
			try {
				jobStorage.remove(job);
			} catch (Exception e) {
				logger.fatal("When removing job to storage", e);
			}
		}
	}
}
