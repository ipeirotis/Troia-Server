package com.datascience.gal.commands;

import com.datascience.core.Job;
import com.datascience.core.storages.IJobStorage;
import com.datascience.gal.AbstractDawidSkene;

/**
 *
 * @author konrad
 */
public class JobStorageCommands{

	
	public static class Adder extends ProjectCommand<Object> {
		
		protected IJobStorage jobStorage;
		protected Job job;
		
		public Adder(AbstractDawidSkene ads, IJobStorage jobStorage, Job job) {
			super(ads, false);
			this.jobStorage = jobStorage;
			this.job = job;
		}

		@Override
		void realExecute() throws Exception {
			jobStorage.add(job);
		}
	}
	
	public static class Remover extends ProjectCommand<Object> {

		protected IJobStorage jobStorage;
		protected Job job;
		
		public Remover(AbstractDawidSkene ads, IJobStorage jobStorage, Job job) {
			super(ads, true);
			this.jobStorage = jobStorage;
			this.job = job;
		}

		@Override
		void realExecute() throws Exception {
			jobStorage.remove(job.getId());
		}
		
	}
}
