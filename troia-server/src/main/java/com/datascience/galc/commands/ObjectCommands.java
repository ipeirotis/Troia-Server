package com.datascience.galc.commands;

import java.util.Collection;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.galc.ContinuousProject;

/**
 *
 * @author artur
 */
public class ObjectCommands {
	
	static public class AddObject extends GALCommandBase<Object> {

		String objectId;
		public AddObject(ContinuousProject cp, String objectId){
			super(cp, true);
			this.objectId = objectId;
		}
		
		@Override
		protected void realExecute() {
			Data<ContValue> data = project.getData();
			if (null == data.getObject(objectId)){
				data.addObject(new LObject<ContValue>(objectId));
			}
			setResult("Assigns added");
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
}
