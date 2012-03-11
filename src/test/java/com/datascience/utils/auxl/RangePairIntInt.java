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
public class RangePairIntInt {
	private int start1;
	private int start2;
	private int end1;
	private int end2;
	private int secondValue1;
	private int secondValue2;
	private boolean isNatural1;
	private boolean isNatural2;
	private int pitch1;
	private int pitch2;
	/**
	 * 
	 */
	public RangePairIntInt() {
		isNatural1 = true;
		isNatural2 = true;
		pitch1 = 1;
		pitch2 = 1;
	}
	
	/**
	 * @param start1
	 * @param end1
	 */
	public void setRange1(int start1, int end1) {
		this.start1 = start1;
		this.end1 = end1;
	}
	/**
	 * @param start1
	 * @param secondValue1
	 * @param end1
	 */
	public void setRange1(int start1, int secondValue1, int end1) {
		this.isNatural1 = false;
		this.secondValue1 = secondValue1;
		this.start1 = start1;
		this.end1 = end1;
		this.pitch1 = secondValue1 - start1;
	}
	/**
	 * @param start2
	 * @param end2
	 */
	public void setRange2(int start2, int end2) {
		this.start2 = start2;
		this.end2 = end2;
	}
	/**
	 * @param start2
	 * @param secondValue2
	 * @param end2
	 */
	public void setRange2(int start2, int secondValue2, int end2) {
		this.isNatural2 = false;
		this.secondValue2 = secondValue2;
		this.start2 = start2;
		this.end2 = end2;
		this.pitch2 = secondValue2 - start2;
	}
	/**
	 * @return
	 */
	public int getStart1() {
		return start1;
	}
	/**
	 * @return
	 */
	public int getStart2() {
		return start2;
	}
	/**
	 * @return
	 */
	public int getEnd1() {
		return end1;
	}
	/**
	 * @return
	 */
	public int getEnd2() {
		return end2;
	}
	/**
	 * @return
	 */
	public boolean isValid() {
		return start1<=end1 && start2<=end2;
	}

	/**
	 * @return the secondValue1
	 */
	public int getSecondValue1() {
		return secondValue1;
	}

	/**
	 * @return the secondValue2
	 */
	public int getSecondValue2() {
		return secondValue2;
	}

	/**
	 * @return
	 */
	public int calculateSize() {
		int size1 = 0;
		int size2 = 0;
		if (isNatural1) {
			size1 = end1-start1+1;
		} else {
			size1 = (end1-start1)/(secondValue1-start1) + 1;
		}
		if (isNatural2) {
			size2 = end2-start2+1;
		} else {
			size2 = (end2-start2)/(secondValue2-start2) + 1;
		}
		return size1*size2;
	}

	/**
	 * @return
	 */
	public int getPitch1() {
		return pitch1;
	}
	/**
	 * @return
	 */
	public int getPitch2() {
		return pitch2;
	}	
}
