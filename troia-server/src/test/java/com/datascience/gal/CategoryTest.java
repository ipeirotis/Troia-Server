/**
 *
 */
package com.datascience.gal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.datascience.core.stats.Category;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.datascience.utils.auxl.TestDataManager;

/**
 * @author Michael Arshynov
 *
 */
public class CategoryTest {

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
	 * Test method for {@link com.datascience.core.stats.Category#hashCode()}.
	 */
	@Test
	public final void testHashCode_ByNameParameterOnly() {
		Category category1 = new Category("category name 1");
		Category category2 = new Category("category name 2");
		Category category3 = new Category("category name 1");
		doTestHashCode(category1, category2, category3, true);
	}
	/**
	 * Test method for {@link com.datascience.core.stats.Category#hashCode()}.
	 */
	@Test @Ignore("FIXME - this test shows why category shouldn't store cost matrix")
	public final void testHashCode_ByNameAndCost() {
		Category category1 = new Category("category name 1");
		Category category2 = new Category("category name 2");
		Category category3 = new Category("category name 1");
		category1.setCost("dest1", 1.1);
		category2.setCost("dest2", 2.2);
		category3.setCost("dest3", 3.3);
		doTestHashCode(category1, category2, category3, false);
	}
	/**
	 * Test method for {@link com.datascience.core.stats.Category#hashCode()}.
	 */
	@Test
	public final void testHashCode_ByNameAndPrior() {
		Category category1 = new Category("category name 1");
		Category category2 = new Category("category name 2");
		Category category3 = new Category("category name 1");
		category1.setPrior(1e1);
		category2.setPrior(1e2);
		category3.setPrior(1e3);
		doTestHashCode(category1, category2, category3, false);
	}
	/**
	 * @param first
	 * @param second
	 * @param third
	 * @param is1and3TheSame
	 */
	public final void doTestHashCode(Category first, Category second, Category third, boolean is1and3TheSame) {
		int hashCode1 = first.hashCode();
		int hashCode2 = second.hashCode();
		int hashCode3 = third.hashCode();
		if (is1and3TheSame)
			assertTrue(first+"="+third,hashCode1==hashCode3);
		else
			assertFalse(first+"="+third,hashCode1==hashCode3);

		assertFalse(first+"="+second,hashCode1==hashCode2);
		assertFalse(third+"="+second,hashCode3==hashCode2);
	}
	/**
	 * Test method for {@link com.datascience.core.stats.Category#Category(java.lang.String)}.
	 */
	@Test
	public final void testCategory() {
		final String categoryName = "category name";
		Category category = new Category(categoryName);
		assertTrue(category.getName().equals(categoryName));
	}

	/**
	 * Test method for {@link com.datascience.core.stats.Category#setCost(java.lang.String, double)}.
	 */
	@Test
	public final void testSetCost() {
		Category category = new Category("category name");
		String ds[] = {"dest1", "dest2", "dest3"};
		double cs[] = {1234e1, -1, 234e2};
		category.setCost(ds[0], cs[0]);
		assertEquals(category.getCost(ds[0]), cs[0], TestDataManager.DELTA_DOUBLE);
		category.setCost(ds[1], cs[1]);
		assertEquals(category.getCost(ds[1]), cs[1], TestDataManager.DELTA_DOUBLE);
		category.setCost(ds[2], cs[2]);
		assertEquals(category.getCost(ds[2]), cs[2], TestDataManager.DELTA_DOUBLE);
	}

	/**
	 * Test method for {@link com.datascience.core.stats.Category#getCost(java.lang.String)}.
	 */
	@Test
	public final void testGetCost() {
		Category category = new Category("category name");
		String ds[] = {"dest1", "dest2", "dest3"};
		Double cs[] = {1234e1, -1., 234e2};
		category.setCost(ds[0], cs[0]);
		assertEquals(category.getCost(ds[0]), cs[0], TestDataManager.DELTA_DOUBLE);
		category.setCost(ds[1], cs[1]);
		assertEquals(category.getCost(ds[1]), cs[1], TestDataManager.DELTA_DOUBLE);
		category.setCost(ds[2], cs[2]);
		assertEquals(category.getCost(ds[2]), cs[2], TestDataManager.DELTA_DOUBLE);

	}

	/**
	 * Test method for {@link com.datascience.core.stats.Category#getPrior()}.
	 */
	@Test
	public final void testGetPrior() {
		Category category = new Category("category name");
		Double priors[] = {0.0, 1.0, -1.0, 1e20, -1e20};
		for (int i=0; i<priors.length; i++) {
			category.setPrior(priors[i]);
			assertEquals(category.getPrior(), priors[i], TestDataManager.DELTA_DOUBLE);
		}
	}

	/**
	 * Test method for {@link com.datascience.core.stats.Category#setPrior(double)}.
	 */
	@Test
	public final void testSetPrior() {
		Category category = new Category("category name");
		double priors[] = {0.0, 1.0, -1.0, 1e20, -1e20};
		for (int i=0; i<priors.length; i++) {
			category.setPrior(priors[i]);
			assertEquals(category.getPrior(), priors[i], TestDataManager.DELTA_DOUBLE);
		}
	}

	/**
	 * Test method for {@link com.datascience.core.stats.Category#hasPrior()}.
	 */
	@Test
	public final void testHasPrior() {
		Category categoryWithPrior = new Category("name1");
		Category categoryWithoutPrior = new Category("name2");
		categoryWithPrior.setPrior(0.1);
		assertTrue(categoryWithPrior.hasPrior());
		assertFalse(categoryWithoutPrior.hasPrior());
	}

	/**
	 * Test method for {@link com.datascience.core.stats.Category#equals(java.lang.Object)}.
	 */
	@Test
	public final void testEqualsObject_ByNameParameterOnly() {
		Category category1 = new Category("category name 1");
		Category category2 = new Category("category name 2");
		Category category3 = new Category("category name 1");
		doTestEqualsObject(category1, category2, category3, true);
	}
	/**
	 * Test method for {@link com.datascience.core.stats.Category#equals(java.lang.Object)}.
	 */
	@Test @Ignore("FIXME - this test shows why category shouldn't store cost matrix")
	public final void testEqualsObject_ByNameAndCost() {
		Category category1 = new Category("category name 1");
		Category category2 = new Category("category name 2");
		Category category3 = new Category("category name 1");

		category1.setCost("to1", 100e0);
		category2.setCost("to2", 200e0);
		category3.setCost("to3", 300e0);
		doTestEqualsObject(category1, category2, category3, false);
	}
	/**
	 * Test method for {@link com.datascience.core.stats.Category#equals(java.lang.Object)}.
	 */
	@Test
	public final void testEqualsObject_ByNameAndPrior() {
		Category category1 = new Category("category name 1");
		Category category2 = new Category("category name 2");
		Category category3 = new Category("category name 1");

		category1.setPrior(1e+1);
		category2.setPrior(1e+2);
		category3.setPrior(1e+3);
		doTestEqualsObject(category1, category2, category3, false);
	}
	/**
	 * @param first
	 * @param second
	 * @param third
	 * @param is1and3TheSame
	 */
	private final void doTestEqualsObject(Category first, Category second, Category third, boolean is1and3TheSame) {
		boolean isDifferent = !(first.equals(second) || second.equals(first));
		assertTrue(first+"!="+second,isDifferent);

		boolean isTheSame = first.equals(third) && third.equals(first);

		if (is1and3TheSame)
			assertTrue(first+"="+third,isTheSame);
		else
			assertFalse(first+"="+third,isTheSame);
		boolean isEqualNull = first.equals(null) || second.equals(null) || third.equals(null);
		assertFalse(isEqualNull);
	}

	/**
	 * Test method for {@link com.datascience.core.stats.Category#equals(java.lang.Object)}.
	 * Test method for {@link com.datascience.core.stats.Category#hashCode()}.
	 */
	@Test
	public final void testEqualsObjectWithHashCode() {
		Category category1 = new Category("name1");
		Category category2 = new Category("name1");
		Category category3 = new Category("name3");
		doTestEqualsObjectWithHashCode(category1, category2);
		doTestEqualsObjectWithHashCode(category1, null);
		doTestEqualsObjectWithHashCode(category3, category2);

		Category category1WithCost = new Category("name1");
		Category category2WithCost = new Category("name1");
		Category category3WithCost = new Category("name3");
		category1WithCost.setCost("to1", 1e1);
		category2WithCost.setCost("to2", 1e2);
		category3WithCost.setCost("to1", 1e1);
		doTestEqualsObjectWithHashCode(category1WithCost, category2WithCost);
		doTestEqualsObjectWithHashCode(category1WithCost, category3WithCost);
		doTestEqualsObjectWithHashCode(category2WithCost, null);

		doTestEqualsObjectWithHashCode(category1WithCost, category1);


		Category category1WithPrior = new Category("name1");
		Category category2WithPrior = new Category("name1");
		Category category3WithPrior = new Category("name3");
		category1WithPrior.setPrior(0.4);
		category2WithPrior.setPrior(0.2);
		category3WithPrior.setPrior(0.4);
		doTestEqualsObjectWithHashCode(category1WithPrior, category2WithPrior);
		doTestEqualsObjectWithHashCode(category1WithPrior, category3WithPrior);
		doTestEqualsObjectWithHashCode(category1WithPrior, null);
		doTestEqualsObjectWithHashCode(category1WithPrior, category1);
		doTestEqualsObjectWithHashCode(category1WithPrior, category1WithCost);
	}

	/**
	 * @param first
	 * @param secondCouldBeNull
	 */
	public final void doTestEqualsObjectWithHashCode(Category first, Category secondCouldBeNull) {
		boolean isEqual = first.equals(secondCouldBeNull);
		boolean isTheSameHashCode;
		if (secondCouldBeNull==null)
			isTheSameHashCode = false;
		else
			isTheSameHashCode = first.hashCode() == secondCouldBeNull.hashCode();
		assertTrue(first+"="+secondCouldBeNull, isEqual == isTheSameHashCode);
	}
	/**
	 * Test method for {@link com.datascience.core.stats.Category#getName()}.
	 */
	@Test
	public final void testGetName() {
		String[] names = {"name1", "name2", "", " ", null};
		Category category = new Category("name1");
		for (String name: names) {
			category.setName(name);
			assertEquals(category.getName(), name);
		}
	}

	/**
	 * Test method for {@link com.datascience.core.stats.Category#setName(java.lang.String)}.
	 */
	@Test
	public final void testSetName() {
		String[] names = {"name1", "name2", "", " ", null};
		Category category = new Category("name1");
		for (String name: names) {
			category.setName(name);
			assertEquals(category.getName(), name);
		}
	}

}
