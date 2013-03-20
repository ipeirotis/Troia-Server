package com.datascience.core.commands;

import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.executor.JobCommand;

import java.util.Collection;

/**
 *
 * @author artur
 */
public class EvaluationObjectsCommands {
	
	static public class AddEvaluationObjects extends JobCommand<Object, Project> {

		Collection<LObject> evalObjects;
		public AddEvaluationObjects(Collection<LObject> evalObjects){
			super(true);
			this.evalObjects = evalObjects;
		}
		
		@Override
		protected void realExecute() {
			Data data = project.getData();
			for (LObject obj : evalObjects){
				LObject object = data.getOrCreateObject(obj.getName());
				object.setEvaluationLabel(obj.getEvaluationLabel());
				data.addObject(object);
			}
			setResult("Evaluation objects added");
		}
	}

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
