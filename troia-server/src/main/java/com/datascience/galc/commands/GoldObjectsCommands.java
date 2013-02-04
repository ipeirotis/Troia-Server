package com.datascience.galc.commands;

import java.util.Collection;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.galc.ContinuousProject;

/**
 *
 * @author artur
 */
public class GoldObjectsCommands {
	
	static public class AddGoldObjects extends GALCommandBase<Object> {

		private LObject<ContValue> goldObject;
		public AddGoldObjects(ContinuousProject cp, LObject<ContValue> gold){
			super(cp, true);
			this.goldObject = gold;
		}
		
		@Override
		protected void realExecute() {
			project.getData().addGoldObject(goldObject);
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
