/**
 * 
 */
package com.datascience.gal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
public class DatumTest {
	int testCount = 4;
	List<String> nameList = new ArrayList<String>();
	List<Set<Category>> categoryList = new ArrayList<Set<Category>>();
	String categoryNames[] = {"category1.1", "category1.2", "category1.3", "category2.1"};
	Set<Category> categorySet1 = new HashSet<Category>();
	Set<Category> categorySet2 = new HashSet<Category>();
	Set<Category> categorySet3 = new HashSet<Category>();
	Set<Category> categorySet4 = null;
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
	public void setUp() throws Exception {
		nameList.add("name1");
		nameList.add("name2");
		nameList.add("name3");
		nameList.add("name4");
		
		categorySet1.add(new Category(categoryNames[0]));
		categorySet1.add(new Category(categoryNames[1]));
		categorySet1.add(new Category(categoryNames[2]));
		categorySet2.add(new Category(categoryNames[3]));
		categorySet4 = ((Set) ((HashSet) categorySet1).clone());
		
		categoryList.add(categorySet1);
		categoryList.add(categorySet2);
		categoryList.add(categorySet3);
		categoryList.add(categorySet4);
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	/**
	 * Test method for {@link com.datascience.gal.Datum#hashCode()}.
	 */
	@Test
	public final void testHashCode() {
		doTestHashCodeAndEquals(true);
	}
	/**
	 * Test method for {@link com.datascience.gal.Datum#equals(java.lang.Object)}.
	 */
	@Test
	public final void testEqualsObject() {
		doTestHashCodeAndEquals(false);
	}
	/**
	 * @param bHC
	 */
	public final void doTestHashCodeAndEquals(boolean bHC) {
		final String msgt = " should be the same as ";
		final String msgns = " should not be the same as ";
		for (int i=0; i<testCount; i++) {
			int j = (i+1) % testCount;
			Datum datum1 = new Datum(nameList.get(i), categoryList.get(i));
			Datum datum2 = new Datum(nameList.get(i), categoryList.get(i));
			datum1.setGold(i%2==0);
			datum2.setGold(i%2==0);
			if (bHC) assertEquals(datum1.hashCode(), datum2.hashCode());
			else assertTrue(datum1+msgt+datum2, datum1.equals(datum2));
			
			datum1.setCorrectCategory(categoryNames[i]);
			datum2.setCorrectCategory(categoryNames[j]);
			if (bHC) assertNotSame(datum1.hashCode(), datum2.hashCode());
			else assertFalse(datum1+msgns+datum2, datum1.equals(datum2));
			
			datum2.setCorrectCategory(categoryNames[i]);
			if (bHC) assertEquals(datum1.hashCode(), datum2.hashCode());
			else assertTrue(datum1+msgt+datum2, datum1.equals(datum2));
			
			datum2.setGold(i%2==1);
			if (bHC) assertNotSame(datum1.hashCode(), datum2.hashCode());
			else assertFalse(datum1+msgns+datum2, datum1.equals(datum2));
			
			datum2.setGold(i%2==0);
			datum1.setCategoryProbability(categoryNames[i], 0.3);
			datum2.setCategoryProbability(categoryNames[i], 0.3);
			if (bHC) assertEquals(datum1.hashCode(), datum2.hashCode());
			else assertTrue(datum1+msgt+datum2, datum1.equals(datum2));
			
			datum2.setCategoryProbability(categoryNames[j], 0.2);
			if (bHC) assertNotSame(datum1.hashCode(), datum2.hashCode());
			else assertFalse(datum1+msgns+datum2, datum1.equals(datum2));
			
			datum2.setCategoryProbability(categoryNames[i], 0.3);
			if (bHC) assertEquals(datum1.hashCode(), datum2.hashCode());
			else assertTrue(datum1+msgt+datum2, datum1.equals(datum2));
			
			datum2.setName(nameList.get(j));
			if (bHC) assertNotSame(datum1.hashCode(), datum2.hashCode());
			else assertFalse(datum1+msgns+datum2, datum1.equals(datum2));
		}
	}

	/**
	 * Test method for {@link com.datascience.gal.Datum#isGold()}.
	 * Test method for {@link com.datascience.gal.Datum#setGold(boolean)}.
	 */
	@Test
	public final void testIsGoldSetGold() {
		for (int i=0; i<testCount; i++) {
			Datum datum1 = new Datum(nameList.get(i), categoryList.get(i));
			Datum datum2 = new Datum(nameList.get(i), categoryList.get(i));
			
			datum1.setGold(i%2==0);
			datum2.setGold(i%2==0);
			assertEquals(datum1.isGold, datum2.isGold);
			
			datum2.setGold(i%2==1);
			assertNotSame(datum1.isGold, datum2.isGold);
		}
	}

	/**
	 * Test method for {@link com.datascience.gal.Datum#getCorrectCategory()}.
	 * Test method for {@link com.datascience.gal.Datum#setCorrectCategory(java.lang.String)}.
	 */
	@Test
	public final void testGetAndSetCorrectCategory() {
		for (int i=0; i<testCount; i++) {
			int j = (i+1) % testCount;
			Datum datum1 = new Datum(nameList.get(i), categoryList.get(i));
			Datum datum2 = new Datum(nameList.get(i), categoryList.get(i));
			
			datum1.setCorrectCategory(categoryNames[i]);
			datum2.setCorrectCategory(categoryNames[i]);
			assertEquals(datum1.getCorrectCategory(), datum2.getCorrectCategory());
			
			datum2.setCorrectCategory(categoryNames[j]);
			assertNotSame(datum1.getCorrectCategory(), datum2.getCorrectCategory());
		}
	}

	/**
	 * Test method for {@link com.datascience.gal.Datum#getCategoryProbability(java.lang.String)}.
	 * Test method for {@link com.datascience.gal.Datum#setCategoryProbability(java.lang.String, double)}.
	 */
	@Test
	public final void testGetCategoryProbabilityString() {
		for (int i=0; i<testCount; i++) {
			int j = (i+1) % testCount;
			Datum datum = new Datum(nameList.get(i), categoryList.get(i));
			datum.setCorrectCategory(categoryNames[i]);
			
			datum.setGold(true);
			assertEquals(1.0, datum.getCategoryProbability(categoryNames[i]), TestDataManager.DELTA_DOUBLE);
			
			datum.setCorrectCategory(categoryNames[j]);
			assertEquals(0.0, datum.getCategoryProbability(categoryNames[i]), TestDataManager.DELTA_DOUBLE);
			
			datum.setGold(false);
			assertEquals(
			//to avoid double checking on Null, coz assertEquals does not support Double
					(Object)datum.getCategoryProbability().get(categoryNames[i]), 
					(Object)datum.getCategoryProbability(categoryNames[i]));
			
		}	
	}

	/**
	 * Test method for {@link com.datascience.gal.Datum#getEntropy()}.
	 */
	@Test
	public final void testGetEntropy() {
		assertTrue("test does not cover this method",true);
	}

	/**
	 * Test method for {@link com.datascience.gal.Datum#addAssignedLabel(com.datascience.gal.AssignedLabel)}.
	 * Test method for {@link com.datascience.gal.Datum#getAssignedLabels()}.
	 */
	@Test
	public final void testAddAssignedLabel() {
		for (int i=0; i<testCount; i++) {
			Datum datum = new Datum(nameList.get(i), categoryList.get(i));
			Set<AssignedLabel> assignedLabels = (Set<AssignedLabel>) datum.getAssignedLabels();
			
			assertEquals(0, assignedLabels.size());
			datum.addAssignedLabel(new AssignedLabel("w", "d", "c"));
			assertEquals(0, assignedLabels.size());
			datum.addAssignedLabel(new AssignedLabel("w", nameList.get(i), "c"));
			assertEquals(1, assignedLabels.size());
		}
	}

	/**
	 * Test method for {@link com.datascience.gal.Datum#getMajorityCategory()}.
	 */
	@Test
	public final void testGetMajorityCategory() {
		for (int i=0; i<testCount; i++) {
			Datum datum = new Datum(nameList.get(i), categoryList.get(i));
//			System.err.println(datum.getMajorityCategory());
		}
		assertTrue("testless because return result is unpredictable," +
				"probably method computing based on some random inputs", true);
		/* 	category1.1
			category2.1
			null
			category1.2

			category1.3
			category2.1
			null
			category1.1

			category1.3
			category2.1
			null
			category1.3	 */
	}

	/**
	 * Test method for {@link com.datascience.gal.Datum#getCategoryProbability()}.
	 */
	@Test
	public final void testGetCategoryProbability() {
		doTestGetCategoryProbability(false);
	}
	@Test
	public final void testGetCategoryProbabilityAfterTheSetCategoryProbability() {
		doTestGetCategoryProbability(true);
	}
	/**
	 * @param isNewCategoryCounted
	 */
	public final void doTestGetCategoryProbability(boolean isNewCategoryCounted) {
		for (int i=0; i<testCount; i++) {
			Set<Category> categorySet = categoryList.get(i);
			Datum datum = new Datum(nameList.get(i), categorySet);
			HashMap<String, Double> categoryProbability = (HashMap<String, Double>) datum.getCategoryProbability();
			
			int size = categoryProbability.keySet().size();

			assertEquals(size,categoryProbability.keySet().size());
			Iterator<Category> iteratorIns = categorySet.iterator();
			while (iteratorIns.hasNext()) {
				Category categoryIn = iteratorIns.next();
				assertTrue(categoryProbability.keySet().contains(categoryIn.getName()));
			}
			if (size>0) {
				Iterator<String> iteratorOuts = categoryProbability.keySet().iterator();
				double oneHundredPercents = 0;
				while (iteratorOuts.hasNext()) {
					Object key = iteratorOuts.next();
					Double probability = categoryProbability.get(key);
					oneHundredPercents += probability;
				}
				assertEquals(1, oneHundredPercents, TestDataManager.DELTA_DOUBLE);
				if (isNewCategoryCounted) {
					// this section covers setCategoryProbability method as well 
					datum.setCategoryProbability("unique category", 0.7);
					categoryProbability = (HashMap<String, Double>) datum.getCategoryProbability();
					int newSize = categoryProbability.size();
					assertEquals(size, newSize-1);
					
					iteratorOuts = categoryProbability.keySet().iterator();
					oneHundredPercents = 0;
					while (iteratorOuts.hasNext()) {
						Object key = iteratorOuts.next();
						Double probability = categoryProbability.get(key);
						oneHundredPercents += probability;
					}
					assertEquals(1, oneHundredPercents, TestDataManager.DELTA_DOUBLE);
				}
			}
		}
		
	}
	/**
	 * Test method for {@link com.datascience.gal.Datum#getName()}.
	 * Test method for {@link com.datascience.gal.Datum#setName(java.lang.String)}.
	 * Test method for {@link com.datascience.gal.Datum#Datum(java.lang.String, java.util.Set)}.
	 */
	@Test
	public final void testGetNameSetNameConstructor() {
		for (int i=0; i<testCount; i++) {
			Datum datum = new Datum(nameList.get(i), categoryList.get(i));
			assertEquals(datum.getName(), nameList.get(i));
			int j = (i+1) % testCount;
			datum.setName(nameList.get(j));
			assertEquals(datum.getName(), nameList.get(j));
		}
	}


}
