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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.datascience.core.storages.JSONUtils;
import com.datascience.gal.CategoryValue;
import com.datascience.gal.MatrixValue;
import com.google.common.base.Objects;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * TODO: implement with logistic regression TODO: bayesian multinomial
 *
 * @author panos and josh
 *
 */
public class MultinomialConfusionMatrix implements ConfusionMatrix {

	public static final ConfusionMatrixDeserializer deserializer = new ConfusionMatrixDeserializer();
	public static final ConfusionMatrixSerializer serializer = new ConfusionMatrixSerializer();
	
	private Set<String> categories;
	private Map<CategoryPair, Double> matrix;
	public Map<String, Double> rowDenominator;

	private MultinomialConfusionMatrix(Collection<String> categories,
									   Map<CategoryPair, Double> matrix, Map<String, Double> rowDenominator) {
		this.categories = new HashSet<String>(categories);
		this.matrix = matrix;
		this.rowDenominator = rowDenominator;
	}
	
	public MultinomialConfusionMatrix(Collection<Category> categories, Map<CategoryPair, Double> matrix) {
		this.categories = new HashSet<String>();
		this.matrix = matrix;
		rowDenominator = new HashMap<String, Double>();
		for (Category c : categories) {
			this.categories.add(c.getName());
		}
	}

