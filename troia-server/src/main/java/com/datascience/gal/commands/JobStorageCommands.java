package com.datascience.gal.commands;

import com.datascience.core.Job;
import com.datascience.core.storages.IJobStorage;
import com.datascience.executor.SynchronizedCommand;
import org.apache.log4j.Logger;

/**
 *
 * @author konrad
 */
public class JobStorageCommands{
	
	static Logger logger = Logger.getLogger(JobStorageCommands.class);
	
	public static class Adder extends SynchronizedCommand {
		
		protected IJobStorage jobStorage;
		protected Job job;
		
		public Adder(IJobStorage jobStorage, Job job) {
			super(job.getRWLock(), false);
			this.jobStorage = jobStorage;
			this.job = job;
		}

		@Override
		public void run() {
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
		
		public Remover(IJobStorage jobStorage, Job job) {
			super(job.getRWLock(), true);
			this.jobStorage = jobStorage;
			this.job = job;
		}

		@Override
		public void run() {
			try {
				jobStorage.remove(job);
			} catch (Exception e) {
				logger.fatal("When removing job to storage", e);
			}
		}
	}
}
