package com.datascience.core.nominal;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.InMemoryData;
import com.datascience.core.base.LObject;
import com.datascience.utils.CostMatrix;
import com.google.common.math.DoubleMath;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * User: artur
 */
public class InMemoryNominalData extends InMemoryData<String> implements INominalData{

	protected Collection<String> categories;
	protected boolean fixedPriors;
	protected Map<String, Double> categoryPriors;
	protected CostMatrix<String> costMatrix;

	@Override
	public Collection<String> getCategories(){
		return categories;
	}

	@Override
	public void setCategories(Collection<String> categories){
		this.categories = categories;
	}

	@Override
	public boolean arePriorsFixed(){
		return fixedPriors;
	}

	@Override
	public void setPriorFixed(boolean fixedPriors){
		this.fixedPriors = fixedPriors;
	}

	@Override
	public double getCategoryPrior(String name){
		return categoryPriors.get(name);
	}

	@Override
	public Map<String, Double> getCategoryPriors(){
		return categoryPriors;
	}

	@Override
	public void setCategoryPriors(Collection<CategoryValue> priors){
		double priorSum = 0.;
		Set<String> categoryNames = new HashSet<String>();
		for (CategoryValue cv : priors){
			priorSum += cv.value;
			checkArgument(categoryNames.add(cv.categoryName),
				"CategoryPriors contains two categories with the same name");
		}
		checkArgument(priors.size() == categories.size(),
				"Different number of categories in categoryPriors and categories parameters");
		checkArgument(DoubleMath.fuzzyEquals(1., priorSum, 1e-6),
				"Priors should sum up to 1. or not to be given (therefore we initialize the priors to be uniform across classes)");
		fixedPriors = true;
		categoryPriors = new HashMap<String, Double>();
		for (CategoryValue cv : priors){
			checkArgument(categories.contains(cv.categoryName),
					"Categories list does not contain category named %s", cv.categoryName);
			categoryPriors.put(cv.categoryName, cv.value);
		}
	}

	@Override
	public CostMatrix<String> getCostMatrix(){
		return costMatrix;
	}

	@Override
	public void setCostMatrix(CostMatrix<String> cm){
		this.costMatrix = cm;
	}

	@Override
	public void initialize(Collection<String> categories, Collection<CategoryValue> priors, CostMatrix<String> costMatrix){
		checkArgument(categories != null, "There is no categories collection");
		checkArgument(categories.size() >= 2, "There should be at least two categories");
		this.categories = new HashSet<String>();
		this.categories.addAll(categories);
		checkArgument(this.categories.size() == categories.size(), "Category names should be different");
		fixedPriors = false;

		if (priors != null){
			setCategoryPriors(priors);
		}

		if (costMatrix == null) {
			this.costMatrix = new CostMatrix<String>();
		}
		else {
			for (String s : costMatrix.getKnownValues()){
				checkArgument(this.categories.contains(s), "Categories list does not contain category named %s", s);
			}
			this.costMatrix = costMatrix;
		}
		for (String c1 : categories)
			for (String c2 : categories){
				if (!this.costMatrix.hasCost(c1, c2))
					this.costMatrix.add(c1, c2, c1.equals(c2) ? 0. : 1.);
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
		if (!categories.contains(name))
			throw new IllegalArgumentException("There is no category named: " + name);
	}
}
