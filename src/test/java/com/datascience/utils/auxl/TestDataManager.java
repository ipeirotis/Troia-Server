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

}
