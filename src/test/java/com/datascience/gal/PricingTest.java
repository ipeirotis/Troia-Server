/**
 * 
 */
package com.datascience.gal;

import static org.junit.Assert.*;

import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Michael Arshynov
 *
 */
public class PricingTest {

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
	 * Test method for {@link com.datascience.gal.Pricing#getNormalizedP_simple(double)}.
	 */
	@Test
	public final void testGetNormalizedP_simple() {
		for (double i=0; i<=1; i+=0.05) {
			double normalizedProbability = Pricing.getNormalizedP_simple(i);
			assertTrue(normalizedProbability>0 && normalizedProbability<=1);
		}
	}

	/**
	 * Test method for {@link com.datascience.gal.Pricing#getNormalizedQexp_simple(double)}.
	 */
	@Test
	public final void testGetNormalizedQexp_simple() {
		for (double i=0; i<=1; i+=0.05) {
			double normalizedProbability = Pricing.getNormalizedQexp_simple(i);
			assertTrue(normalizedProbability>0 && normalizedProbability<=1);
		}
	}

	/**
	 * Test method for {@link com.datascience.gal.Pricing#probabilityCorrect(java.lang.Double, java.lang.Integer)}.
	 */
	@Test
	public final void testProbabilityCorrectTestOnNaN() {
		for (double p=0; p<1.05; p+=0.05) {
			for (int k=-1; k<=100; k++) {
				Double probabilityCorrect = Pricing.probabilityCorrect(new Double(p), new Integer(k));
//				System.out.println("["+p+","+k+"]"+
//						Pricing.probabilityCorrect(new Double(p), new Integer(k)));
				if (probabilityCorrect!=null)
				assertFalse("result "+probabilityCorrect+" should be a number or null",Double.isNaN(probabilityCorrect));
			}
		}
	}

	/**
	 * Test method for {@link com.datascience.gal.Pricing#probabilityCorrect(java.lang.Double, java.lang.Integer)}.
	 */
	@Test
	public final void testProbabilityCorrectTestOnNegativeProbability() {
		for (double p=-10; p<-0.05; p+=0.05) {
			for (int k=-50; k<0; k++) {
				Double probabilityCorrect = Pricing.probabilityCorrect(new Double(p), new Integer(k));
				System.out.println("["+p+","+k+"]"+
						Pricing.probabilityCorrect(new Double(p), new Integer(k)));
				assertNull(probabilityCorrect);
//				assertFalse("result should be a number or null",Double.isNaN(probabilityCorrect));
			}
		}
	}
	
	/**
	 * Test method for {@link com.datascience.gal.Pricing#probabilityCorrect(java.lang.Double, java.lang.Integer)}.
	 */
	@Test
	public final void testProbabilityCorrectTestOnRange() {
		for (double p=0.5; p<1.0; p+=0.05) {
			for (int k=0; k<=100; k++) {
				Double probabilityCorrect = Pricing.probabilityCorrect(new Double(p), new Integer(k));
//				System.out.println("["+p+","+k+"]"+
//				Pricing.probabilityCorrect(new Double(p), new Integer(k)));
				assertTrue("probability "+probabilityCorrect+"is out of [0,1]",probabilityCorrect>=0 && probabilityCorrect<=1);
			}
		}
	}
	
	/**
	 * Test method for {@link com.datascience.gal.Pricing#pricingFactor(double, double)}.
	 */
	@Test
	public final void testPricingFactor() {
		for (double from=0; from<1.0; from+=0.05) {
			for (double to=0; to<=1.0; to+=0.05) {
				Double pricingFactor = Pricing.pricingFactor(new Double(from), new Double(to));
//				System.out.println("["+from+","+to+"]"+pricingFactor);
//				assertTrue(pricingFactor==null||(pricingFactor>=0 && pricingFactor<=1));
				assertTrue(pricingFactor==null||pricingFactor>=0);
			}
		}
	}

}
