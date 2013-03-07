package com.datascience.gal.commands;

import com.datascience.executor.JobCommand;
import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.NominalProject;

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
			setResult(project.getData().getCategoriesNames());
		}
	}
}
