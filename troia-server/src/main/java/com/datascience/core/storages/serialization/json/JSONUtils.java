/*******************************************************************************
 * Copyright (c) 2012 Panagiotis G. Ipeirotis & Josh M. Attenberg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.datascience.core.storages.serialization.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import com.datascience.core.base.Category;
import com.datascience.core.nominal.CategoryValue;
import com.datascience.core.stats.MultinomialConfusionMatrix;
import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.gal.*;
import com.datascience.galc.ContinuousProject;
import com.datascience.core.base.Data;
import com.datascience.galc.WorkerContResults;
import com.datascience.galc.serialization.GenericWorkerDeserializer;
import com.datascience.galc.serialization.GenericWorkerSerializer;
import com.datascience.core.storages.serialization.Serialized;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

/**
 * utility class for json deserialization TODO: add direct methods rather than
 * method access through the gson member
 *
 * @author josh
 *
 */
public class JSONUtils {
	public final Gson gson;

	public static final Type categoryValuesCollectionType = new TypeToken<Collection<CategoryValue>>() {
	} .getType();
	public static final Type categorySetType = new TypeToken<Collection<Category>>() {
	} .getType();
	public static final Type stringSetType = new TypeToken<Collection<String>>() {
	} .getType();
	public static final Type categoryType = new TypeToken<Category>() {
	} .getType();
	public static final Type misclassificationCostType = new TypeToken<MisclassificationCost>() {
	} .getType();
	public static final Type workerGenericType = new TypeToken<com.datascience.core.base.Worker>() {
	} .getType();
	public static final Type workerContResultsType = new TypeToken<WorkerContResults>() {
	} .getType();
	public static final Type continuousProject = new TypeToken<ContinuousProject>() {
	} .getType();
	public static final Type matrixValuesCollectionType = new TypeToken<Collection<MatrixValue>>() {
	} .getType();
	public static final Type confusionMatrixType = new TypeToken<MultinomialConfusionMatrix>() {
	} .getType();
	public static final Type dawidSkeneType = new TypeToken<AbstractDawidSkene>() {
	} .getType();

	public static final Type objectsStringType = new TypeToken<Collection<LObject<String>>>() {}.getType();
	public static final Type objectsContValueType = new TypeToken<Collection<LObject<ContValue>>>() {}.getType();
	public static final Type assignsStringType = new TypeToken<Collection<DataJSON.ShallowAssign<String>>>(){}.getType();
	public static final Type assignsContValueType = new TypeToken<Collection<DataJSON.ShallowAssign<ContValue>>>(){}.getType();


	public JSONUtils() {
		GsonBuilder builder = getFilledDefaultGsonBuilder();
		gson = builder.create();
	}

	public static class SerializedSerializer  implements JsonSerializer<Serialized> {
		public JsonElement serialize(Serialized src, Type typeOfSrc, JsonSerializationContext context) {
			// If we use the same instance of serializer everywhere than this should be safe
			return (JsonElement) src.getObject();
		}
	}
	
	public static GsonBuilder getDefaultGsonBuilder() {
		return new GsonBuilder().serializeSpecialFloatingPointValues();
	}

	public static GsonBuilder getFilledDefaultGsonBuilder() {
		GsonBuilder builder = getDefaultGsonBuilder();
		builder.registerTypeAdapter(categoryType, new CategoryJSON.CategoryDeserializer());
		builder.registerTypeAdapter(categoryType, new CategoryJSON.CategorySerializer());
		builder.registerTypeAdapter(misclassificationCostType,MisclassificationCost.deserializer);
		builder.registerTypeAdapter(confusionMatrixType, MultinomialConfusionMatrix.deserializer);
		builder.registerTypeAdapter(confusionMatrixType, MultinomialConfusionMatrix.serializer);

		builder.registerTypeAdapter(workerGenericType, new GenericWorkerDeserializer());
		builder.registerTypeAdapter(workerGenericType, new GenericWorkerSerializer());

		builder.registerTypeAdapter(Data.class, new DataJSON.Deserializer());
		builder.registerTypeAdapter(Data.class, new DataJSON.Serializer());
		builder.registerTypeAdapter(AssignedLabel.class, new DataJSON.AssignSerializer());
		builder.registerTypeAdapter(Serialized.class, new SerializedSerializer());
		builder.registerTypeAdapter(workerContResultsType, new DataJSON.WorkerContResultsSerializer());

		builder.registerTypeAdapter(objectsStringType,
				new GenericCollectionDeserializer<LObject<String>>(
						"objects",
						new TypeToken<LObject<String>>(){}.getType()));
		builder.registerTypeAdapter(objectsContValueType,
				new GenericCollectionDeserializer<LObject<ContValue>>(
						"objects",
						new TypeToken<LObject<ContValue>>(){}.getType()));
		builder.registerTypeAdapter(assignsStringType,
				new GenericCollectionDeserializer<DataJSON.ShallowAssign<String>>(
						"assigns",
						new TypeToken<DataJSON.ShallowAssign<String>>(){}.getType()));
		builder.registerTypeAdapter(assignsContValueType,
				new GenericCollectionDeserializer<DataJSON.ShallowAssign<ContValue>>(
						"assigns",
						new TypeToken<DataJSON.ShallowAssign<ContValue>>(){}.getType()));

		return builder;
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
			Logger.getAnonymousLogger().warning(jsonElement.toString());
			Collection<T> ret = new ArrayList<T>();
			for (JsonElement je : jsonElement.getAsJsonObject().get(collectionName).getAsJsonArray()){
				ret.add((T)context.deserialize(je, this.type));
			}
			return ret;
		}

	}

//	public static Gson getOldGson(){
//		GsonBuilder builder = getFilledDefaultGsonBuilder();
//		builder.registerTypeAdapter(com.datascience.gal.Worker.class, Worker.deserializer);
//		return builder.create();
//	}
}
