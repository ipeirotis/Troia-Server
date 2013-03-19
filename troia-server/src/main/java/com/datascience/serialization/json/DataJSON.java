package com.datascience.serialization.json;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import com.datascience.core.base.*;
import com.datascience.core.nominal.NominalData;
import com.datascience.core.results.ResultsFactory;
import com.datascience.core.results.WorkerContResults;
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
			for (JsonElement je: jo.get("objects").getAsJsonArray()){
				LObject<T> object = jsonDeserializationContext.deserialize(je, LObject.class);
				data.addObject((object));
				if (object.getEvaluationLabel() != null){
					data.addEvaluationObject(object);
				}
				if (object.getGoldLabel() != null){
					data.addGoldObject(object);
				}
			}

			for (JsonElement je: jo.get("assigns").getAsJsonArray()){
				ShallowAssign<T> sassign = jsonDeserializationContext.deserialize(je, ShallowAssign.class);

				Worker<T> worker = data.getWorker(sassign.worker);
				LObject<T> object = data.getObject(sassign.object);
				data.addAssign(new AssignedLabel<T>(worker, object, sassign.label));
			}

			return data;
		}
	}

	public static class NominalDeserializer implements JsonDeserializer<NominalData> {

		@Override
		public NominalData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			Deserializer<String> deserializer = new Deserializer<String>();
			NominalData ret = (NominalData) deserializer.deserialize(jsonElement, type, jsonDeserializationContext);
			ret.setPriorFixed(jsonElement.getAsJsonObject().get("fixedPriors").getAsBoolean());
			ret.setCategories((Set<Category>)jsonDeserializationContext.deserialize(
					jsonElement.getAsJsonObject().get("categories"), JSONUtils.categorySetType));
			return ret;
		}
	}

	public static class Serializer<T> implements JsonSerializer<Data<T>> {

		@Override
		public JsonElement serialize(Data<T> data, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonElement objects = jsonSerializationContext.serialize(data.getObjects());
			JsonElement assigns = jsonSerializationContext.serialize(data.getAssigns());

			JsonObject je = new JsonObject();
			je.add("objects", objects);
			je.add("assigns", assigns);

			return je;
		}
	}

	public static class NominalSerializer implements JsonSerializer<NominalData>{
		@Override
		public JsonElement serialize(NominalData data, Type type, JsonSerializationContext jsonSerializationContext) {
			Serializer<String> serializer = new Serializer<String>();
			JsonObject ret = serializer.serialize(data, type, jsonSerializationContext).getAsJsonObject();
			ret.addProperty("fixedPriors", data.arePriorsFixed());
			ret.add("categories", jsonSerializationContext.serialize(data.getCategories()));
			return ret;
		}
	}

	public static class ShallowAssign<T>{

		public String worker;
		public String object;
		public T label;

		public ShallowAssign(AssignedLabel<T> assign){
			worker = assign.getWorker().getName();
			object = assign.getLobject().getName();
			label = assign.getLabel();
		}
	}


	public static class AssignSerializer<T> implements JsonSerializer<AssignedLabel<T>> {

		@Override
		public JsonElement serialize(AssignedLabel<T> tAssignedLabel, Type type, JsonSerializationContext jsonSerializationContext) {
			return jsonSerializationContext.serialize(new ShallowAssign<T>(tAssignedLabel));
		}
	}
	
	public static class WorkerSerializer<T> implements JsonSerializer<Worker<T>> {
		@Override
		public JsonElement serialize(Worker<T> w, Type type, JsonSerializationContext ctx) {
			return new JsonPrimitive(w.getName());
		}
	}

	public static class DatumCreatorDeserializer<T, U> implements JsonDeserializer<ResultsFactory.DatumResultCreator<T, U>> {

		@Override
		public ResultsFactory.DatumResultCreator<T, U> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jo = jsonElement.getAsJsonObject();
			ResultsFactory.DatumResultFactoryCreator creator = new ResultsFactory.DatumResultFactoryCreator();
			return creator.create(jo.get("clazz").getAsString());
		}
	}

	public static class WorkerCreatorDeserializer<T, U> implements JsonDeserializer<ResultsFactory.WorkerResultCreator<T, U>> {

		@Override
		public ResultsFactory.WorkerResultCreator<T, U> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jo = jsonElement.getAsJsonObject();
			ResultsFactory.WorkerResultFactoryCreator creator = new ResultsFactory.WorkerResultFactoryCreator();
			return creator.create(jo.get("clazz").getAsString());
		}
	}
}
