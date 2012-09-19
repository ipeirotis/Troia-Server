/**
 * 
 */
package com.datascience.utils.auxl;

/**
 * @author Michael Arshynov
 *
 */
public class ModelDoubleDouble {
	private double arg1;
	private double res;
	/**
	 * @return the arg1
	 */
	public double getArg1() {
		return arg1;
	}
	/**
	 * @param arg1 the arg1 to set
	 */
	public void setArg1(double arg1) {
		this.arg1 = arg1;
	}
	/**
	 * @return the res
	 */
	public double getRes() {
		return res;
	}
	/**
	 * @param res the res to set
	 */
	public void setRes(double res) {
		this.res = res;
	}
	
	/**
	 * @param arg1
	 * @param res
	 */
	public ModelDoubleDouble(double arg1, double res) {
		this.arg1 = arg1;
		this.res = res;
	}
}
