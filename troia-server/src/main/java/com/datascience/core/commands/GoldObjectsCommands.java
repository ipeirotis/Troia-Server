package com.datascience.core.commands;

import com.datascience.core.base.ContValue;
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
public class GoldObjectsCommands {
	
	static public class AddGoldObjects<T> extends JobCommand<Object, Project> {

		ShallowGoldObjectCollection<T> goldObjects;
		public AddGoldObjects(ShallowGoldObjectCollection<T> goldObjects){
			super(true);
			this.goldObjects = goldObjects;
		}
		
		@Override
		protected void realExecute() {
			Data<T> data = project.getData();
			for (ShallowGoldObject<T> obj : goldObjects.objects){
				LObject<T> object = data.getOrCreateObject(obj.object);
				object.setGoldLabel(obj.label);
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
