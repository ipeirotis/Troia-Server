package com.datascience.datastoring.datamodels.memory;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.nominal.PureNominalData;
import com.datascience.core.nominal.CategoryValue;
import com.datascience.core.nominal.INominalData;
import com.datascience.utils.CostMatrix;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * User: artur
 */
public class InMemoryNominalData extends InMemoryData<String> implements INominalData {

	protected PureNominalData jobData;

	public InMemoryNominalData(){
		jobData = new PureNominalData();
	}

	@Override
	public void setCategories(Collection<String> categories) {
		jobData.setCategories(categories);
	}

	@Override
	public Collection<String> getCategories() {
		return jobData.getCategories();
	}

	@Override
	public void setPriorFixed(boolean fixedPriors) {
		jobData.setPriorFixed(fixedPriors);
	}

	@Override
	public boolean arePriorsFixed() {
		return jobData.arePriorsFixed();
	}

	@Override
	public double getCategoryPrior(String name) {
		return jobData.getCategoryPrior(name);
	}

	@Override
	public Map<String, Double> getCategoryPriors() {
		return jobData.getCategoryPriors();
	}

	@Override
	public void setCategoryPriors(Collection<CategoryValue> priors) {
		jobData.setCategoryPriors(priors);
	}

	@Override
	public CostMatrix<String> getCostMatrix() {
		return jobData.getCostMatrix();
	}

	@Override
	public void setCostMatrix(CostMatrix<String> cm) {
		jobData.setCostMatrix(cm);
	}

	@Override
	public void initialize(Collection<String> categories, Collection<CategoryValue> priors, CostMatrix<String> costMatrix) {
		jobData.initialize(categories, priors, costMatrix);
	}

	@Override
	public void addAssign(AssignedLabel<String> assign){
		jobData.checkForCategoryExist(assign.getLabel());
		super.addAssign(assign);
	}

	@Override
	public void addObject(LObject<String> object){
		if (object.isGold()) jobData.checkForCategoryExist(object.getGoldLabel());
		if (object.isEvaluation()) jobData.checkForCategoryExist(object.getEvaluationLabel());
		super.addObject(object);
	}
}
