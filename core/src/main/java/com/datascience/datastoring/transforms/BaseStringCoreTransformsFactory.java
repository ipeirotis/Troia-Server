package com.datascience.datastoring.transforms;

import com.datascience.utils.ITransformation;
import com.datascience.utils.transformations.IdTransformation;
import com.datascience.utils.transformations.JSONStrTransformation;
import com.google.gson.JsonObject;

/**
 * @Author: konrad
 */
public abstract class BaseStringCoreTransformsFactory implements ICoreTransformsFactory<String> {

	protected ITransformation<JsonObject, String> jsonObjectStringITransformation = new JSONStrTransformation();
	protected ITransformation<String, String> identicalTransform = new IdTransformation<String>();

	@Override
	public ITransformation<JsonObject, String> createSettingsTransform() {
		return jsonObjectStringITransformation;
	}

	@Override
	public ITransformation<String, String> createKindTransform() {
		return identicalTransform;
	}
}
