package com.datascience.serialization.json;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.datascience.core.base.*;
import com.datascience.core.nominal.CategoryValue;
import com.datascience.core.nominal.NominalData;
import com.datascience.core.results.ResultsFactory;
import com.datascience.core.stats.MatrixValue;
import com.datascience.utils.CostMatrix;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

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
			}

			for (JsonElement je: jo.get("assigns").getAsJsonArray()){
				ShallowAssign<T> sassign = jsonDeserializationContext.deserialize(je, ShallowAssign.class);

				Worker<T> worker = data.getOrCreateWorker(sassign.worker);
				LObject<T> object = data.getObject(sassign.object);
				data.addAssign(new AssignedLabel<T>(worker, object, sassign.label));
			}

			return data;
		}
	}

	public static class NominalDeserializer implements JsonDeserializer<NominalData> {

		@Override
		public NominalData deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			Deserializer<String> deserializer = new Deserializer<String>();
			NominalData ret = (NominalData) deserializer.deserialize(element, type, context);
			ret.setPriorFixed(element.getAsJsonObject().get("fixedPriors").getAsBoolean());
			ret.setCategories((Set<String>) context.deserialize(element.getAsJsonObject().get("categories"), JSONUtils.stringSetType));
			ret.setCategoryPriors((Collection<CategoryValue>)context.deserialize(element.getAsJsonObject().get("categoryPriors"), JSONUtils.categoryValuesCollectionType));
			ret.setCostMatrix((CostMatrix<String>)context.deserialize(element.getAsJsonObject().get("costMatrix"), CostMatrix.class));
			return ret;
		}
	}

	public static class Serializer<T> implements JsonSerializer<Data<T>> {

		@Override
		public JsonElement serialize(Data<T> data, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject je = new JsonObject();
			je.add("objects", jsonSerializationContext.serialize(data.getObjects()));
			je.add("assigns", jsonSerializationContext.serialize(data.getAssigns()));
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
			ret.add("categoryPriors", jsonSerializationContext.serialize(data.getCategoryPriors()));
			ret.add("costMatrix", jsonSerializationContext.serialize(data.getCostMatrix()));
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

	public static class CostMatrixSerializer<T> implements JsonSerializer<CostMatrix<T>> {
		@Override
		public JsonElement serialize(CostMatrix<T> w, Type type, JsonSerializationContext ctx) {
			JsonArray ja = new JsonArray();
			for (T from : w.getKnownValues()){
				for(T to: w.getKnownValues())
					ja.add(ctx.serialize(new MatrixValue(from, to, w.getCost(from, to))));
			}
			return ja;
		}
	}

	public static class CostMatrixDeserializer<T> implements JsonDeserializer<CostMatrix<T>> {
		@Override
		public CostMatrix<T> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			CostMatrix<T> cm = new CostMatrix<T>();
			for (JsonElement je : element.getAsJsonArray()){
				MatrixValue<T> mv = context.deserialize(je, MatrixValue.class);
				cm.add(mv.from, mv.to, mv.value);
			}
			return cm;
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

	public static class JsonObjectSerializer implements JsonSerializer<JsonObject> {
		@Override
		public JsonElement serialize(JsonObject jo, Type type, JsonSerializationContext ctx) {
			return jo;
		}
	}
}
