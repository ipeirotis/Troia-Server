package com.datascience.galc.serialization;

import com.datascience.core.storages.JSONUtils;
import com.datascience.gal.AssignedLabel;
import com.datascience.galc.Datum;
import com.datascience.galc.DatumCont;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Set;


public class DatumDeserializer implements JsonDeserializer<Datum> {

	@Override
	public Datum deserialize(JsonElement json, Type type,
							 JsonDeserializationContext context) throws JsonParseException {
		JsonObject jobject = (JsonObject) json;
		String name = jobject.get("name").getAsString();
		Double est_value = jobject.get("est_value").getAsDouble();
		Double est_zeta = jobject.get("est_zeta").getAsDouble();
		Set<AssignedLabel> labels = JSONUtils.gson.fromJson(
											   jobject.get("labels"), JSONUtils.assignedLabelSetType);

		if (jobject.has("correctCategory")) {
			return new DatumCont(name);
		} else {
			return new DatumCont(name);
		}
	}
	
}