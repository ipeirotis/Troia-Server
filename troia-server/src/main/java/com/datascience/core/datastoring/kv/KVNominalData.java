package com.datascience.core.datastoring.kv;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.CategoryValue;
import com.datascience.core.nominal.INominalData;
import com.datascience.utils.CostMatrix;
import com.datascience.utils.storage.ISafeKVStorage;

import java.util.Collection;
import java.util.Map;

/**
 * @Author: konrad
 */
public class KVNominalData extends KVData<String> implements INominalData{

	public KVNominalData(ISafeKVStorage<Collection<AssignedLabel<String>>> workersAssigns,
				  ISafeKVStorage<Collection<AssignedLabel<String>>> objectsAssigns,
				  ISafeKVStorage<Collection<LObject<String>>> objects,
				  ISafeKVStorage<Collection<LObject<String>>> goldObjects,
				  ISafeKVStorage<Collection<LObject<String>>> evaluationObjects,
				  ISafeKVStorage<Collection<Worker<String>>> workers){
		super(workersAssigns, objectsAssigns, objects, goldObjects, evaluationObjects, workers);
	}

	@Override
	public void setCategories(Collection<String> categories) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Collection<String> getCategories() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void setPriorFixed(boolean fixedPriors) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean arePriorsFixed() {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public double getCategoryPrior(String name) {
		return 0;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Map<String, Double> getCategoryPriors() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void setCategoryPriors(Collection<CategoryValue> priors) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public CostMatrix<String> getCostMatrix() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void setCostMatrix(CostMatrix<String> cm) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void initialize(Collection<String> categories, Collection<CategoryValue> priors, CostMatrix<String> costMatrix) {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}
