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

import java.util.Set;

public interface ConfusionMatrix {

	public abstract void incrementRowDenominator(String from, double value);

	public abstract void decrementRowDenominator(String from, double value);

	public abstract void empty();

	/**
	 * Makes the matrix to be row-stochastic: In other words, for a given "from"
	 * category, if we sum the errors across all the "to" categories, we get 1.0
	 */
	public abstract void normalize();

	/**
	 * Makes the matrix to be row-stochastic: In other words, for a given "from"
	 * category, if we sum the errors across all the "to" categories, we get
	 * 1.0.
	 *
	 * We use Laplace smoothing
	 */
	public abstract void normalizeLaplacean();

	public abstract void addError(String from, String to, Double error);

	public abstract void removeError(String from, String to, Double error);

	public abstract double getErrorRateBatch(String from, String to);

	public abstract double getNormalizedErrorRate(String from, String to);

	public abstract double getLaplaceNormalizedErrorRate(String from, String to);

	public abstract double getIncrementalErrorRate(String from, String to);

	public abstract void setErrorRate(String from, String to, Double cost);

	public abstract String toString();

	public abstract Set<String> getCategories();

}
