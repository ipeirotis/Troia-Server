/**
 *
 */
package com.datascience.gal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import com.datascience.core.stats.CategoryPair;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Michael Arshynov
 *
 */
public class CategoryPairTest {
	String froms[] = {"src1", "src1","src3","src1", "", "src3"};
	String tos[] = {"dest1","dest1","dest3", "dest4", null, "dest3"};
	P[] theSames = {	new P(0,1),new P(2,5) };
	P[] differents = {	new P(0,2), new P(0,3), new P(0,4), new P(0,5),
		   new P(1,2), new P(1,3), new P(1,4),new P(1,5),
		   new P(2,3), new P(2,4),
		   new P(3,4), new P(3,5),
		   new P(4,5)
	};
	CategoryPair categoryPairs[] = new CategoryPair[froms.length];
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
		for (int i=0; i<categoryPairs.length; i++) {
			categoryPairs[i] = new CategoryPair(froms[i], tos[i]);
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.datascience.core.stats.CategoryPair#hashCode()}.
	 */
	@Test
	public final void testHashCode() {
		for (P p : theSames) {
			assertEquals(categoryPairs[p.one].hashCode(), categoryPairs[p.two].hashCode());
		}
		for (P p : differents) {
			assertNotSame(categoryPairs[p.one].hashCode(), categoryPairs[p.two].hashCode());
		}
	}

	/**
	 * @author Michael Arshynov
	 * P means Pairs
	 */
	class P {
		int one;
		int two;
		public P(int one, int two) {
			this.one = one;
			this.two = two;
		}
	}
	/**
	 * Test method for {@link com.datascience.core.stats.CategoryPair#equals(java.lang.Object)}.
	 */
	@Test
	public final void testEqualsObject() {
		for (P p : theSames) {
			assertTrue(categoryPairs[p.one].equals(categoryPairs[p.two]));
		}
		for (P p : differents) {
			assertFalse(categoryPairs[p.one].equals(categoryPairs[p.two]));
		}
	}

}
