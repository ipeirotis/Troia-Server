package com.datascience.core.storages.serialization.json;

import com.datascience.core.base.Category;
import com.datascience.core.nominal.CategoryValue;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: konrad
 */
public class CategoryJSON {

	/**
	 * TODO: also allow to accept misclassification costs - at the moment this
	 * seems to come from the DS class
	 *
	 * @author josh
	 *
	 */
	public static class CategoryDeserializer implements
			JsonDeserializer<Category> {

		@Override
		public Category deserialize(JsonElement json, Type type,
									JsonDeserializationContext context) throws JsonParseException {
			JsonObject jobject = (JsonObject) json;
			String name = jobject.get("name").getAsString();
			double prior = -1.;
			if (jobject.has("prior"))
				prior = jobject.get("prior").getAsDouble();
			Collection<CategoryValue> misclassificationValues = new ArrayList<CategoryValue>();
			if (jobject.has("misclassificationCost"))
				misclassificationValues = context.deserialize(
						jobject.get("misclassificationCost"),
						JSONUtils.categoryValuesCollectionType);
			Map<String, Double> misclassification_cost = new HashMap<String, Double>();
			for (CategoryValue cv : misclassificationValues){
				misclassification_cost.put(cv.categoryName, cv.value);
			}
			return new Category(name, prior, misclassification_cost);
		}
	}

	public static class CategorySerializer implements JsonSerializer<Category> {

		@Override
		public JsonElement serialize(Category arg0, Type arg1,
									 JsonSerializationContext arg2) {
			JsonObject ret = new JsonObject();
			if (arg0.hasPrior())
				ret.addProperty("prior", arg0.getPrior());
			ret.addProperty("name", arg0.getName());
			Collection<CategoryValue> cp = new ArrayList<CategoryValue>(arg0.getMisclassificationCosts().size());
			for (Map.Entry<String, Double> e : arg0.getMisclassificationCosts().entrySet()){
				cp.add(new CategoryValue(e.getKey(), e.getValue()));
			}
			ret.add("misclassificationCost", arg2.serialize(cp));
			return ret;
		}
	}
}
