package com.datascience.core.storages;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Label;
import com.datascience.core.base.Worker;
import com.datascience.galc.WorkerContResults;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

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

	public static class AssignSerializer<T> implements JsonSerializer<AssignedLabel<T>> {

		@Override
		public JsonElement serialize(AssignedLabel<T> tAssignedLabel, Type type, JsonSerializationContext jsonSerializationContext) {
			return jsonSerializationContext.serialize(new ShallowAssign<T>(tAssignedLabel));
		}
	}

	public static class WorkerContResultsSerializer implements JsonSerializer<WorkerContResults> {

		private static final Gson serializer;
		
		static {
			GsonBuilder builder = JSONUtils.getDefaultGsonBuilder();
			builder.registerTypeAdapter(Worker.class, new SimpleWorkerSerializer());
			builder.registerTypeAdapter(AssignedLabel.class, new AssignSerializer());
			serializer = builder.create();
		}
		
		@Override
		public JsonElement serialize(WorkerContResults wcr, Type type, JsonSerializationContext jsonSerializationContext) {
			return serializer.toJsonTree(wcr);
		}
	}
	
	private static class SimpleWorkerSerializer<T> implements JsonSerializer<Worker<T>> {
		@Override
		public JsonElement serialize(Worker<T> w, Type type, JsonSerializationContext ctx) {
			return new JsonPrimitive(w.getName());
		}
	}
	
	private static class SimpleObjectSerializer<T> implements JsonSerializer<LObject<T>> {
		@Override
		public JsonElement serialize(LObject<T> obj, Type type, JsonSerializationContext ctx) {
			return new JsonPrimitive(obj.getName());
		}
	}
	
	public static class SimpleLabelSerializer<T> implements JsonSerializer<Label<T>> {
		@Override
		public JsonElement serialize(Label<T> label, Type type, JsonSerializationContext ctx) {
			return ctx.serialize(label.getValue());
		}
	}
}
