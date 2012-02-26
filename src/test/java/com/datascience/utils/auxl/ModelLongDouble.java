package com.datascience.utils.auxl;


/**
 * @author Michael Arshynov
 *
 *	model for test date
 */
public class ModelLongDouble {
	private long arg1;
	private double res;
	/**
	 * @return
	 */
	public long getArg1() {
		return arg1;
	}
	/**
	 * @return
	 */
	public double getRes() {
		return res;
	}
	/**
	 * @param arg1
	 * @param res
	 */
	public ModelLongDouble(long arg1, double res) {
		this.arg1 = arg1;
		this.res = res;
	}
}
