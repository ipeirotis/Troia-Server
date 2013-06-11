package com.datascience.datastoring.datamodels.memory;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.nominal.PureNominalData;
import com.datascience.core.nominal.CategoryValue;
import com.datascience.core.nominal.INominalData;
import com.datascience.utils.CostMatrix;

import java.util.*;


/**
 * User: artur
 */
public class InMemoryNominalData extends InMemoryData<String> implements INominalData {

	protected PureNominalData jobData;

	public InMemoryNominalData(){
		jobData = new PureNominalData();
	}

	public InMemoryNominalData(InMemoryData<String> data){
		jobData = new PureNominalData();
		this.assigns = data.assigns;
		this.workers = data.workers;
		this.mapWorkers = data.mapWorkers;
		this.mapObjects = data.mapObjects;
		this.objects = data.objects;
		this.goldObjects = data.goldObjects;
		this.evaluationObjects = data.evaluationObjects;
		this.datums = data.datums;
		this.workersAssigns = data.workersAssigns;
	}

	@Override
	public Collection<String> getCategories() {
		return jobData.getCategories();
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
	public CostMatrix<String> getCostMatrix() {
		return jobData.getCostMatrix();
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
