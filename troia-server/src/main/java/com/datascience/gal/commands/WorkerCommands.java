package com.datascience.gal.commands;

import java.util.Collection;
import java.util.Map;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.Worker;

/**
 *
 * @author konrad
 */
public class WorkerCommands {
	
	static public class GetWorker extends ProjectCommand<Object> {

		private String workerId;
		
		public GetWorker(AbstractDawidSkene ads, String workerId){
			super(ads, false);
			this.workerId = workerId;
		}
		
		@Override
		void realExecute() {
			setResult(ads.getWorker(workerId));
		}
	}
	
	static public class GetWorkers extends ProjectCommand<Collection<Worker>> {
		
		public GetWorkers(AbstractDawidSkene ads){
			super(ads, false);
		}
		
		@Override
		void realExecute() {
			setResult(ads.getWorkers());
		}
	}
	
	static public class GetWorkersScores extends ProjectCommand<Collection<Map<String, Object>>> {
		public GetWorkersScores(AbstractDawidSkene ads){
			super(ads, false);
		}
		
		@Override
		void realExecute() {
			setResult(ads.getAllWorkerScores(true));
		}
	}
}
