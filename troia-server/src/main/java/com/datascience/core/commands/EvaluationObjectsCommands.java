package com.datascience.core.commands;

import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.core.storages.DataJSON.ShallowGoldObject;
import com.datascience.core.storages.DataJSON.ShallowGoldObjectCollection;
import com.datascience.executor.JobCommand;

import java.util.Collection;

/**
 *
 * @author artur
 */
public class EvaluationObjectsCommands {
	
	static public class AddEvaluationObjects<T> extends JobCommand<Object, Project> {

		ShallowGoldObjectCollection<T> evalObjects;
		public AddEvaluationObjects(ShallowGoldObjectCollection<T> evalObjects){
			super(true);
			this.evalObjects = evalObjects;
		}
		
		@Override
		protected void realExecute() {
			Data<T> data = project.getData();
			for (ShallowGoldObject<T> obj : evalObjects.objects){
				LObject<T> object = data.getOrCreateObject(obj.object);
				object.setEvaluationLabel(obj.label);
				data.addEvaluationObject(object);
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
