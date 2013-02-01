package com.datascience.gal.commands;

import java.util.Collection;

import com.datascience.gal.AbstractDawidSkene;

/**
 *
 * @author konrad
 */
public class CategoriesCommands {
	
	static public class GetCategories extends DSCommandBase<Collection<String>> {
		
		public GetCategories(AbstractDawidSkene ads){
			super(ads, false);
		}
		
		@Override
		protected void realExecute() {
			setResult(ads.getCategories().keySet());
		}
	}
}
