package com.datascience.galc.commands;

import java.util.Collection;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.storages.DataJSON.ShallowObjectCollection;
import com.datascience.executor.JobCommand;
import com.datascience.galc.ContinuousProject;

/**
 *
 * @author artur
 */
public class ObjectCommands {
	
	static public class AddObjects extends JobCommand<Object, ContinuousProject> {

		ShallowObjectCollection objects;
		public AddObjects(ShallowObjectCollection objects){
			super(true);
			this.objects = objects;
		}
		
		@Override
		protected void realExecute() {
			Data<ContValue> data = project.getData();
			for (String objectId : objects.objects){
				if (null == data.getObject(objectId)){
					data.addObject(new LObject<ContValue>(objectId));
				}
			}
			setResult("Objects without labels added");
		}
	}
	
	static public class GetObjects extends JobCommand<Collection<LObject<ContValue>>, ContinuousProject> {
		
		public GetObjects(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getData().getObjects());
		}
	}
	
	static public class GetObject extends JobCommand<LObject<ContValue>, ContinuousProject> {
		
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
	
	static public class GetObjectAssigns extends JobCommand<Collection<AssignedLabel<ContValue>>, ContinuousProject> {
		
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
