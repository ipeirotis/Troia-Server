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
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import com.datascience.core.base.*;
import com.datascience.core.datastoring.memory.InMemoryData;
import com.datascience.core.nominal.CategoryValue;
import com.datascience.core.datastoring.memory.InMemoryNominalData;
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

	public static final Type shallowAssignsStringType = new TypeToken<Collection<ShallowAssign<String>>>(){}.getType();
	public static final Type shallowAssignsContValueType = new TypeToken<Collection<ShallowAssign<ContValue>>>(){}.getType();
	public static final Type assignsStringType = new TypeToken<Collection<AssignedLabel<String>>>(){}.getType();
	public static final Type assignsContValueType = new TypeToken<Collection<AssignedLabel<ContValue>>>(){}.getType();

	public static final Type assignString = new TypeToken<AssignedLabel<String>>(){}.getType();
	public static final Type assignContValue = new TypeToken<AssignedLabel<ContValue>>(){}.getType();
	public static final Type shallowAssignString = new TypeToken<ShallowAssign<String>>(){}.getType();
	public static final Type shallowAssignContValue = new TypeToken<ShallowAssign<ContValue>>(){}.getType();


	public static final Type workersStringType = new TypeToken<Collection<Worker<String>>>() {}.getType();
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

		builder.registerTypeAdapter(objectsStringType,
				new GenericCollectionDeserializer<LObject<String>>(
						"objects",
						new TypeToken<LObject<String>>(){}.getType()));
		builder.registerTypeAdapter(objectsContValueType,
				new GenericCollectionDeserializer<LObject<ContValue>>(
						"objects",
						new TypeToken<LObject<ContValue>>(){}.getType()));
		builder.registerTypeAdapter(shallowAssignsStringType,
				new GenericCollectionDeserializer<ShallowAssign<String>>(
						"assigns", shallowAssignString));
		builder.registerTypeAdapter(shallowAssignsContValueType,
				new GenericCollectionDeserializer<ShallowAssign<ContValue>>(
						"assigns", shallowAssignContValue));

        builder.registerTypeAdapter(objectsContValueType, new GenericCollectionSerializer<LObject<ContValue>>("objects"));
        builder.registerTypeAdapter(objectsStringType, new GenericCollectionSerializer<LObject<String>>("objects"));

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
			Collection<T> ret = new ArrayList<T>();
			for (JsonElement je : jsonElement.getAsJsonObject().get(collectionName).getAsJsonArray()){
				ret.add((T)context.deserialize(je, this.type));
			}
			return ret;
		}
	}

    public static class GenericCollectionSerializer<T> implements JsonSerializer<Collection<T>> {

        private String collectionName;

        public GenericCollectionSerializer(String collectionName){
            this.collectionName = collectionName;
        }

        public JsonElement serialize(Collection<T> src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jo = new JsonObject();
            JsonArray ja = new JsonArray();
            for (T el : src){
                ja.add(context.serialize(el));
            }
            jo.add(collectionName, ja);
            Logger.getAnonymousLogger().warning(jo.toString());
            return jo;
        }
    }
}
