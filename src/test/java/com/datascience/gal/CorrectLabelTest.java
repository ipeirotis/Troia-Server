/**
 *
 */
package com.datascience.gal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Michael Arshynov
 *
 */
public class CorrectLabelTest {

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
	 * Test method for {@link com.datascience.gal.CorrectLabel#hashCode()}.
	 */
	@Test
	public final void testHashCode() {
		doTestEqualsHashCodeOrBoth(EToTestOnEquals.EQUALS);
	}
	/**
	 * Test method for {@link com.datascience.gal.CorrectLabel#hashCode()}.
	 */
	@Test
	public final void testHashCodeAndEquals() {
		doTestEqualsHashCodeOrBoth(EToTestOnEquals.BOTH);
	}
	/**
	 * Test method for {@link com.datascience.gal.CorrectLabel#CorrectLabel(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testCorrectLabel() {
		doTestGettersAndConstructor();
	}

	/**
	 * Test method for {@link com.datascience.gal.CorrectLabel#getObjectName()}.
	 */
	@Test
	public final void testGetObjectName() {
		doTestGettersAndConstructor();
	}

	/**
	 * Test method for {@link com.datascience.gal.CorrectLabel#getCorrectCategory()}.
	 */
	@Test
	public final void testGetCorrectCategory() {
		doTestGettersAndConstructor();
	}

	/**
	 *
	 */
	public final void doTestGettersAndConstructor() {
		String[] objectNames = {"on1", "on2", "", "on4"};
		String[] correctCategories = {"cc1", "cc2", "cc3", null};
		for (int i=0; i<objectNames.length; i++) {
			CorrectLabel correctLabel = new CorrectLabel(objectNames[i],correctCategories[i]);
			String objectName = correctLabel.getObjectName();
			String correctCategory = correctLabel.getCorrectCategory();
			assertEquals(objectName, objectNames[i]);
			assertEquals(correctCategory, correctCategories[i]);
		}
	}

	class P {
		int first;
		int second;
		public P(int first, int second) {
			this.first = first;
			this.second = second;
		}
	}
	/**
	 * Test method for {@link com.datascience.gal.CorrectLabel#equals(java.lang.Object)}.
	 */
	@Test
	public final void testEqualsObject() {
		doTestEqualsHashCodeOrBoth(EToTestOnEquals.EQUALS);
	}

	/**
	 * @author Michael Arshynov
	 *
	 */
	enum EToTestOnEquals {
		EQUALS, HASHCODE, BOTH
	}
	/**
	 * @param eWhich
	 */
	public final void doTestEqualsHashCodeOrBoth(EToTestOnEquals eWhich) {
		String[] objectNames = {"on1", "on1", "", "on4", "on4"};
		String[] correctCategories = {"cc1", "cc1", "cc3", null, null};
		int len = objectNames.length;
		CorrectLabel[] correctLabels = new CorrectLabel[len];

		for (int i=0; i<len; i++) {
			correctLabels[i] = new CorrectLabel(objectNames[i],correctCategories[i]);
		}
		P[] theSames = { new P(0,1), new P(3,4) };
		P[] differents = { 	new P(0,2), new P(0,3), new P(0,4),
			  new P(1,2), new P(1,3), new P(1,4),
			  new P(2,3), new P(2,4)
		};
		for (int i=0; i<theSames.length; i++) {
			int first = theSames[i].first;
			int second = theSames[i].second;
			if (eWhich == EToTestOnEquals.EQUALS) {
				assertTrue(correctLabels[first].equals(correctLabels[second]));
			} else if (eWhich == EToTestOnEquals.HASHCODE) {
				assertEquals(correctLabels[first].hashCode(),correctLabels[second].hashCode());
			} else if (eWhich == EToTestOnEquals.BOTH) {
				assertTrue(correctLabels[first].equals(correctLabels[second]));
				assertEquals(correctLabels[first].hashCode(),correctLabels[second].hashCode());
			}
		}
		for (int i=0; i<differents.length; i++) {
			int first = differents[i].first;
			int second = differents[i].second;
			if (eWhich == EToTestOnEquals.EQUALS) {
				assertFalse(correctLabels[first].equals(correctLabels[second]));
			} else if (eWhich == EToTestOnEquals.HASHCODE) {
				assertNotSame(correctLabels[first].hashCode(),correctLabels[second].hashCode());
			} else if (eWhich == EToTestOnEquals.BOTH) {
				assertFalse(correctLabels[first].equals(correctLabels[second]));
				assertNotSame(correctLabels[first].hashCode(),correctLabels[second].hashCode());
			}
		}
	}
}
