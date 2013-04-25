/**
 *
 */
package com.datascience.utils.auxl;

/**
 * @author Michael Arshynov
 *
 */
public class ModelDoubleIntIntDouble {
	private double arg1;
	private int arg2;
	private int arg3;
	private double res;
	/**
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @param res
	 */
	public ModelDoubleIntIntDouble(double arg1, int arg2, int arg3, double res) {
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.arg3 = arg3;
		this.res = res;
	}
	/**
	 * @return the arg1
	 */
	public double getArg1() {
		return arg1;
	}
	/**
	 * @return the arg2
	 */
	public int getArg2() {
		return arg2;
	}
	/**
	 * @return the arg3
	 */
	public int getArg3() {
		return arg3;
	}
	/**
	 * @return the res
	 */
	public double getRes() {
		return res;
	}
}
