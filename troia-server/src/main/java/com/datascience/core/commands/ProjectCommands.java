package com.datascience.core.commands;

import com.datascience.core.base.*;
import com.datascience.executor.JobCommand;

import java.util.*;

/**
 *
 * @author artur
 */
public class ProjectCommands {
	
	static public class Compute extends JobCommand<Object, Project> {

		public Compute(){
			super(true);
		}
		
		@Override
		protected void realExecute() {
			boolean notifyState = project.getResults().isNotifyEnabled();
			project.getResults().setNotifyEnabled(false);
			project.getAlgorithm().compute();
			project.getResults().setNotifyEnabled(notifyState);
			project.getResults().notifyAllNewResults();
			setResult("Computation done");
		}
	}
	
	static public class GetProjectInfo extends JobCommand<Map<String, Object>, Project> {

		public GetProjectInfo(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getInfo());
		}
	}
}
