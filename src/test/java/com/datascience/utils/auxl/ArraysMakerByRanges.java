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
