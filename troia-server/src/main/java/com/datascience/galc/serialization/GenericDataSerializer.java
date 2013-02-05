package com.datascience.galc.serialization;

import com.datascience.core.base.Data;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/**
 *
 * @author Michal Borysiak
 */
public class GenericDataSerializer<T> implements JsonSerializer<Data<T>> {

	@Override
	public JsonElement serialize(Data<T> data, Type type, JsonSerializationContext context) {
		JsonObject jsonData = new JsonObject();
		jsonData.add("assigns", context.serialize(data.getAssigns()));
		jsonData.add("workers", context.serialize(data.getWorkers()));
		jsonData.add("objects", context.serialize(data.getObjects()));
		return jsonData;
	}

}