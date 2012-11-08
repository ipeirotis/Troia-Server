/**
 *
 */
package com.datascience.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.datascience.utils.auxl.ModelDoubleDouble;
import com.datascience.utils.auxl.ModelDoubleIntIntDouble;
import com.datascience.utils.auxl.ModelIntIntDouble;
import com.datascience.utils.auxl.ModelLongDouble;
import com.datascience.utils.auxl.RangePairIntInt;
import com.datascience.utils.auxl.TestDataManager;


/**
 * @author Michael Arshynov
 *
 */
public class StatTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.datascience.utils.Stat#hg(long, long, long)}.
	 */
	@Test
	public final void testHg() {
		System.out.println(Stat.hg(10, 10, 20));
	}

	/**
	 * Test method for {@link com.datascience.utils.Stat#hgapprox(long, long, int)}.
	 */
	@Test
	public final void testHgapprox() {
		System.out.println(Stat.hgapprox(10, 10, 10));
	}

	/**
	 * Test method for {@link com.datascience.utils.Stat#Beta_CDF(double, int, int)}.
	 */
	@Test
	public final void testBeta_CDF() {
		testIxOrBeta_CDF();
	}

	/**
	 * @param aStart
	 * @param bStart
	 */
	private final void testIncompleteBetaWithLimitedParameters(int aStart, int bStart) {
		double ress[] = { 0.105, 0.693, 2.303, 5.361e-3, 0.193, 1.403,
						  3.605e-4, 0.068, 0.998, 2.718e-5, 0.026, 0.755, 2.182e-6,
						  0.011, 0.591, 0.095, 0.375, 0.495, 4.667e-3, 0.083, 0.162,
						  3.083e-4, 0.026, 0.079, 2.3e-5, 9.375e-3, 0.046, 1.833e-6,
						  3.646e-3, 0.03, 0.086, 0.234, 0.25, 4.073e-3, 0.041, 0.05,
						  2.642e-4, 0.011, 0.017, 1.949e-5, 3.571e-3, 7.123e-3, 1.542e-6,
						  1.297e-3, 3.553e-3, 0.078, 0.164, 0.167, 3.564e-3, 0.022,
						  0.024, 2.267e-4, 5.092e-3, 5.952e-3, 1.653e-5, 1.48e-3,
						  1.984e-3, 1.298e-6, 4.945e-4, 7.935e-4, 0.071, 0.125, 0.125,
						  3.127e-3, 0.014, 0.014, 1.95e-4, 2.626e-3, 2.778e-3, 1.404e-5,
						  6.718e-4, 7.576e-4, 1.093e-6, 2.036e-4, 2.525e-4, 0.065, 0.1,
						  0.1, 2.751e-3, 9.038e-3, 9.091e-3, 1.68e-4, 1.486e-3, 1.515e-3,
						  1.194e-5, 3.335e-4, 3.497e-4, 9.221e-7, 9.093e-5, 9.99e-5
						};
		double argsX[] = {0.1, 0.5, 0.9};
		RangePairIntInt rp = new RangePairIntInt();
		rp.setRange1(1, 2, 5);
		rp.setRange2(0, 2, 10);
		List<ModelDoubleIntIntDouble> testData = TestDataManager.fillTestCasesDoubleIntIntDouble(argsX, rp, ress);
		for (ModelDoubleIntIntDouble datum : testData) {
			double x = datum.getArg1();
			int a = datum.getArg2();
			int b = datum.getArg3();
			if (a>=aStart && b>=bStart) {
				assertEquals(datum.getRes(), Stat.incompleteBeta(x, a, b), TestDataManager.DELTA_DOUBLE_testIncompleteBeta);
			}
		}
	}
	/**
	 * Test method for {@link com.datascience.utils.Stat#incompleteBeta(double, int, int)}.
	 */
	@Test
	public final void testIncompleteBetaPositiveParametersOnly() {
		testIncompleteBetaWithLimitedParameters(1,1);
	}

	/**
	 * Test method for {@link com.datascience.utils.Stat#incompleteBeta(double, int, int)}.
	 */
	@Test
	public final void testIncompleteBetaWithZeroParameters() {
		testIncompleteBetaWithLimitedParameters(0,0);
	}
	/**
	 * Test method for {@link com.datascience.utils.Stat#incompleteBeta(double, int, int)}.
	 */
	@Test
	public final void testIncompleteBetaWithZeroParameterA() {
		testIncompleteBetaWithLimitedParameters(0,1);
	}
	/**
	 * Test method for {@link com.datascience.utils.Stat#incompleteBeta(double, int, int)}.
	 */
	@Test
	public final void testIncompleteBetaWithZeroParameterB() {
		testIncompleteBetaWithLimitedParameters(1,0);
	}
	/**
	 * Test method for {@link com.datascience.utils.Stat#Beta(int, int)}.
	 */
	@Test
	public final void testBeta() {
		RangePairIntInt rp = new RangePairIntInt();
		rp.setRange1(1, 5);
		rp.setRange2(1, 5);
		double ress[] = { 1, 0.5, 0.333, 0.25, 0.2, 0.5, 0.167, 0.083, 0.05,
						  0.033, 0.333, 0.083, 0.033, 0.017, 9.524 / 1000, 0.25, 0.05,
						  0.017, 7.143 / 1000, 3.571 / 1000, 0.2, 0.033, 9.524 / 1000,
						  3.571 / 1000, 1.587 / 1000
						};
		List<ModelIntIntDouble> testData = TestDataManager.fillTestCasesIntIntDouble(rp,ress);
		for(ModelIntIntDouble datum : testData) {
			assertEquals(datum.getRes(), Stat.Beta(datum.getArg1(), datum.getArg2()),TestDataManager.DELTA_DOUBLE);
		}
	}

	/**
	 *
	 */
	private void testIxOrBeta_CDF() {
		double argsX[] = {0.1, 0.5, 0.9};
		RangePairIntInt rp = new RangePairIntInt();
		rp.setRange1(1, 2, 5);
		rp.setRange2(1, 5);
		double ress[] = { 0.1, 0.5, 0.9, 0.01, 0.25, 0.81, 1e-3, 0.125, 0.729,
						  1e-4, 0.063, 0.656, 1e-5, 0.031, 0.59, 0.19, 0.75, 0.99, 0.028,
						  0.5, 0.972, 3.7e-3, 0.312, 0.948, 4.6e-4, 0.187, 0.919, 5.5e-5,
						  0.109, 0.886, 0.271, 0.875, 0.999, 0.052, 0.688, 0.996,
						  8.56e-3, 0.5, 0.991, 1.27e-3, 0.344, 0.984, 1.765e-4, 0.227,
						  0.974, 0.344, 0.938, 1, 0.081, 0.813, 1, 0.016, 0.656, 0.999,
						  2.728e-3, 0.5, 0.997, 4.317e-4, 0.363, 0.995, 0.41, 0.969, 1,
						  0.114, 0.891, 1, 0.026, 0.773, 1, 5.024e-3, 0.637, 1, 8.909e-4,
						  0.5, 0.999
						};
		List<ModelDoubleIntIntDouble> testData = TestDataManager.fillTestCasesDoubleIntIntDouble(argsX, rp, ress);
		for (ModelDoubleIntIntDouble datum : testData) {
			double x = datum.getArg1();
			int a = datum.getArg2();
			int b = datum.getArg3();
			assertEquals(datum.getRes(), Stat.Ix(x, a, b), TestDataManager.DELTA_DOUBLE);
		}
	}
	/**
	 * Test method for {@link com.datascience.utils.Stat#Ix(double, int, int)}.
	 */
	@Test
	public final void testIx() {
		testIxOrBeta_CDF();
	}

	/**
	 * Test method for {@link com.datascience.utils.Stat#logNfact(long)}.
	 */
	@Test
	public final void testLogNfact() {
		long arg1s[] = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		double ress[] = {0,0,0.693,1.792,3.178,4.787,6.579,8.525,10.605,12.802,15.104,17.502,19.987,22.552,25.191,27.899};
		List<ModelLongDouble> testData = TestDataManager.fillTestCasesLongDouble(arg1s, ress);
		for (ModelLongDouble datum : testData) {
			assertEquals(datum.getRes(), Stat.logNfact(datum.getArg1()), TestDataManager.DELTA_DOUBLE);
		}
	}

	/**
	 * Test method for {@link com.datascience.utils.Stat#NfactExact(long)}.
	 */
	@Test
	public final void testNfactExact() {
		assertTrue("method is invisible", true);
	}

	/**
	 * Test method for {@link com.datascience.utils.Stat#binom(int, int)}.
	 */
	@Test
	public final void testBinom() {
		RangePairIntInt rp = new RangePairIntInt();
		rp.setRange1(5, 9);
		rp.setRange2(1, 4);
		double ress[] = {5,6,7,8,9,10,15,21,28,36,10,20,35,56,84,5,15,35,70,126};
		List<ModelIntIntDouble> testData = TestDataManager.fillTestCasesIntIntDouble(rp,ress);
		for(ModelIntIntDouble datum : testData) {
			assertEquals(datum.getRes(), Stat.binom(datum.getArg1(), datum.getArg2()),TestDataManager.DELTA_DOUBLE);
		}
	}

	/**
	 * Test method for {@link com.datascience.utils.Stat#logBinom(int, int)}.
	 * for calculation data to compare with
	 * the Stirling's Approximation for n! is substituted for real ln(n!)
	 */
	@Test
	public final void testLogBinom() {
		RangePairIntInt rp = new RangePairIntInt();
		rp.setRange1(5, 10);
		rp.setRange2(1, 4);
		double ress[] = { 1.609, 1.792, 1.946, 2.079, 2.197, 2.303, 2.303,
						  2.708, 3.045, 3.332, 3.584, 3.807, 2.303, 2.996, 3.555, 4.025,
						  4.431, 4.787, 1.609, 2.708, 3.555, 4.248, 4.836, 5.347
						};
		List<ModelIntIntDouble> testData = TestDataManager.fillTestCasesIntIntDouble(rp,ress);
		for(ModelIntIntDouble datum : testData) {
			assertEquals(datum.getRes(), Stat.logBinom(datum.getArg1(), datum.getArg2()),TestDataManager.DELTA_DOUBLE);
		}
	}

	/**
	 * Test method for {@link com.datascience.utils.Stat#logoggs(double)}.
	 */
	@Test
	public final void testLogoggs() {
		double arg1s[] = { 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45,
						   0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95
						 };
		double ress[] = { -2.944, -2.197, -1.735, -1.386, -1.099, -0.847,
						  -0.619, -0.405, -0.201, 0, 0.201, 0.405, 0.619, 0.847, 1.099,
						  1.386, 1.735, 2.197, 2.944
						};
		List<ModelDoubleDouble> testData = TestDataManager.fillTestCasesDoubleDouble(arg1s, ress);
		for (ModelDoubleDouble datum: testData) {
			assertEquals(datum.getRes(), Stat.logoggs(datum.getArg1()), TestDataManager.DELTA_DOUBLE);
		}
	}

	/**
	 * Test method for {@link com.datascience.utils.Stat#logit(double)}.
	 */
	@Test
	public final void testLogit() {
		double arg1s[] = { 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45,
						   0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95
						 };
		double ress[] = { 0.512, 0.525, 0.537, 0.55, 0.562, 0.574, 0.587,
						  0.599, 0.611, 0.622, 0.634, 0.646, 0.657, 0.668, 0.679, 0.69,
						  0.701, 0.711, 0.721
						};
		List<ModelDoubleDouble> testData = TestDataManager.fillTestCasesDoubleDouble(arg1s, ress);
		for (ModelDoubleDouble datum: testData) {
			assertEquals(datum.getRes(), Stat.logit(datum.getArg1()), TestDataManager.DELTA_DOUBLE);
		}
	}

}
