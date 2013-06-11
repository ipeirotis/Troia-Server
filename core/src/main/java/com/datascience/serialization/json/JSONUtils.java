/*******************************************************************************
 * Copyright (c) 2012 Panagiotis G. Ipeirotis & Josh M. Attenberg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.datascience.serialization.json;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import com.datascience.core.base.*;
import com.datascience.datastoring.datamodels.memory.InMemoryData;
import com.datascience.core.nominal.CategoryValue;
import com.datascience.datastoring.datamodels.memory.InMemoryNominalData;
import com.datascience.core.results.ResultsFactory;
import com.datascience.core.stats.ConfusionMatrix;
import com.datascience.core.stats.MatrixValue;
import com.datascience.core.commands.Utils.ShallowAssign;
import com.datascience.serialization.Serialized;
import com.datascience.utils.CostMatrix;
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

	public static final Type categoryValuesCollectionType = new TypeToken<Collection<CategoryValue>>() {} .getType();
	public static final Type stringSetType = new TypeToken<Collection<String>>() {} .getType();
	public static final Type matrixValuesCollectionType = new TypeToken<Collection<MatrixValue>>() {} .getType();

	public static final Type objectsStringType = new TypeToken<Collection<LObject<String>>>() {}.getType();
	public static final Type objectsContValueType = new TypeToken<Collection<LObject<ContValue>>>() {}.getType();
	public static final Type objectsCollection = new TypeToken<Collection<LObject>>() {}.getType();

	public static final Type shallowAssignsStringType = new TypeToken<Collection<ShallowAssign<String>>>(){}.getType();
	public static final Type shallowAssignsContValueType = new TypeToken<Collection<ShallowAssign<ContValue>>>(){}.getType();
	public static final Type assignsStringType = new TypeToken<Collection<AssignedLabel<String>>>(){}.getType();
	public static final Type assignsContValueType = new TypeToken<Collection<AssignedLabel<ContValue>>>(){}.getType();

	public static final Type assignString = new TypeToken<AssignedLabel<String>>(){}.getType();
	public static final Type assignContValue = new TypeToken<AssignedLabel<ContValue>>(){}.getType();
	public static final Type shallowAssignString = new TypeToken<ShallowAssign<String>>(){}.getType();
	public static final Type shallowAssignContValue = new TypeToken<ShallowAssign<ContValue>>(){}.getType();


	public static final Type workersStringType = new TypeToken<Collection<Worker>>() {}.getType();
	public static final Type workersCollection = new TypeToken<Collection<Worker>>() {}.getType();

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
		return new GsonBuilder().serializeSpecialFloatingPointValues().enableComplexMapKeySerialization();
	}

	public static GsonBuilder getFilledDefaultGsonBuilder() {
		GsonBuilder builder = getDefaultGsonBuilder();
		builder.registerTypeAdapter(ConfusionMatrix.class, new MultinominalConfusionMatrixJSON.ConfusionMatrixDeserializer());
		builder.registerTypeAdapter(ConfusionMatrix.class, new MultinominalConfusionMatrixJSON.ConfusionMatrixSerializer());
		builder.registerTypeAdapter(InMemoryNominalData.class, new DataJSON.NominalDeserializer());
		builder.registerTypeAdapter(InMemoryNominalData.class, new DataJSON.NominalSerializer());
		builder.registerTypeAdapter(InMemoryData.class, new DataJSON.Deserializer());
		builder.registerTypeAdapter(InMemoryData.class, new DataJSON.Serializer());
		builder.registerTypeAdapter(AssignedLabel.class, new DataJSON.AssignSerializer());
		builder.registerTypeAdapter(assignString, new DataJSON.AssignDeserializer<AssignedLabel<String>>(shallowAssignString));
		builder.registerTypeAdapter(assignContValue, new DataJSON.AssignDeserializer<AssignedLabel<ContValue>>(shallowAssignContValue));
		builder.registerTypeAdapter(Worker.class, new DataJSON.WorkerSerializer());
		builder.registerTypeAdapter(Worker.class, new DataJSON.WorkerDeserializer());
		builder.registerTypeAdapter(Serialized.class, new SerializedSerializer());
		builder.registerTypeAdapter(CostMatrix.class, new DataJSON.CostMatrixDeserializer<String>());
		builder.registerTypeAdapter(CostMatrix.class, new DataJSON.CostMatrixSerializer<String>());

		builder.registerTypeAdapter(ResultsFactory.DatumResultCreator.class, new DataJSON.DatumCreatorDeserializer());
		builder.registerTypeAdapter(ResultsFactory.WorkerResultCreator.class, new DataJSON.WorkerCreatorDeserializer());

		builder.registerTypeAdapter(JsonObject.class, new DataJSON.JsonObjectSerializer());
		builder.registerTypeAdapter(JsonObject.class, new DataJSON.JsonObjectDeserializer());

		builder.registerTypeAdapter(objectsStringType,
				new DataJSON.GenericCollectionDeserializer<LObject<String>>(
						"objects",
						new TypeToken<LObject<String>>(){}.getType()));
		builder.registerTypeAdapter(objectsContValueType,
				new DataJSON.GenericCollectionDeserializer<LObject<ContValue>>(
						"objects",
						new TypeToken<LObject<ContValue>>(){}.getType()));
		builder.registerTypeAdapter(shallowAssignsStringType,
				new DataJSON.GenericCollectionDeserializer<ShallowAssign<String>>(
						"assigns", shallowAssignString));
		builder.registerTypeAdapter(shallowAssignsContValueType,
				new DataJSON.GenericCollectionDeserializer<ShallowAssign<ContValue>>(
						"assigns", shallowAssignContValue));

		return builder;
	}

	public static void ensureDefaultString(JsonObject params, String paramName, String paramValue){
		if (!params.has(paramName)){
			params.addProperty(paramName, paramValue);
		}
	}

	public static void ensureDefaultNumber(JsonObject params, String paramName, Number paramValue){
		if (!params.has(paramName)){
			params.addProperty(paramName, paramValue);
		}
	}

	public static String getDefaultString(JsonObject params, String paramName, String defaultVal){
		return params.has(paramName) ? params.get(paramName).getAsString() : defaultVal;
	}

	public static String t(String def){
		return def.toLowerCase();
	}

	public static JsonObject tKeys(JsonObject jo){
		JsonObject ret = new JsonObject();
		for (Map.Entry<String, JsonElement> e : jo.entrySet()){
			ret.add(t(e.getKey()), e.getValue());
		}
		return ret;
	}
}
