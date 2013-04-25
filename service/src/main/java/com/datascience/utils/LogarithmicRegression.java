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


/**
 * @author Jing Wang
 *
 */
public class LogarithmicRegression {

	/**
	 *
	 */
	public LogarithmicRegression() {
	}
	/**
	 * @param x
	 * @param y
	 * @param n
	 * @param target
	 * @return
	 */
	public static double getValue(double[] x, double[] y, int n, double target) {

		double[] logx = new double[n];

		for(int i = 0; i < n; i ++) {

			logx[i] = Math.log(x[i]);

		}

		double[] beta = getBeta(logx,y,n);

		return Math.exp((target - beta[0])/beta[1]);

	}
	/**
	 * @param x
	 * @param y
	 * @param n
	 * @return
	 */
	public static double[] getBeta(double[] x, double[] y, int n) {

		double sumx = getSum_x(x,n);
		double sumy = getSum_y(y,n);
		double xbar = sumx / n;
		double ybar = sumy / n;

		// second pass: compute summary statistics
		double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
		for (int i = 0; i < n; i++) {
			xxbar += (x[i] - xbar) * (x[i] - xbar);
			yybar += (y[i] - ybar) * (y[i] - ybar);
			xybar += (x[i] - xbar) * (y[i] - ybar);
		}

		double beta1 = xybar / xxbar;
		double beta0 = ybar - beta1 * xbar;
		//System.out.println(xxbar+"***"+beta1+"***"+beta0);
		double[] beta = {beta0,beta1};
		return beta;
	}
	/**
	 * @param x
	 * @param y
	 * @param n
	 * @return
	 */
	public static double[] getBeta_2(double[] x, double[] y, int n) {

		double sumx = getSum_x(x,n);
		double sumy = getSum_y(y,n);
		double sumx2 = getSum_x2(x,n);
		double sumxy = getSum_xy(x,y,n);

		double beta0 = (sumy*sumx2 - sumx*sumxy)/(n*sumx2-sumx*sumx);
		double beta1 = (n*sumxy - sumx*sumy)/(n*sumx2-sumx*sumx);
		double[] beta = {beta0,beta1};
		return beta;
	}
	/**
	 * @param x
	 * @param y
	 * @param logy
	 * @param n
	 * @return
	 */
	public static double[] getBeta_3(double[] x, double[] y, double[] logy, int n) {

//		double sumx = getSum_x(x,n);
		double sumy = getSum_y(y,n);
//		double sumx2 = getSum_x2(x,n);
		double sumxy = getSum_xy(x,y,n);
		double sumx2y = getSum_x2y(x,y,n);
		double sumylogy = getSum_xy(y,logy,n);
		double sumxylogy = getSum_xylogy(x, y, logy, n);

		double beta0 = (sumx2y*sumylogy - sumxy*sumxylogy)/(sumy*sumx2y - sumxy*sumxy);
		double beta1 = (sumy*sumxylogy - sumxy*sumylogy)/(sumy*sumx2y - sumxy*sumxy);
		System.out.println(sumx2y+" "+sumylogy+" "+sumxy+" "+sumxylogy+" "+sumy+" "+sumxylogy);
		double[] beta = {beta0,beta1};
		return beta;
	}
	/**
	 * @param x
	 * @param n
	 * @return
	 */
	public static double getSum_x(double[] x, int n) {
		double sum_x = 0.0;
		for(int i = 0; i < n; i ++)
			sum_x += x[i];
		return sum_x;
	}
	/**
	 * @param x
	 * @param n
	 * @return
	 */
	public static double getSum_x2(double[] x, int n) {
		double sum_x2 = 0.0;
		for(int i = 0; i < n; i ++)
			sum_x2 += x[i]*x[i];
		return sum_x2;
	}
	/**
	 * @param x
	 * @param y
	 * @param n
	 * @return
	 */
	public static double getSum_xy(double[] x, double[] y, int n) {
		double sum_xy = 0.0;
		for(int i = 0; i < n; i ++)
			sum_xy += x[i]*y[i];
		return sum_xy;
	}
	/**
	 * @param x
	 * @param y
	 * @param logy
	 * @param n
	 * @return
	 */
	public static double getSum_xylogy(double[] x, double[] y, double[] logy, int n) {
		double sum_xylogy = 0.0;
		for(int i = 0; i < n; i ++)
			sum_xylogy += x[i]*y[i]*logy[i];
		return sum_xylogy;
	}
	/**
	 * @param y
	 * @param n
	 * @return
	 */
	public static double getSum_y(double[] y, int n) {
		double sum_y = 0.0;
		for(int i = 0; i < n; i ++)
			sum_y += y[i];
		return sum_y;
	}
	/**
	 * @param x
	 * @param y
	 * @param n
	 * @return
	 */
	public static double getSum_x2y(double[] x, double[] y, int n) {
		double sum_x2y = 0.0;
		for(int i = 0; i < n; i ++)
			sum_x2y += x[i]*x[i]*y[i];
		return sum_x2y;
	}
}
