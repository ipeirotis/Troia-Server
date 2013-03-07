package com.datascience.core.base;

import com.datascience.gal.Category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * User: artur
 */
public class NominalData extends Data<String> {

	protected Set<Category> categories;

	public Set<Category> getCategories(){
		return categories;
	}

	public Collection<String> getCategoriesNames(){
		Collection<String> ret = new ArrayList<String>();
		for (Category c : categories){
			ret.add(c.getName());
		}
		return ret;
	}

	public Category getCategory(String name){
		for (Category c : categories)
			if (c.getName().equals(name))
				return c;
		return null;
	}

	public void addCategory(Category c){
		categories.add(c);
	}
}
