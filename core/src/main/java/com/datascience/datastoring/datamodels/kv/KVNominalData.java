package com.datascience.datastoring.datamodels.kv;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.PureNominalData;
import com.datascience.core.nominal.CategoryValue;
import com.datascience.core.nominal.INominalData;
import com.datascience.utils.CostMatrix;
import com.datascience.datastoring.adapters.kv.ISafeKVStorage;
import java.util.Collection;
import java.util.Map;

/**
 * @Author: konrad
 */
public class KVNominalData extends KVData<String> implements INominalData{

	protected PureNominalData jobData;
	protected ISafeKVStorage<PureNominalData> storage;

	public KVNominalData(ISafeKVStorage<Collection<AssignedLabel<String>>> workersAssigns,
				  ISafeKVStorage<Collection<AssignedLabel<String>>> objectsAssigns,
				  ISafeKVStorage<Collection<LObject<String>>> objects,
				  ISafeKVStorage<Collection<LObject<String>>> goldObjects,
				  ISafeKVStorage<Collection<LObject<String>>> evaluationObjects,
				  ISafeKVStorage<Collection<Worker<String>>> workers,
				  ISafeKVStorage<PureNominalData> storage){
		super(workersAssigns, objectsAssigns, objects, goldObjects, evaluationObjects, workers);
		jobData = new PureNominalData();
		this.storage = storage;
	}

	@Override
	public void setCategories(Collection<String> categories) {
		jobData.setCategories(categories);
		storage.put("", jobData);
	}

	@Override
	public Collection<String> getCategories() {
		return jobData.getCategories();
	}

	@Override
	public void setPriorFixed(boolean fixedPriors) {
		jobData.setPriorFixed(fixedPriors);
		storage.put("", jobData);
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
		storage.put("", jobData);
	}

	@Override
	public CostMatrix<String> getCostMatrix() {
		return jobData.getCostMatrix();
	}

	@Override
	public void setCostMatrix(CostMatrix<String> cm) {
		jobData.setCostMatrix(cm);
		storage.put("", jobData);
	}

	@Override
	public void initialize(Collection<String> categories, Collection<CategoryValue> priors, CostMatrix<String> costMatrix) {
		jobData.initialize(categories, priors, costMatrix);
		storage.put("", jobData);
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
