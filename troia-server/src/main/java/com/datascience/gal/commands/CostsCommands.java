package com.datascience.gal.commands;

import java.util.Collection;

import com.datascience.executor.JobCommand;
import com.datascience.core.nominal.NominalProject;

/**
 *
 * @author konrad
 */
public class CostsCommands {
	
	/** This is not a bug - we are using categories here as they gsoned gives us costs
	 * */
	static public class GetCosts extends JobCommand<Collection<String>, NominalProject> {
		
		public GetCosts(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getData().getCategories());
		}
	}
}
