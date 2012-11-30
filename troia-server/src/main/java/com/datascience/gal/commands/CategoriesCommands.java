/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.datascience.gal.commands;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.Category;
import java.util.Collection;

/**
 *
 * @author konrad
 */
public class CategoriesCommands {
	
	static public class SetCategories extends ProjectCommand<Object> {

		private Collection<Category> categories;
		
		public SetCategories(AbstractDawidSkene ads, Collection<Category> categories){
			super(ads, true);
			this.categories = categories;
		}
		
		@Override
		void realExecute() {
			ads.initializeOnCategories(categories);
			setResult("Categories added");
		}
	}
	
	static public class GetCategories extends ProjectCommand<Collection<String>> {
		
		public GetCategories(AbstractDawidSkene ads){
			super(ads, false);
		}
		
		@Override
		void realExecute() {
			setResult(ads.getCategories().keySet());
		}
	}
}
