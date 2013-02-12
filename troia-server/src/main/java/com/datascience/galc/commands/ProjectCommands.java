package com.datascience.galc.commands;

import java.util.Collection;

import com.datascience.galc.ContinuousProject;
import com.datascience.galc.DatumContResults;
import com.datascience.galc.WorkerContResults;

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
	
	static public class ObjectsPrediction extends GALCommandBase<Collection<DatumContResults>> {

		public ObjectsPrediction(ContinuousProject cp){
			super(cp, true);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getDataPrediction().values());
		}
	}

	static public class WorkersPrediction extends GALCommandBase<Collection<WorkerContResults>> {

		public WorkersPrediction(ContinuousProject cp){
			super(cp, true);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getWorkerPrediction().values());
		}
	}
	
}
