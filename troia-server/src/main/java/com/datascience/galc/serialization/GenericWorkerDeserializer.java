
package com.datascience.galc.serialization;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.Worker;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 *
 * @author Michał Borysiak
 */
public class GenericWorkerDeserializer<T> implements JsonDeserializer<Worker<T>> {

	@Override
	public Worker<T> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jdc) throws JsonParseException {
		JsonObject jsonObject = (JsonObject) jsonElement;
		String name = jsonObject.get("name").getAsString();
		Collection<AssignedLabel<T>> assignedLabels = new Gson().fromJson(jsonObject.get("assigns"), new TypeToken<Collection<AssignedLabel<T>>>(){}.getType());
		Worker<T> worker = new Worker<T>(name);
		for (AssignedLabel<T> assignedLabel : assignedLabels) {
			worker.addAssign(assignedLabel);
		}
		return worker;
	}
	
}
