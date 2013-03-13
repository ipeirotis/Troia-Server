package com.datascience.core.commands;

import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.executor.JobCommand;

import java.util.Collection;
import java.util.logging.Logger;

/**
 *
 * @author artur
 */
public class GoldObjectsCommands {
	
	static public class AddGoldObjects extends JobCommand<Object, Project> {

		Collection<LObject> goldObjects;
		public AddGoldObjects(Collection<LObject> goldObjects){
			super(true);
			this.goldObjects = goldObjects;
		}
		
		@Override
		protected void realExecute() {
			Data data = project.getData();
			for (LObject obj : goldObjects){
				Logger.getAnonymousLogger().warning(obj.toString());
				LObject object = data.getOrCreateObject(obj.getName());
				object.setGoldLabel(obj.getGoldLabel());
				data.addGoldObject(object);
			}
			setResult("Gold objects added");			
		}
	}

	static public class GetGoldObjects<T> extends JobCommand<Collection<LObject<T>>, Project> {
		
		public GetGoldObjects(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getData().getGoldObjects());
		}
	}
	
	static public class GetGoldObject<T> extends JobCommand<LObject<T>, Project> {
		
		String objectId;
		public GetGoldObject(String name){
			super(false);
			this.objectId = name;
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getData().getGoldObject(objectId));
		}
	}
}
