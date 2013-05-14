/*******************************************************************************
 * Copyright (c) 2012 Panagiotis G. Ipeirotis & Josh M. Attenberg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.datascience.utils;

public class Stat {

	/**
	 * Computes the value of the hypergeometric distribution for the passed
	 * values. To avoid overflows when computing the factorials, we use a small
	 * trick by taking initially the logarithms and then returning the exponent.
	 *
	 * log(N!) = log (1*2*...*N = log1 + log2 + .... + log N = Sum(log(i),
	 * i=1..N);
	 *
	 * @param D
	 *            Size of database
	 * @param gt
	 *            Token degree
	 * @param S
	 *            Sample size
	 * @return The value of the hypergeometric
	 */
	public static double hg(long D, long gt, long S) {

		double dgt = 0;
		for (int i = 1; i <= D - gt; i++) {
			dgt += Math.log(i);
		}

		double ds = 0;
		for (int i = 1; i <= D - S; i++) {
			ds += Math.log(i);
		}

		double dgts = 0;
		for (int i = 1; i <= D - gt - S; i++) {
			dgts += Math.log(i);
		}

		double d = 0;
		for (int i = 1; i <= D; i++) {
			d += Math.log(i);
		}

		double P = Math.exp(dgt + ds - dgts - d);

		return P;
	}

	/**
	 * Computes the value of the hypergeometric distribution for the passed
	 * values. To avoid overflows when computing the factorials, we use a small
	 * trick by taking initially the logarithms and then returning the exponent.
	 *
	 * log(N!) = log (1*2*...*N = log1 + log2 + .... + log N = Sum(log(i),
	 * i=1..N);
	 *
	 * @param D
	 *            Size of database
	 * @param gt
	 *            Token degree
	 * @param S
	 *            Sample size
	 * @return The value of the hypergeometric
	 */
	public static double hgapprox(long D, long gt, int S) {

		double dgt = logNfact(D - gt);

		double ds = logNfact(D - S);

		double dgts = logNfact(D - gt - S);

		double d = logNfact(D);

		double P = Math.exp(dgt + ds - dgts - d);

		return P;
	}

	public static double beta_CDF(double x, int a, int b) {
		return ix(x, a, b);
	}

	public static double incompleteBeta(double x, int a, int b) {
		return beta(a, b) * ix(x, a, b);
	}

	public static double beta(int a, int b) {
		return Math.exp(logNfactExact(a - 1) + logNfactExact(b - 1)
						- logNfactExact(a + b - 1));
	}

	public static double ix(double x, int a, int b) {

		double result = 0;
		for (int j = a; j <= a + b - 1; j++) {
			double m = Math.exp(logNfactExact(a + b - 1) - logNfactExact(j)
								- logNfactExact(a + b - 1 - j));
			double n = Math.pow(x, j) * Math.pow(1 - x, a + b - 1 - j);
			result += m * n;
		}
		return result;

	}

	/**
	 * Computing log(n!) using Stirling's approximation of n!
	 */
	public static double logNfact(long n) {

		if (n <= 0)
			return 0;

		if (n < 100)
			return logNfactExact(n);

		// Stirling's approximation:
		// double P = Math.log(2*Math.PI*n)/2 + n*Math.log(n/Math.E);

		// Gosper's approximation
		double P = Math.log((2 * n + 1.0 / 3) * Math.PI) / 2 + n
				   * Math.log(n / Math.E);

		return P;
	}

	/**
	 * Computing log(n!) using Stirling's approximation of n!
	 */
	private static double logNfactExact(long n) {

		if (n <= 0)
			return 0;

		double s = 0;
		for (int i = 1; i <= n; i++) {
			s += Math.log(i);
		}

		return s;
	}

	/**
	 * Computing log(n!) using Stirling's approximation of n!
	 */
	public static long nFactExact(long n) {

		if (n == 0)
			return 1;

		long s = 1;
		for (int i = 1; i <= n; i++) {
			s *= i;
		}

		return s;
	}

	/**
	 * Computing log(n!) using Stirling's approximation of n!
	 */
	public static double binom(int n, int i) {

		return 1.0 * nFactExact(n) / (nFactExact(i) * nFactExact(n - i));

	}

	public static double logBinom(int n, int i) {

		return logNfactExact(n) - (logNfactExact(i) + logNfactExact(n - i));

	}

	public static double logoggs(double p) {
		return Math.log(p) - Math.log(1 - p);
	}

	public static double logit(double logodds) {
		return Math.exp(logodds) / (1 + Math.exp(logodds));
	}

	public static void main(String[] args) {
		// Testing IncompleteBeta

		/*
		 * for (int a =1 ; a<10; a++) for (int b =1 ; b<10; b++)
		 * System.out.println("a="+a+" b="+b+" beta(a,b)="+beta(a,b));
		 */

		double x = 0.5;
		for (int a = 0; a <= 20; a++)
			for (int b = 0; b <= 20; b++)
				System.out.println("x=" + x + " pos=" + a + " neg=" + b
								   + " beta_CDF(x;pos,neg)=" + beta_CDF(x, a + 1, b + 1));

	}

}
