package com.datascience.core.commands;

import com.datascience.core.base.*;
import com.datascience.executor.JobCommand;
import com.datascience.galc.commands.ParamChecking;

import java.util.Collection;

/**
 *
 * @author artur
 */
public class ObjectCommands {
	
	static public class AddObjects<T> extends JobCommand<Object, Project> {

		Collection<LObject> objects;

		public AddObjects(Collection<LObject> objects){
			super(true);
			this.objects = objects;
		}
		
		@Override
		protected void realExecute() {
			Data<T> data = project.getData();
			for (LObject objectId : objects){
				data.addObject(new LObject<T>(objectId.getName()));
			}
			setResult("Objects without labels added");
		}
	}
	
	static public class GetObjects<T> extends JobCommand<Collection<LObject<T>>, Project> {
		
		public GetObjects(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getData().getObjects());
		}
	}
	
	static public class GetObject<T> extends JobCommand<LObject<T>, Project> {
		
		String objectId;
		public GetObject(String objectId){
			super(false);
			this.objectId = objectId;
		}
		
		@Override
		protected void realExecute(){
			setResult(ParamChecking.object(project.getData(), objectId));
		}
	}
	
	static public class GetObjectAssigns<T> extends JobCommand<Collection<AssignedLabel<T>>, Project> {
		
		String objectId;
		public GetObjectAssigns(String objectId){
			super(false);
			this.objectId = objectId;
		}
		
		@Override
		protected void realExecute(){
			LObject<ContValue> obj = ParamChecking.object(project.getData(), objectId);
			setResult(project.getData().getAssignsForObject(obj));
		}
	}
}
