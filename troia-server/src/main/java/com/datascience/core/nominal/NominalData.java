package com.datascience.core.nominal;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.Category;
import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.google.common.math.DoubleMath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User: artur
 */
public class NominalData extends Data<String> {

	protected Set<Category> categories;
	protected boolean fixedPriors;

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

	public boolean arePriorsFixed(){
		return fixedPriors;
	}

	public void setPriorFixed(boolean fixedPriors){
		this.fixedPriors = fixedPriors;
	}

	public void setCategories(Set<Category> categories){
		this.categories = categories;
	}

	/*
		@returns: fixedPriors
	 */
	public void addCategories(Collection<Category> categories){
		double priorSum = 0.;
		int priorCnt = 0;
		fixedPriors = false;

		this.categories = new HashSet<Category>();
		if (categories.size() < 2){
			throw new IllegalArgumentException("There should be at least two categories");
		}
		for (Category c : categories) {
			this.categories.add(c);
			if (c.hasPrior()) {
				priorCnt += 1;
				priorSum += c.getPrior();
			}
		}
		if (!(priorCnt == 0 || (priorCnt == categories.size() && DoubleMath.fuzzyEquals(1., priorSum, 1e-6)))){
			throw new IllegalArgumentException(
					"Priors should sum up to 1. or not to be given (therefore we initialize the priors to be uniform across classes)");
		}
		if (priorCnt == 0){
			for (Category c : this.categories)
				c.setPrior(1. / categories.size());
		}
		if (priorCnt == categories.size() && DoubleMath.fuzzyEquals(1., priorSum, 1e-6))
			fixedPriors = true;

		//set cost matrix values if not provided
		for (Category from : this.categories) {
			for (Category to : this.categories) {
				if (from.getCost(to.getName()) == null){
					from.setCost(to.getName(), from.getName().equals(to.getName()) ? 0. : 1.);
				}
			}
		}
	}

	@Override
	public void addAssign(AssignedLabel<String> assign){
		checkForCategoryExist(assign.getLabel());
		super.addAssign(assign);
	}

	@Override
	public void addObject(LObject<String> object){
		if (object.isGold()) checkForCategoryExist(object.getGoldLabel());
		if (object.isEvaluation()) checkForCategoryExist(object.getEvaluationLabel());
		super.addObject(object);
	}

	private void checkForCategoryExist(String name){
		if (!getCategoriesNames().contains(name))
			throw new IllegalArgumentException("There is no category named: " + name);
	}
}
