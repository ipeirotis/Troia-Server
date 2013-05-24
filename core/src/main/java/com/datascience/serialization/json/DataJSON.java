package com.datascience.serialization.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.datascience.core.base.*;
import com.datascience.core.commands.Utils.ShallowAssign;
import com.datascience.datastoring.datamodels.memory.InMemoryData;
import com.datascience.core.nominal.CategoryValue;
import com.datascience.datastoring.datamodels.memory.InMemoryNominalData;
import com.datascience.core.results.ResultsFactory;
import com.datascience.core.stats.MatrixValue;
import com.datascience.utils.CostMatrix;
import com.google.gson.*;

/**
 * @Author: konrad
 */
public class DataJSON {

	public static class Deserializer<T> implements JsonDeserializer<InMemoryData<T>> {

		@Override
		public InMemoryData<T> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			InMemoryData<T> data = new InMemoryData<T>();
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

	public static class NominalDeserializer implements JsonDeserializer<InMemoryNominalData> {

		@Override
		public InMemoryNominalData deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			Deserializer<String> deserializer = new Deserializer<String>();
			InMemoryNominalData ret = (InMemoryNominalData) deserializer.deserialize(element, type, context);
			ret.setPriorFixed(element.getAsJsonObject().get("fixedPriors").getAsBoolean());
			ret.setCategories((Set<String>) context.deserialize(element.getAsJsonObject().get("categories"), JSONUtils.stringSetType));
			ret.setCategoryPriors((Collection<CategoryValue>) context.deserialize(element.getAsJsonObject().get("categoryPriors"), JSONUtils.categoryValuesCollectionType));
			ret.setCostMatrix((CostMatrix<String>) context.deserialize(element.getAsJsonObject().get("costMatrix"), CostMatrix.class));
			return ret;
		}
	}

	public static class Serializer<T> implements JsonSerializer<InMemoryData<T>> {

		@Override
		public JsonElement serialize(InMemoryData<T> data, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject je = new JsonObject();
			je.add("objects", jsonSerializationContext.serialize(data.getObjects()));
			je.add("assigns", jsonSerializationContext.serialize(data.getAssigns()));
			return je;
		}
	}

	public static class NominalSerializer implements JsonSerializer<InMemoryNominalData>{
		@Override
		public JsonElement serialize(InMemoryNominalData data, Type type, JsonSerializationContext jsonSerializationContext) {
			Serializer<String> serializer = new Serializer<String>();
			JsonObject ret = serializer.serialize(data, type, jsonSerializationContext).getAsJsonObject();
			ret.addProperty("fixedPriors", data.arePriorsFixed());
			ret.add("categories", jsonSerializationContext.serialize(data.getCategories()));
			ret.add("categoryPriors", jsonSerializationContext.serialize(data.getCategoryPriors()));
			ret.add("costMatrix", jsonSerializationContext.serialize(data.getCostMatrix()));
			return ret;
		}
	}



	public static class AssignSerializer<T> implements JsonSerializer<AssignedLabel<T>> {
		@Override
		public JsonElement serialize(AssignedLabel<T> tAssignedLabel, Type type, JsonSerializationContext jsonSerializationContext) {
			return jsonSerializationContext.serialize(new ShallowAssign<T>(tAssignedLabel));
		}
	}

	public static class AssignDeserializer<T> implements JsonDeserializer<AssignedLabel<T>> {

		private Type type;

		public AssignDeserializer(Type type){
			this.type = type;
		}

		@Override
		public AssignedLabel<T> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			ShallowAssign<T> sassign = context.deserialize(element, this.type);
			return new AssignedLabel<T>(
					new Worker<T>(sassign.worker),
					new LObject<T>(sassign.object),
					sassign.label);
		}
	}
	
	public static class WorkerSerializer<T> implements JsonSerializer<Worker<T>> {
		@Override
		public JsonElement serialize(Worker<T> w, Type type, JsonSerializationContext ctx) {
			return new JsonPrimitive(w.getName());
		}
	}

	public static class WorkerDeserializer<T> implements JsonDeserializer<Worker<T>> {
		@Override
		public Worker<T> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			return new Worker<T>(element.getAsString());
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

	public static class DatumCreatorDeserializer<T> implements JsonDeserializer<ResultsFactory.DatumResultCreator<T>> {
		@Override
		public ResultsFactory.DatumResultCreator<T> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jo = jsonElement.getAsJsonObject();
			ResultsFactory.DatumResultFactoryCreator creator = new ResultsFactory.DatumResultFactoryCreator();
			return creator.create(jo.get("clazz").getAsString());
		}
	}

	public static class WorkerCreatorDeserializer<T> implements JsonDeserializer<ResultsFactory.WorkerResultCreator<T>> {
		@Override
		public ResultsFactory.WorkerResultCreator<T> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
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

	public static class GenericCollectionDeserializer<T> implements JsonDeserializer<Collection<T>> {

		private String collectionName;
		private Type type;

		public GenericCollectionDeserializer(String collectionName, Type type){
			this.collectionName = collectionName;
			this.type = type;
		}

		@Override
		public Collection<T> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
			Collection<T> ret = new ArrayList<T>();
			for (JsonElement je : jsonElement.getAsJsonObject().get(collectionName).getAsJsonArray()){
				ret.add((T)context.deserialize(je, this.type));
			}
			return ret;
		}
	}
}
