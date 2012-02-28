/**
 * 
 */
package com.datascience.utils;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.datascience.utils.auxl.TestDataManager;

/**
 * @author Michael Arshynov
 *
 */
public class UtilsTest {

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
	 * Test method for {@link com.datascience.utils.Utils#cleanLine(java.lang.String)}.
	 */
	@Test
	public final void testCleanLine() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.datascience.utils.Utils#getFile(java.lang.String)}.
	 */
	@Test
	public final void testGetFile() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.datascience.utils.Utils#writeFile(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testWriteFile() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link com.datascience.utils.Utils#round(double, int)}.
	 */
	@Test
	public final void testRound() {
		double ress[] =
			{ -10.0, 10.0, -110.0, 110.0, 0.0, -110.0, 0.0, 0.0, 1000.0, -1000.0,
				1000.0, -1000.0, -11.0, 11.0, -105.0, 105.0, 1.0, -105.0, 0.0,
				0.0, 1001.0, -1001.0, 1000.0, -1000.0, -10.5, 10.5, -105.1,
				105.1, 1.1, -105.1, 0.0, 0.0, 1000.5, -1000.5, 1000.5, -1000.5,
				-10.51, 10.51, -105.1, 105.1, 1.05, -105.1, 0.0, 0.0, 1000.5,
				-1000.5, 1000.49, -1000.49, -10.51, 10.51, -105.1, 105.1,
				1.051, -105.1, 0.0, 0.0, 1000.5, -1000.5, 1000.49, -1000.49,
				-10.51, 10.51, -105.1, 105.1, 1.051, -105.1, 0.0001, -0.0001,
				1000.5, -1000.5, 1000.49, -1000.49, -10.51, 10.51, -105.1,
				105.1, 1.051, -105.1, 0.0001, -0.0001, 1000.5, -1000.5,
				1000.49, -1000.49 };
		double arg1s[] = {	-10.51, +10.51, 
							-10.51e+1, +10.51e+1,
							+10.51e-1, -10.51e+1,
							+0.0001, -0.0001,
							+1000.5, -1000.5,
							+1000.49, -1000.49
							};
		int arg2s[] = {-1, 0,1,2,3,4,5};
		for (int i=0; i<arg2s.length; i++) {
			for (int j=0; j<arg1s.length; j++) {
				double one = arg1s[j];
				int two = arg2s[i];
				double res = Utils.round(one, two);
				int inx = i*arg1s.length+j;
				assertEquals(res, ress[inx], TestDataManager.DELTA_DOUBLE);
			}
		}
	}

	/**
	 * Test method for {@link com.datascience.utils.Utils#entropy(double[])}.
	 */
	@Test
	public final void testEntropy() {
		double assay[] = {-1, 1};
		double r1 = Utils.entropy(assay);
		System.out.println(r1); 
	}

}
