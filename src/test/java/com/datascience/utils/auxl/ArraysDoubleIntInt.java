/**
 * 
 */
package com.datascience.utils.auxl;

/**
 * @author Michael Arshynov
 *
 */
public class ArraysDoubleIntInt {
	private double[] first;
	private int[] second;
	private int[] third;
	private int pos = 0;
	
	/**
	 * @param size
	 */
	public ArraysDoubleIntInt(int size) {
		first = new double[size];
		second = new int[size];
		third = new int[size];
	}
	/**
	 * @param firstVal
	 * @param secondVal
	 * @param thirdVal
	 */
	public void add(double firstVal, int secondVal, int thirdVal) {
		first[pos] = firstVal;
		second[pos] = secondVal;
		third[pos++] = thirdVal;
	}
	/**
	 * @return the first
	 */
	public double getFirstByIndex(int i) {
		return first[i];
	}
	/**
	 * @return the second
	 */
	public int getSecondByIndex(int i) {
		return second[i];
	}
	/**
	 * @return the third
	 */
	public int getThirdByIndex(int i) {
		return third[i];
	}
}
