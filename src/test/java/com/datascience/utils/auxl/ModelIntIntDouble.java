package com.datascience.utils.auxl;

/**
 * @author Michael Arshynov
 *
 *	model for test date
 */
public class ModelIntIntDouble {
	private int arg1;
	private int arg2;
	private double res;
	/**
	 * @return
	 */
	public int getArg1() {
		return arg1;
	}
	/**
	 * @return
	 */
	public int getArg2() {
		return arg2;
	}
	/**
	 * @return
	 */
	public double getRes() {
		return res;
	}
	/**
	 * @param arg1
	 * @param arg2
	 * @param res
	 */
	public ModelIntIntDouble(int arg1, int arg2, double res) {
		this.arg1 = arg1;
		this.arg2 = arg2;
		this.res = res;
	}
}
