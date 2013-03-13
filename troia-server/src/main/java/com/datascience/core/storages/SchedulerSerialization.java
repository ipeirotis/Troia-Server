package com.datascience.core.storages;

import com.datascience.scheduler.IScheduler;
import com.datascience.scheduler.CachedScheduler;
import com.datascience.scheduler.SchedulerFactory;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SchedulerSerialization {

	public static class Deserializer<T> implements JsonDeserializer<IScheduler<T>> {

		@Override
		public IScheduler<T> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			Map<String, String> params = new HashMap<String, String>();
			for (Map.Entry<String, JsonElement> e : jsonObject.entrySet()) {
				params.put(e.getKey(), e.getValue().getAsString());
			}
			return new SchedulerFactory<T>().create(params);
		}
	}

	public static class Serializer<T> implements JsonSerializer<IScheduler<T>> {

		@Override
		public JsonElement serialize(IScheduler<T> scheduler, Type type, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("scheduler", scheduler.getClass().getName());
			jsonObject.addProperty("calculator", scheduler.getCalculator().getClass().getName());
			if (scheduler instanceof CachedScheduler) {
				CachedScheduler<T> cachedScheduler = (CachedScheduler<T>) scheduler;
				jsonObject.addProperty("pauseDuration", cachedScheduler.getPauseDuration());
				jsonObject.addProperty("pauseUnit", cachedScheduler.getPauseUnit().toString());
			}
			return jsonObject;
		}
	}
}
