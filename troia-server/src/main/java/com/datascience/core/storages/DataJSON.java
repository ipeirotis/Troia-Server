package com.datascience.core.storages;

import com.datascience.core.base.*;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: konrad
 */
public class DataJSON {

	public static class Deserializer<T> implements JsonDeserializer<Data<T>> {

		@Override
		public Data<T> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			Data<T> data = new Data<T>();
			JsonObject jo = jsonElement.getAsJsonObject();
			for (JsonElement je: jo.get("workers").getAsJsonArray()){
				Worker<T> worker = new Worker<T>(je.getAsString());
				data.addWorker(worker);
			}
			for (JsonElement je: jo.get("objects").getAsJsonArray()){
				LObject<T> object = jsonDeserializationContext.deserialize(je, LObject.class);
				data.addObject((object));
			}
			for (JsonElement je: jo.get("goldObjects").getAsJsonArray()){
				LObject<T> object = jsonDeserializationContext.deserialize(je, LObject.class);
				data.addGoldObject((object));
			}
			for (JsonElement je: jo.get("evaluationObjects").getAsJsonArray()){
				LObject<T> object = jsonDeserializationContext.deserialize(je, LObject.class);
				data.addEvaluationObject((object));
			}

			for (JsonElement je: jo.get("assigns").getAsJsonArray()){
				ShallowAssign<T> sassign = jsonDeserializationContext.deserialize(je, ShallowAssign.class);

				Worker<T> worker = data.getWorker(sassign.worker);
				LObject<T> object = data.getObject(sassign.object);
				data.addAssign(new AssignedLabel<T>(worker, object, new Label<T>(sassign.label)));
			}

			return data;
		}
	}

	public static class Serializer<T> implements JsonSerializer<Data<T>> {

		@Override
		public JsonElement serialize(Data<T> data, Type type, JsonSerializationContext jsonSerializationContext) {
			Set<String> workers = new HashSet<String>();
			for (Worker<T> worker: data.getWorkers()){
				workers.add(worker.getName());
			}
			JsonElement jworkers = jsonSerializationContext.serialize(workers);
			JsonElement objects = jsonSerializationContext.serialize(data.getObjects());
			JsonElement goldObjects = jsonSerializationContext.serialize(data.getGoldObjects());
			JsonElement evaluationObjects = jsonSerializationContext.serialize(data.getEvaluationObjects());
			JsonElement assigns = jsonSerializationContext.serialize(data.getAssigns());

			JsonObject je = new JsonObject();
			je.add("workers", jworkers);
			je.add("objects", objects);
			je.add("goldObjects", goldObjects);
			je.add("evaluationObjects", evaluationObjects);
			je.add("assigns", assigns);

			return je;
		}
	}

	public static class AssignSerializer<T> implements JsonSerializer<AssignedLabel<T>> {

		@Override
		public JsonElement serialize(AssignedLabel<T> tAssignedLabel, Type type, JsonSerializationContext jsonSerializationContext) {
//			JsonObject jo = new JsonObject();
//			jo.add("worker", new JsonPrimitive(tAssignedLabel.getWorker().getName()));
//			jo.add("object", new JsonPrimitive(tAssignedLabel.getLobject().getName()));
//			jo.add("label", jsonSerializationContext.serialize(tAssignedLabel.getLabel()));
//			return jo;
			return jsonSerializationContext.serialize(new ShallowAssign<T>(tAssignedLabel));
		}
	}

	public static class ShallowAssign<T>{
		protected String worker;
		protected String object;
		protected T label;

		public ShallowAssign(AssignedLabel<T> assign){
			worker = assign.getWorker().getName();
			object = assign.getLobject().getName();
			label = assign.getLabel().getValue();
		}
	}
}
