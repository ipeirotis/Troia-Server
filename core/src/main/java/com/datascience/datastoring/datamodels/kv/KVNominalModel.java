package com.datascience.datastoring.datamodels.kv;

import com.datascience.core.nominal.INominalModel;
import com.datascience.datastoring.adapters.kv.ISafeKVStorage;
import com.datascience.datastoring.datamodels.memory.NominalModel;

import java.util.Map;

/**
 * User: artur
 * Date: 6/7/13
 */
public class KVNominalModel implements INominalModel {

	protected ISafeKVStorage<INominalModel> storage;
	INominalModel model;

	public KVNominalModel(ISafeKVStorage<INominalModel> storage){
		this.storage = storage;
		INominalModel m = storage.get("");
		if (m != null)
			model = m;
		else
			model = new NominalModel();
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
}
