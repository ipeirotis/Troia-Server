package com.datascience.utils.transformations.simple;

import com.datascience.core.nominal.PureNominalData;
import com.datascience.serialization.json.JSONUtils;
import com.datascience.utils.ITransformation;
import com.google.gson.Gson;

/**
* User: artur
* Date: 5/7/13
*/
public class NominalDataTransform implements ITransformation<PureNominalData, String> {

	Gson gson;
	public NominalDataTransform(){
		gson = JSONUtils.getFilledDefaultGsonBuilder().create();
	}

	@Override
	public String transform(PureNominalData s) {
		return gson.toJson(s);
	}

	@Override
	public PureNominalData inverse(String object) {
		return gson.fromJson(object, PureNominalData.class);
	}
}
