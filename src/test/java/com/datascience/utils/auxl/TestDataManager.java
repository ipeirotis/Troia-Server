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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Arshynov
 *
 */
public class TestDataManager {
	/**
	 * @see org.junit.Assert#assertEquals(double expected, double actual, double delta)
	 */
	public static final double DELTA_DOUBLE = 0.001;
	public static final double DELTA_DOUBLE_testIncompleteBeta = 0.01;
	
	/**
	 * @param arg1s
	 * @param ress
	 * @return TestData Collection
	 */
	public static List<ModelLongDouble> fillTestCasesLongDouble(long[] arg1s, double[] ress) {
		List<ModelLongDouble> ret = null;
		if (arg1s==null || ress==null || arg1s.length<1 || ress.length<1 || arg1s.length!=ress.length) {
			fail("wrong input/output parameters (TestCasesManager:fillTestCasesLongDouble)");
		}
		ret = new ArrayList<ModelLongDouble>();
		for (int i=0; i<arg1s.length; i++) {
			ret.add(new ModelLongDouble(arg1s[i], ress[i]));
		}
		return ret;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	/**
	 * @param rp
	 * @param ress
	 * @return TestData Collection
	 */
	public static List<ModelIntIntDouble> fillTestCasesIntIntDouble(
			RangePairIntInt rp, double[] ress) {
		List<ModelIntIntDouble> ret = null;
		if (!rp.isValid() || ress == null || ress.length<1) {
			fail("wrong input/output parameters (TestCasesManager:fillTestCasesIntIntDouble)");
		}
		ArraysIntInt inputArrays = ArraysMakerByRanges.makeIntIntArraysByRangeBothRevert(rp);
		if (inputArrays.len()!=ress.length) {
			fail("the length of the inputs and outputs is not the same (TestCasesManager:fillTestCasesIntIntDouble)");
		}
		ret = new ArrayList<ModelIntIntDouble>();
		for (int i=0; i<ress.length; i++) {
			ret.add(new ModelIntIntDouble(inputArrays.getFirstByIndex(i), inputArrays.getSecondByIndex(i), ress[i]));
		}
		return ret;
	}

	/**
	 * @param arg1s
	 * @param ress
	 * @return
	 */
	public static List<ModelDoubleDouble> fillTestCasesDoubleDouble(
			double[] arg1s, double[] ress) {
		List<ModelDoubleDouble> ret = null;
		if (arg1s==null || ress==null || arg1s.length<1 || arg1s.length!=ress.length) {
			fail("wrong input/output parameters (TestCasesManager:fillTestCasesDoubleDouble)");
		}
		ret = new ArrayList<ModelDoubleDouble>();
		for (int i=0; i<arg1s.length; i++) {
			ret.add(new ModelDoubleDouble(arg1s[i], ress[i]));
		}
		return ret;
	}

	/**
	 * @param argsX
	 * @param rp
	 * @param ress
	 * @return
	 */
	public static List<ModelDoubleIntIntDouble> fillTestCasesDoubleIntIntDouble(
			double[] argsX, RangePairIntInt rp, double[] ress) {
		List<ModelDoubleIntIntDouble> ret = null;
		if (argsX==null || rp==null || ress==null || argsX.length<1 || ress.length<1) {
			fail("wrong input/output parameters (TestCasesManager:fillTestCasesDoubleIntIntDouble)");
		}
		ret = new ArrayList<ModelDoubleIntIntDouble>();
		ArraysDoubleIntInt inputArrays = ArraysMakerByRanges.makeDoubleIntIntArraysByRangeBothRevert(argsX, rp);
		for (int i = 0; i < ress.length; i++) {
				ret.add(new ModelDoubleIntIntDouble(inputArrays.getFirstByIndex(i),
						inputArrays.getSecondByIndex(i), inputArrays.getThirdByIndex(i), ress[i]));
		}
		return ret;
	}

}
