package com.datascience.core.commands;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.core.jobs.JobCommand;

import java.util.Collection;

/**
 *
 * @author artur
 */
public class GoldObjectsCommands {

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
