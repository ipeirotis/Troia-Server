
package com.datascience.galc.serialization;

import java.lang.reflect.Type;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

/**
 *
 * @author Michał Borysiak
 */
public class GenericWorkerDeserializer<T> implements JsonDeserializer<Worker<T>> {

	@Override
	public Worker<T> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonObject = (JsonObject) jsonElement;
		Worker<T> worker = new Worker<T>(jsonObject.get("name").getAsString());
		JsonArray jsonAssigns = jsonObject.get("assigns").getAsJsonArray();
		for (int i = 0; i < jsonAssigns.size(); i++) {
			JsonObject jsonAssign = jsonAssigns.get(i).getAsJsonObject();
			T label = context.deserialize(jsonAssign.get("label"), new TypeToken<T>(){}.getType());
			LObject<T> lObject = context.deserialize(jsonAssign.get("lObject"), new TypeToken<LObject<T>>(){}.getType());
			worker.addAssign(new AssignedLabel<T>(worker, lObject, label));
		}
		return worker;
	}

}
