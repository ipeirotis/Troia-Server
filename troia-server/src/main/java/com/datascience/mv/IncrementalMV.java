package com.datascience.mv;

import com.datascience.core.algorithms.INewDataObserver;
import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.IncrementalNominalModel;
import com.datascience.core.stats.CategoryPriorCalculators;
import com.datascience.core.stats.ErrorRateCalculators;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @Author: konrad
 */
public class IncrementalMV extends MajorityVote implements INewDataObserver {

	private IncrementalNominalModel model;

	public IncrementalMV(){
		super(
			new ErrorRateCalculators.BatchErrorRateCalculator(),
			new CategoryPriorCalculators.IncrementalCategoryPriorCalculator());
		model = new IncrementalNominalModel();
	}

	@Override
	public IncrementalNominalModel getModel() {
		return model;
	}

	@Override
	public Type getModelType() {
		return new TypeToken<IncrementalNominalModel>() {} .getType();
	}

	@Override
	public void setModel(Object o){
		model = (IncrementalNominalModel) o;
	}

	@Override
	public void compute() {
		//There is nothing that we would make sense to do here
	}

	public void computeForNewAssign(AssignedLabel<String> assign){
		computeResultsForObject(assign.getLobject());
		for (AssignedLabel<String> al: getData().getAssignsForObject(assign.getLobject())){
			computeWorkersConfusionMatrix(al.getWorker());
		}
		model.priorDenominator++;
		model.categoryPriors.put(assign.getLabel(), model.categoryPriors.get(assign.getLabel()) + 1);
	}

	@Override
	public void newAssign(AssignedLabel assign) {
		computeForNewAssign(assign);
	}

	@Override
	public void newGoldObject(LObject object) {
		computeResultsForObject(object);
	}

	@Override
	public void newObject(LObject object) {
		computeResultsForObject(object);
	}

	@Override
	public void newWorker(Worker worker) {
	}
}
