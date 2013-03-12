/*******************************************************************************
 * Copyright (c) 2012 Panagiotis G. Ipeirotis & Josh M. Attenberg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.datascience.core.stats;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.datascience.core.storages.JSONUtils;
import com.datascience.gal.CategoryValue;
import com.google.common.base.Objects;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class Category {
	public static final CategoryDeserializer deserializer = new CategoryDeserializer();
	public static final CategorySerializer serializer = new CategorySerializer();
	
	private String name;

	// The prior probability for this category
	private double prior;

	// The misclassification cost when we classify an object of this category
	// into some other category. The HashMap key is the other category, and the
	// Double
	// is the cost.
	private Map<String, Double> misclassification_cost;

	private Category(String name, double prior,
					 Map<String, Double> misclassification_cost) {
		this.name = name;
		this.prior = prior;
		this.misclassification_cost = misclassification_cost;
	}

	public Category(String name) {
		this.name = name;
		this.prior = -1.0;
		this.misclassification_cost = new HashMap<String, Double>();
	}

	public void setCost(String to, double cost) {
		misclassification_cost.put(to, cost);
	}

	public Double getCost(String to) {
		return misclassification_cost.get(to);
	}

	public Map<String, Double> getMisclassificationCosts() {
		return misclassification_cost;
	}

	public double getPrior() {
		return prior;
	}

	public void setPrior(double prior) {
		this.prior = prior;
	}

	public boolean hasPrior() {
		return this.prior != -1;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(name, prior);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Category))
			return false;
		Category other = (Category) obj;
		return Objects.equal(name, other.name) &&
			Objects.equal(prior, other.prior);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
			Collection<CategoryValue> cp = new ArrayList<CategoryValue>(arg0.misclassification_cost.size());
			for (Entry<String, Double> e : arg0.misclassification_cost.entrySet()){
				cp.add(new CategoryValue(e.getKey(), e.getValue()));
			}
			ret.add("misclassificationCost", arg2.serialize(cp));
			return ret;
		}
	}

}
