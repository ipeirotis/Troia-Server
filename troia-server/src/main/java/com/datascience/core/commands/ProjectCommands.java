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
			project.getAlgorithm().compute();
			setResult("Computation done");
		}
	}
	
	static public class GetProjectInfo extends JobCommand<Map<String, String>, Project> {

		public GetProjectInfo(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getInfo());
		}
	}
}
