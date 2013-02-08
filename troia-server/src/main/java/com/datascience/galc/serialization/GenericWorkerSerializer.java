package com.datascience.galc.serialization;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.Worker;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Set;

/**
 *
 * @author Michal Borysiak
 */
public class GenericWorkerSerializer<T> implements JsonSerializer<Worker<T>> {

	@Override
	public JsonElement serialize(Worker<T> worker, Type type, JsonSerializationContext context) {
		Set<AssignedLabel<T>> assignedLabels = worker.getAssigns();
		JsonArray jsonAssigns = new JsonArray();
		for (AssignedLabel<T> assignedLabel : assignedLabels) {
			JsonObject jsonAssign = new JsonObject();
			jsonAssign.add("worker", new JsonPrimitive(worker.getName()));
			jsonAssign.add("lobject", context.serialize(assignedLabel.getLobject()));
			jsonAssign.add("label", context.serialize(assignedLabel.getLabel()));
			jsonAssigns.add(jsonAssign);
		}
		JsonObject jsonWorker = new JsonObject();
		jsonWorker.add("assigns", jsonAssigns);
		jsonWorker.addProperty("name", worker.getName());
		return jsonWorker;
	}

}
