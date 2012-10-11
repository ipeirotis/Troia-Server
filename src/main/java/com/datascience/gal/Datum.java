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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.datascience.gal.service.JSONUtils;
import com.datascience.utils.Utils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class Datum {
	public static final DatumDeserializer deserializer = new DatumDeserializer();
	String name;

	// Defines if we have the correct category for this object
	// and if it is gold, the correctCategory holds the name of the correct
	// category
	boolean isGold;
	String correctCategory;

	// The probability estimates for the object belonging to different
	// categories
	Map<String, Double> categoryProbability;

	// The labels that have been assigned to this object, together with the
	// workers who
	// assigned these labels. Serves mainly as a speedup, and intended to be
	// used in
	// environments with persistence and caching (especially memcache)
	Set<AssignedLabel> labels;

	/**
	 * @return the isGold
	 */
	public boolean isGold() {
		return isGold;
	}

	/**
	 * @param isGold
	 *            the isGold to set
	 */
	public void setGold(boolean isGold) {
		this.isGold = isGold;
	}
	
	/**
	 * @return the goldCategory
	 */
	public String getCorrectCategory() {
		return correctCategory;
	}

	/**
	 * @param goldCategory
	 *            the goldCategory to set
	 */
	public void setCorrectCategory(String correctCategory) {
		this.correctCategory = correctCategory;

	}

	public Double getCategoryProbability(String c) {
		if (this.isGold) {
			if (c.equals(this.correctCategory)) {
				return 1.0;
			} else {
				return 0.0;
			}
		}
		return categoryProbability.get(c);
	}

	public void setCategoryProbability(String c, double prob) {
		categoryProbability.put(c, prob);
	}

	public double getEntropy() {
		double[] p = new double[this.categoryProbability.size()];

		int i = 0;
		for (String c : this.categoryProbability.keySet()) {
			p[i] = getCategoryProbability(c);
			i++;
		}

		return Utils.entropy(p);
	}

	private Datum(String name, Map<String, Double> categoryProbabilities,
				  boolean isGold, Collection<AssignedLabel> labels,
				  String correctCategory) {
		this(name, categoryProbabilities, isGold, labels);
		this.correctCategory = correctCategory;
	}

	private Datum(String name, Map<String, Double> categoryProbabilities,
				  boolean isGold, Collection<AssignedLabel> labels) {
		this.name = name;
		this.categoryProbability = categoryProbabilities;
		this.isGold = isGold;
		this.labels = new HashSet<AssignedLabel>(labels);
	}

	public Datum(String name, Set<Category> categories) {
		this.name = name;
		this.isGold = false;
		this.correctCategory = null;
		this.labels = new HashSet<AssignedLabel>();

		// We initialize the probabilities vector to be uniform across
		// categories
		this.categoryProbability = new HashMap<String, Double>();
		for (Category c : categories) {
			this.categoryProbability.put(c.getName(), 1.0 / categories.size());
		}
	}

	public void addAssignedLabel(AssignedLabel al) {
		if (al.getObjectName().equals(name)) {
			this.labels.add(al);
		}
	}

	public Collection<AssignedLabel> getAssignedLabels() {
		return this.labels;
	}

	public String getMajorityCategory() {
		double maxProbability = -1;
		String majorityCategory = null;

		for (String category : this.categoryProbability.keySet()) {
			Double probability = this.categoryProbability.get(category);
			if (probability > maxProbability) {
				maxProbability = probability;
				majorityCategory = category;
			} else if (probability == maxProbability) {
				// In case of a tie, break ties randomly
				// TODO: This is a corner case. We can also break ties
				// using the priors. But then we also need to group together
				// all the ties, and break ties probabilistically across the
				// group. Otherwise, we slightly favor the later comparisons.
				if (Math.random() > 0.5) {
					maxProbability = probability;
					majorityCategory = category;
				}
			}
		}

		return majorityCategory;
	}

	/**
	 * @return the categoryProbability
	 */
	public Map<String, Double> getCategoryProbability() {
		return categoryProbability;
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
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (!(obj instanceof Datum))
			return false;
		Datum other = (Datum) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return JSONUtils.gson.toJson(this);
	}

	public static class DatumDeserializer implements JsonDeserializer<Datum> {

		@Override
		public Datum deserialize(JsonElement json, Type type,
								 JsonDeserializationContext context) throws JsonParseException {
			JsonObject jobject = (JsonObject) json;
			Map<String, Double> catMap = JSONUtils.gson.fromJson(
											 jobject.get("categoryProbability"),
											 JSONUtils.stringDoubleMapType);
			boolean isGold = jobject.get("isGold").getAsBoolean();
			Collection<AssignedLabel> labels = JSONUtils.gson.fromJson(
												   jobject.get("labels"), JSONUtils.assignedLabelSetType);
			String name = jobject.get("name").getAsString();

			if (jobject.has("correctCategory")) {
				return new Datum(name, catMap, isGold, labels, jobject.get(
									 "correctCategory").getAsString());
			} else {
				return new Datum(name, catMap, isGold, labels);
			}
		}
	}

}
