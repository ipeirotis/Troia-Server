package com.datascience.core.commands;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.executor.JobCommand;

import java.util.Collection;

/**
 *
 * @author artur
 */
public class EvaluationObjectsCommands {

	static public class GetEvaluationObjects<T> extends JobCommand<Collection<LObject<T>>, Project> {
		
		public GetEvaluationObjects(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getData().getEvaluationObjects());
		}
	}
	
	static public class GetEvaluationObject<T> extends JobCommand<LObject<T>, Project> {
		
		String objectId;
		public GetEvaluationObject(String name){
			super(false);
			this.objectId = name;
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getData().getEvaluationObject(objectId));
		}
	}
}
