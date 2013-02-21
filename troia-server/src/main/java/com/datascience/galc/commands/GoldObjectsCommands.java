package com.datascience.galc.commands;

import java.util.Collection;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.storages.DataJSON.ShallowGoldObject;
import com.datascience.core.storages.DataJSON.ShallowGoldObjectCollection;
import com.datascience.galc.ContinuousProject;

/**
 *
 * @author artur
 */
public class GoldObjectsCommands {
	
	static public class AddGoldObjects extends GALCommandBase<Object> {

		ShallowGoldObjectCollection<ContValue> goldObjects;
		public AddGoldObjects(ContinuousProject cp, ShallowGoldObjectCollection<ContValue> goldObjects){
			super(cp, true);
			this.goldObjects = goldObjects;
		}
		
		@Override
		protected void realExecute() {
			Data<ContValue> data = project.getData();
			for (ShallowGoldObject<ContValue> obj : goldObjects.objects){
				LObject<ContValue> object = data.getOrCreateObject(obj.object);
				object.setGoldLabel(obj.label);
				data.addGoldObject(object);
			}
			setResult("Gold objects added");			
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
