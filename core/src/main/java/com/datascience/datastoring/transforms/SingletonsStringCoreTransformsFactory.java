package com.datascience.datastoring.transforms;

import com.datascience.utils.ITransformation;
import com.datascience.utils.transformations.IdTransformation;
import com.datascience.utils.transformations.JSONStrTransformation;
import com.google.gson.JsonObject;

/**
 * @Author: konrad
 */
public abstract class SingletonsStringCoreTransformsFactory implements ICoreTransformsFactory<String> {

	@Override
	public ITransformation<JsonObject, String> createSettingsTransform() {
		return new JSONStrTransformation();
	}

	@Override
	public ITransformation<String, String> createKindTransform() {
		return new IdTransformation<String>();
	}
}
