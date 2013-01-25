/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.datascience.gal.commands;

import java.util.Collection;

import com.datascience.gal.AbstractDawidSkene;

/**
 *
 * @author konrad
 */
public class CategoriesCommands {
	
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
