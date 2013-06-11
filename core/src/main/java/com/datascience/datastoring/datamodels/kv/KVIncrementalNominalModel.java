package com.datascience.datastoring.datamodels.kv;

import com.datascience.core.nominal.IIncrementalNominalModel;
import com.datascience.datastoring.adapters.kv.ISafeKVStorage;
import com.datascience.datastoring.datamodels.memory.IncrementalNominalModel;

import java.util.Map;

/**
 * User: artur
 * Date: 6/7/13
 */
public class KVIncrementalNominalModel implements IIncrementalNominalModel {

	protected ISafeKVStorage<IIncrementalNominalModel> storage;
	IIncrementalNominalModel model;

	public KVIncrementalNominalModel(ISafeKVStorage<IIncrementalNominalModel> storage){
		this.storage = storage;
		IIncrementalNominalModel m = storage.get("");
		if (m != null)
			model = m;
		else
			model = new IncrementalNominalModel();
	}

	@Override
	public void setCategoryPriors(Map<String, Double> map) {
		model.setCategoryPriors(map);
		storage.put("", model);
	}

	@Override
	public Map<String, Double> getCategoryPriors() {
		return model.getCategoryPriors();
	}

	@Override
	public int getPriorDenominator() {
		return model.getPriorDenominator();
	}

	@Override
	public void setPriorDenominator(int a) {
		model.setPriorDenominator(a);
		storage.put("", model);
	}
}
