package com.datascience.gal.commands;

import java.util.Collection;

/**
 *
 * @author konrad
 */
public class CategoriesCommands {
	
	static public class GetCategories extends DSCommandBase<Collection<String>> {
		
		public GetCategories(){
			super(false);
		}
		
		@Override
		protected void realExecute() {
			setResult(ads.getCategories().keySet());
		}
	}
}
