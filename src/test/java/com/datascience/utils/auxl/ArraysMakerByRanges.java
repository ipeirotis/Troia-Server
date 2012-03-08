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

import static org.junit.Assert.fail;

/**
 * @author Michael Arshynov
 *
 */
public class ArraysMakerByRanges {

	
	/**
	 * @param firstRangeStart
	 * @param firstRangeEnd
	 * @param secondRangeStart
	 * @param secondRangeEnd
	 * @return
	 */
	public static ArraysIntInt makeIntIntArraysByRangeBothRevert(int firstRangeStart, int firstRangeEnd, int secondRangeStart, int secondRangeEnd) {
		if (firstRangeStart>firstRangeEnd || secondRangeStart>secondRangeEnd) {
			fail("wrong input parameters("+firstRangeStart+","+firstRangeEnd+","+secondRangeStart+","+secondRangeEnd
					+") (ArraysMakerByRanges:makeIntIntArraysByRangeBoth)");
		}
		int size = (firstRangeEnd - firstRangeStart + 1) * (secondRangeEnd - secondRangeStart + 1);
		ArraysIntInt ret = new ArraysIntInt(size);
		for (int two=secondRangeStart; two<=secondRangeEnd; two++) {
			for (int one=firstRangeStart; one<=firstRangeEnd; one++) {
					ret.add(one,two);
			}
		}
		return ret;
	}
	/**
	 * @param rp
	 * @return
	 */
	public static ArraysIntInt makeIntIntArraysByRangeBothRevert(RangePairIntInt rp) {
		return makeIntIntArraysByRangeBothRevert(rp.getStart1(), rp.getEnd1(), rp.getStart2(), rp.getEnd2());
	}
	/**
	 * @return
	 */
	public ArraysIntInt makeIntIntArraysByRangeFirst() {
		return null;
	}

	public static ArraysDoubleIntInt makeDoubleIntIntArraysByRangeBothRevert(
			double[] argsX, RangePairIntInt rp) {
		if (argsX==null || rp==null || argsX.length<1 || !rp.isValid()) {
			fail("wrong input parameters("+rp.getStart1()+","+rp.getEnd1()+","+rp.getStart2()+","+rp.getEnd2()
					+") (ArraysMakerByRanges:makeDoubleIntIntArraysByRangeBothRevert)");
		}
		int size  = rp.calculateSize()*argsX.length;
		ArraysDoubleIntInt ret = new ArraysDoubleIntInt(size);
		for (int third = rp.getStart2(); third<=rp.getEnd2(); third+=rp.getPitch2()) {
			for (int second = rp.getStart1(); second<=rp.getEnd1(); second+=rp.getPitch1()) {
				for (int first=0; first<argsX.length; first++) {
					ret.add(argsX[first], second, third);
				}
			}
		}
		return ret;
	}

}
