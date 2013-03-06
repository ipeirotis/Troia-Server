package com.datascience.core.base;

import com.datascience.gal.Category;

import java.util.Set;

/**
 * User: artur
 */
public class NominalData extends Data<String> {

	protected Set<Category> categories;

	public Set<Category> getCategories(){
		return categories;
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
