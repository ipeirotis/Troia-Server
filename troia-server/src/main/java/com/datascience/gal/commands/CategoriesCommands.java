package com.datascience.gal.commands;

import com.datascience.executor.JobCommand;
import com.datascience.gal.AbstractDawidSkene;

import java.util.Collection;

/**
 *
 * @author konrad
 */
public class CategoriesCommands {
	
	static public class GetCategories extends JobCommand<Collection<String>, AbstractDawidSkene> {
		
		public GetCategories(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(project.getCategories().keySet());
		}
	}
}