	public MultinomialConfusionMatrix(Collection<Category> categories) {
		this.categories = new HashSet<String>();
		for (Category c : categories) {
			this.categories.add(c.getName());
		}

		this.matrix = new HashMap<CategoryPair, Double>();
		rowDenominator = new HashMap<String, Double>();

		// We now initialize the confusion matrix
		// and we set it to 0.9 in the diagonal and 0.0 elsewhere
		for (String from : this.categories) {
			for (String to : this.categories) {
				double value = 0.1 / (double) (this.categories.size() - 1);
				if (from.equals(to)) {
					value = .9;
				}
				setErrorRate(from, to, value);
				incrementRowDenominator(from, value);
			}
		}
		normalize();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ipeirotis.gal.ConfusionMatrix#incrementRowDenominator(java.lang.String
	 * , double)
	 */
	@Override
	public void incrementRowDenominator(String from, double value) {
		double old = rowDenominator.containsKey(from) ? rowDenominator
					 .get(from) : 0;
		rowDenominator.put(from, old + value);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ipeirotis.gal.ConfusionMatrix#decrementRowDenominator(java.lang.String
	 * , double)
	 */
	@Override
	public void decrementRowDenominator(String from, double value) {
		double old = rowDenominator.containsKey(from) ? rowDenominator
					 .get(from) : 0;
		rowDenominator.put(from, Math.max(0., old - value));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ipeirotis.gal.ConfusionMatrix#empty()
	 */
	@Override
	public void empty() {
		// for (String from : this.categories) {
		// for (String to : this.categories) {
		// rowDenominator.put(from, 0.);
		// setErrorRate(from, to, 0.0);
		// }
		// }
		matrix = new HashMap<CategoryPair, Double>();
		rowDenominator = new HashMap<String, Double>();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ipeirotis.gal.ConfusionMatrix#normalize()
	 */
	@Override
	public void normalize() {

		for (String from : this.categories) {
			double from_marginal = rowDenominator.containsKey(from) ? rowDenominator
								   .get(from) : 0;

			for (String to : this.categories) {
				double error = getErrorRateBatch(from, to);
				double error_rate;

				// If the marginal across the "from" category is 0
				// this means that the worker has not even seen an object of the
				// "from"
				// category. In this case, we switch to Laplacean smoothing for
				// computing the matrix
				if (from_marginal == 0.0) {
					 error_rate = Double.NaN;
					// System.out.println(from_marginal);
//					error_rate = (error + 1)
//								 / (from_marginal + this.categories.size());
				} else {
					error_rate = error / from_marginal;
				}
				setErrorRate(from, to, error_rate);
			}
			rowDenominator.put(from, 1.);
//            System.out.println(from + " " + rowDenominator.get(from));
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ipeirotis.gal.ConfusionMatrix#normalizeLaplacean()
	 */
	@Override
	public void normalizeLaplacean() {
		for (String from : this.categories) {
			double from_marginal = rowDenominator.get(from);
			for (String to : this.categories) {
				double error = getErrorRateBatch(from, to);
				setErrorRate(from, to, (error + 1)
							 / (from_marginal + this.categories.size()));
			}
			rowDenominator.put(from, 1.);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ipeirotis.gal.ConfusionMatrix#addError(java.lang.String,
	 * java.lang.String, java.lang.Double)
	 */
	@Override
	public void addError(String from, String to, Double error) {
		CategoryPair cp = new CategoryPair(from, to);
		double currentError = matrix.containsKey(cp) ? matrix.get(cp) : 0;
		incrementRowDenominator(from, error);
		this.matrix.put(cp, currentError + error);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ipeirotis.gal.ConfusionMatrix#removeError(java.lang.String,
	 * java.lang.String, java.lang.Double)
	 */
	@Override
	public void removeError(String from, String to, Double error) {
		CategoryPair cp = new CategoryPair(from, to);
		double currentError = matrix.containsKey(cp) ? matrix.get(cp) : 0;
		decrementRowDenominator(from, error);
		this.matrix.put(cp, Math.max(0, currentError - error));

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ipeirotis.gal.ConfusionMatrix#getErrorRateBatch(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public double getErrorRateBatch(String from, String to) {
		CategoryPair cp = new CategoryPair(from, to);
		return matrix.containsKey(cp) ? matrix.get(cp) : 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ipeirotis.gal.ConfusionMatrix#getNormalizedErrorRate(java.lang.String
	 * , java.lang.String)
	 */
	@Override
	public double getNormalizedErrorRate(String from, String to) {
		CategoryPair cp = new CategoryPair(from, to);
		return matrix.get(cp) / rowDenominator.get(from);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ipeirotis.gal.ConfusionMatrix#getLaplaceNormalizedErrorRate(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public double getLaplaceNormalizedErrorRate(String from, String to) {
		CategoryPair cp = new CategoryPair(from, to);
		return (1. + matrix.get(cp))
			   / (rowDenominator.get(from) + categories.size());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ipeirotis.gal.ConfusionMatrix#getIncrementalErrorRate(java.lang.String
	 * , java.lang.String)
	 */
	@Override
	public double getIncrementalErrorRate(String from, String to) {
		CategoryPair cp = new CategoryPair(from, to);
		return matrix.get(cp) / rowDenominator.get(from);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ipeirotis.gal.ConfusionMatrix#setErrorRate(java.lang.String,
	 * java.lang.String, java.lang.Double)
	 */
	@Override
	public void setErrorRate(String from, String to, Double cost) {
		CategoryPair cp = new CategoryPair(from, to);
		matrix.put(cp, cost);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MultinomialConfusionMatrix))
			return false;
		MultinomialConfusionMatrix other = (MultinomialConfusionMatrix) obj;
		return Objects.equal(categories, other.categories) &&
			Objects.equal(matrix, other.matrix) &&
			Objects.equal(rowDenominator, other.rowDenominator);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(categories, matrix, rowDenominator);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ipeirotis.gal.ConfusionMatrix#getCategories()
	 */
	@Override
	public Set<String> getCategories() {
		return new HashSet<String>(categories);
	}

	public static class ConfusionMatrixDeserializer implements
		JsonDeserializer<MultinomialConfusionMatrix> {

		@Override
		public MultinomialConfusionMatrix deserialize(JsonElement json,
				Type type, JsonDeserializationContext context)
		throws JsonParseException {
			JsonObject jobject = (JsonObject) json;
			Collection<String> categories =
					context.deserialize(jobject.get("categories"), JSONUtils.stringSetType);
			
			
			Collection<MatrixValue> matrixValues =
					context.deserialize(jobject.get("matrix"), JSONUtils.matrixValuesCollectionType);
			Map<CategoryPair, Double> matrix = new HashMap<CategoryPair, Double>();
			for (MatrixValue mv : matrixValues){
				matrix.put(new CategoryPair(mv.from, mv.to), mv.value);
			}
			
			
			Collection<CategoryValue> rowDenominatorValues =
					context.deserialize(jobject.get("rowDenominator"), JSONUtils.categoryValuesCollectionType);
			Map<String, Double> rowDenominator = new HashMap<String, Double>();
			for (CategoryValue cv : rowDenominatorValues) {
				rowDenominator.put(cv.categoryName, cv.value);
			}
			
			return new MultinomialConfusionMatrix(categories, matrix, rowDenominator);
		}
	}
	
	public static class ConfusionMatrixSerializer implements JsonSerializer<MultinomialConfusionMatrix> {

		@Override
		public JsonElement serialize(MultinomialConfusionMatrix arg0,
				Type arg1, JsonSerializationContext arg2) {
			JsonObject ret = new JsonObject();

			Collection<CategoryValue> cp = new ArrayList<CategoryValue>(arg0.rowDenominator.size());
			for (Entry<String, Double> e : arg0.rowDenominator.entrySet()){
				cp.add(new CategoryValue(e.getKey(), e.getValue()));
			}
			ret.add("rowDenominator", arg2.serialize(cp));
			
			Collection<MatrixValue> mv = new ArrayList<MatrixValue>(arg0.matrix.size());
			for (Entry<CategoryPair, Double> e : arg0.matrix.entrySet()){
				mv.add(new MatrixValue(e.getKey().from, e.getKey().to, e.getValue()));
			}
			ret.add("matrix", arg2.serialize(mv));
			
			ret.add("categories", arg2.serialize(arg0.getCategories()));
			return ret;
		}
		
	}

}
