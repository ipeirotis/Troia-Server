package com.datascience.gal.commands;

import java.util.Map;

import com.datascience.executor.JobCommand;
import com.datascience.gal.AbstractDawidSkene;

/**
 *
 * @author artur
 */
public class JobCommands {
	
	static public class GetJobInfo extends JobCommand<Map<String, String>, AbstractDawidSkene> {

		public GetJobInfo(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getInfo());
		}
	}
}
