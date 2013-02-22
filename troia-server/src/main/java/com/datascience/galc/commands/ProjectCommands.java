package com.datascience.galc.commands;

import java.util.Collection;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.Data;
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
		
		public Compute(int iterations, double epsilon){
			super(true);
			this.iterations = iterations;
			this.epsilon = epsilon;
		}
		
		@Override
		protected void realExecute() {
			project.compute(iterations, epsilon);
			setResult("Computation done");
		}
	}
	
	static public class GetProjectInfo extends GALCommandBase<Data<ContValue>> {

		public GetProjectInfo(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getData());
		}
	}
	
	static public class ObjectsPrediction extends GALCommandBase<Collection<DatumContResults>> {

		public ObjectsPrediction(){
			super(true);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getDataPrediction().values());
		}
	}

	static public class WorkersPrediction extends GALCommandBase<Collection<WorkerContResults>> {

		public WorkersPrediction(){
			super(true);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getWorkerPrediction().values());
		}
	}
	
}
