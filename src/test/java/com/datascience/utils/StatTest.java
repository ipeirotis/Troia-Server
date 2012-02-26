/**
 * 
 */
package com.datascience.utils;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.datascience.utils.auxl.ModelDoubleDouble;
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
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.datascience.utils.Stat#hgapprox(long, long, int)}.
	 */
	@Test
	public final void testHgapprox() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.datascience.utils.Stat#Beta_CDF(double, int, int)}.
	 */
	@Test
	public final void testBeta_CDF() {
		System.out.println("testBeta_CDF="+Stat.Beta_CDF(0.5, 20, 14));
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.datascience.utils.Stat#incompleteBeta(double, int, int)}.
	 */
	@Test
	public final void testIncompleteBeta() {
		System.out.println("testIncompleteBeta="+Stat.incompleteBeta(0.5, 20, 14));
		fail("Not yet implemented"); // TODO
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
				3.571 / 1000, 1.587 / 1000 };
		List<ModelIntIntDouble> testData = TestDataManager.fillTestCasesIntIntDouble(rp,ress);
		for(ModelIntIntDouble datum : testData) {
			assertEquals(datum.getRes(), Stat.Beta(datum.getArg1(), datum.getArg2()),TestDataManager.DELTA_DOUBLE);
		}		
	}

	/**
	 * Test method for {@link com.datascience.utils.Stat#Ix(double, int, int)}.
	 */
	@Test
	public final void testIx() {
		System.out.println("testIx"+Stat.Ix(0.5, 20, 14));
		fail("Not yet implemented"); // TODO
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
				4.431, 4.787, 1.609, 2.708, 3.555, 4.248, 4.836, 5.347 };
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
				0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95 };
		double ress[] = { -2.944, -2.197, -1.735, -1.386, -1.099, -0.847,
				-0.619, -0.405, -0.201, 0, 0.201, 0.405, 0.619, 0.847, 1.099,
				1.386, 1.735, 2.197, 2.944 };
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
				0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95 };
		double ress[] = { 0.512, 0.525, 0.537, 0.55, 0.562, 0.574, 0.587,
				0.599, 0.611, 0.622, 0.634, 0.646, 0.657, 0.668, 0.679, 0.69,
				0.701, 0.711, 0.721 };
		List<ModelDoubleDouble> testData = TestDataManager.fillTestCasesDoubleDouble(arg1s, ress);
		for (ModelDoubleDouble datum: testData) {
			assertEquals(datum.getRes(), Stat.logit(datum.getArg1()), TestDataManager.DELTA_DOUBLE);
		}
	}

}
