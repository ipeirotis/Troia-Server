package com.datascience.utils.transformations;

import com.datascience.serialization.json.JSONUtils;
import com.datascience.utils.ITransformation;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class JSONStrTransformation implements ITransformation<JsonObject, String> {

	protected Gson gson = JSONUtils.getFilledDefaultGsonBuilder().create();

	@Override
	public String transform(JsonObject object) {
		return gson.toJson(object);
	}

	@Override
	public JsonObject inverse(String object) {
		return gson.fromJson(object, JsonObject.class);
	}
}
