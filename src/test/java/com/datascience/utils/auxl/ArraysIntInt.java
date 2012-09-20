/*******************************************************************************
 * Copyright (c) 2012 Panagiotis G. Ipeirotis & Josh M. Attenberg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.datascience.utils.auxl;

/**
 * @author Michael Arshynov
 *
 */
public class ArraysIntInt {
	private int[] first;
	private int[] second;
	private int pos = 0;

	/**
	 * @param size
	 */
	public ArraysIntInt(int size) {
		this.first = new int[size];
		this.second = new int[size];
	}
	/**
	 * @param firstVal
	 * @param secondVal
	 */
	public void add(int firstVal, int secondVal) {
		first[pos] = firstVal;
		second[pos++] = secondVal;
	}

	/**
	 * @return
	 */
	public int getFirstByIndex(int i) {
		return first[i];
	}
	/**
	 * @return
	 */
	public int getSecondByIndex(int i) {
		return second[i];
	}
	public int len() {
		return pos;
	}
}
