package com.datascience.gal.commands;

import com.datascience.datastoring.jobs.JobCommand;
import com.datascience.core.nominal.NominalProject;

import java.util.Collection;

/**
 *
 * @author konrad
 */
public class CategoriesCommands {
	
	static public class GetCategories extends JobCommand<Collection<String>, NominalProject> {
		
		public GetCategories(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getData().getCategories());
		}
	}
}
