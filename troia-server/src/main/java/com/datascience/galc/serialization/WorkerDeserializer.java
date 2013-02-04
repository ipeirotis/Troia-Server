package com.datascience.galc.serialization;

import com.datascience.core.base.Worker;
import com.datascience.core.storages.JSONUtils;
import com.datascience.galc.AssignedLabel;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Collection;

public class WorkerDeserializer implements JsonDeserializer<Worker> {

	@Override
	public Worker deserialize(JsonElement json, Type type,
							  JsonDeserializationContext context) throws JsonParseException {
		JsonObject jobject = (JsonObject) json;
		String name = jobject.get("name").getAsString();
		Double est_rho = jobject.get("est_rho").getAsDouble();
		Double est_mu = jobject.get("est_mu").getAsDouble();
		Double est_sigma = jobject.get("est_sigma").getAsDouble();
		Collection<AssignedLabel> labels = JSONUtils.gson.fromJson(
											   jobject.get("labels"), JSONUtils.assignedLabelSetType);
		return new Worker(name);
	}
}