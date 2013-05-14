package com.datascience.gal.dataGenerator;

import java.util.ArrayList;
import java.util.Collection;
import com.datascience.core.nominal.CategoryValue;
import com.datascience.utils.CostMatrix;

public class CategoryFactory {

	public Collection<CategoryValue> createCategoryPriors(Collection<String> categories){
		Collection<CategoryValue> ret = new ArrayList<CategoryValue>();
		for (String s : categories){
			ret.add(new CategoryValue(s, 1. / categories.size()));
		}
		return ret;
	}

	public CostMatrix<String> createMatrix(Collection<String> categories){
		CostMatrix<String> cm = new CostMatrix<String>();
		for (String c1 : categories){
			for (String c2 : categories){
				cm.add(c1, c2, c1.equals(c2) ? 0. : 1.);
			}
		}
		return cm;
	}

	public static CategoryFactory getInstance() {
		return instance;
	}

	private static CategoryFactory instance = new CategoryFactory();

	private CategoryFactory() {
	}
}