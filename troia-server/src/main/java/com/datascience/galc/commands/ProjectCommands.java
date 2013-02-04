package com.datascience.galc.commands;

import com.datascience.galc.ContinuousProject;

/**
 *
 * @author artur
 */
public class ProjectCommands {
	
	static public class Compute extends GALCommandBase<Object> {

		int iterations;
		double epsilon;
		
		public Compute(ContinuousProject cp, int iterations, double epsilon){
			super(cp, true);
			this.iterations = iterations;
			this.epsilon = epsilon;
		}
		
		@Override
		protected void realExecute() {
			project.compute(iterations, epsilon);
			setResult("Computation done");
		}
	}
	
	static public class ObjectsPrediction extends GALCommandBase<Object> {

		public ObjectsPrediction(ContinuousProject cp){
			super(cp, true);
		}
		
		@Override
		protected void realExecute() {
			project.getDataPrediction();
			setResult("TODO");
		}
	}

	static public class WorkersPrediction extends GALCommandBase<Object> {

		public WorkersPrediction(ContinuousProject cp){
			super(cp, true);
		}
		
		@Override
		protected void realExecute() {
			project.getWorkerPrediction();
			setResult("TODO");
		}
	}
	
}
