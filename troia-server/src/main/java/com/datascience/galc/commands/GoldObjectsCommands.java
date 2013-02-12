package com.datascience.galc.commands;

import java.util.Collection;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Label;
import com.datascience.galc.ContinuousProject;

/**
 *
 * @author artur
 */
public class GoldObjectsCommands {
	
	static public class AddGoldObjects extends GALCommandBase<Object> {

		String objectId;
		ContValue label;
		public AddGoldObjects(ContinuousProject cp, String objectId, ContValue label){
			super(cp, true);
			this.objectId = objectId;
			this.label = label;
		}
		
		@Override
		protected void realExecute() {
			LObject<ContValue> object = project.getData().getObject(objectId);
			if (object == null)
				object = new LObject<ContValue>(objectId);
			object.setGoldLabel(new Label<ContValue>(label));
			project.getData().addGoldObject(object);
			setResult("Gold object added");
		}
	}
	
	static public class GetGoldObjects extends GALCommandBase<Collection<LObject<ContValue>>> {
		
		public GetGoldObjects(ContinuousProject cp){
			super(cp, false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getData().getGoldObjects());
		}
	}
	
	static public class GetGoldObject extends GALCommandBase<LObject<ContValue>> {
		
		String objectId;
		public GetGoldObject(ContinuousProject cp, String name){
			super(cp, false);
			this.objectId = name;
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getData().getGoldObject(objectId));
		}
	}
}
