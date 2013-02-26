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

    static public class ObjectPrediction extends GALCommandBase<DatumContResults> {

        String objectId;
        public ObjectPrediction(String objectId){
            super(true);
            this.objectId = objectId;
        }

        @Override
        protected void realExecute() {
            setResult(project.getDataPrediction().get(project.getData().getObject(objectId)));
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

    static public class WorkerPrediction extends GALCommandBase<WorkerContResults> {

        String workerId;
        public WorkerPrediction(String wid){
            super(true);
            workerId = wid;
        }

        @Override
        protected void realExecute() {
            setResult(project.getWorkerPrediction().get(project.getData().getWorker(workerId)));
        }
    }
}
