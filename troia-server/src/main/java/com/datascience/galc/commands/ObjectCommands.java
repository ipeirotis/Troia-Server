package com.datascience.galc.commands;

import java.util.Collection;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.storages.DataJSON.ShallowObjectCollection;
import com.datascience.galc.ContinuousProject;

/**
 *
 * @author artur
 */
public class ObjectCommands {
	
	static public class AddObjects extends GALCommandBase<Object> {

		ShallowObjectCollection objects;
		public AddObjects(ContinuousProject cp, ShallowObjectCollection objects){
			super(cp, true);
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
	
	static public class GetObjects extends GALCommandBase<Collection<LObject<ContValue>>> {
		
		public GetObjects(ContinuousProject cp){
			super(cp, false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getData().getObjects());
		}
	}
	
	static public class GetObject extends GALCommandBase<LObject<ContValue>> {
		
		String objectId;
		public GetObject(ContinuousProject cp, String objectId){
			super(cp, false);
			this.objectId = objectId;
		}
		
		@Override
		protected void realExecute(){
			setResult(ParamChecking.object(project.getData(), objectId));
		}
	}
	
	static public class GetObjectAssigns extends GALCommandBase<Collection<AssignedLabel<ContValue>>> {
		
		String objectId;
		public GetObjectAssigns(ContinuousProject cp, String objectId){
			super(cp, false);
			this.objectId = objectId;
		}
		
		@Override
		protected void realExecute(){
			LObject<ContValue> obj = ParamChecking.object(project.getData(), objectId);
			setResult(project.getData().getAssignsForObject(obj));
		}
	}
}
