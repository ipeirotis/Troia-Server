/*******************************************************************************
 * Copyright (c) 2012 Panagiotis G. Ipeirotis & Josh M. Attenberg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.datascience.gal;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class MisclassificationCost {
	public static final MisclassificationCostDeserializer deserializer = new MisclassificationCostDeserializer();

	private String categoryFrom;
	private String categoryTo;
	private double cost;

	public MisclassificationCost(String from, String to, double cost) {
		this.categoryFrom = from;
		this.categoryTo = to;
		this.cost = cost;
	}

	/**
	 * @return the categoryFrom
	 */
	public String getCategoryFrom() {
		return categoryFrom;
	}

	/**
	 * @return the categoryTo
	 */
	public String getCategoryTo() {
		return categoryTo;
	}

	/**
	 * @return the cost
	 */
	public double getCost() {
		return cost;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				 + ((categoryFrom == null) ? 0 : categoryFrom.hashCode());
		result = prime * result
				 + ((categoryTo == null) ? 0 : categoryTo.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MisclassificationCost))
			return false;
		MisclassificationCost other = (MisclassificationCost) obj;
		if (categoryFrom == null) {
			if (other.categoryFrom != null)
				return false;
		} else if (!categoryFrom.equals(other.categoryFrom))
			return false;
		if (categoryTo == null) {
			if (other.categoryTo != null)
				return false;
		} else if (!categoryTo.equals(other.categoryTo))
			return false;
		return true;
	}

	public static class MisclassificationCostDeserializer implements
		JsonDeserializer<MisclassificationCost> {

		@Override
		public MisclassificationCost deserialize(JsonElement json, Type type,
				JsonDeserializationContext context) throws JsonParseException {
			JsonObject jobject = (JsonObject) json;
			return new MisclassificationCost(jobject.get("categoryFrom")
											 .getAsString(), jobject.get("categoryTo").getAsString(),
											 jobject.get("cost").getAsDouble());
		}

	}
}
