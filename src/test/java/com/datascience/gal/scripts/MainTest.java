/**
 *
 */
package com.datascience.gal.scripts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.datascience.gal.AssignedLabel;
import com.datascience.gal.Category;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.MisclassificationCost;
import com.datascience.utils.auxl.TestDataManager;

/**
 * @author Michael Arshynov
 *
 */
public class MainTest {

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
	 * Test method for {@link com.datascience.gal.scripts.Main#getAssignedLabels(java.lang.String[])}.
	 */
	@Test
	public final void testGetAssignedLabels() {
		String w1 = "workername1";
		String o1 = " on1 ";
		String c1 = "c1";
		String [] labels = {w1+"\t"+o1+"\t"+c1, "Label2\t on2 \tc2", "label1\t on1 \tc1"};
		Set<AssignedLabel> cSetLabel1 = Main.getAssignedLabels(labels);

		assertTrue(cSetLabel1.size()==labels.length);
		for (AssignedLabel label: cSetLabel1) {
			// order is reversive
			System.out.println(label.toString());
			if (label.getWorkerName()==w1) {
				assertTrue("["+label.getWorkerName()+","+w1+"]",label.getWorkerName().equals(w1));
				assertTrue("["+label.getObjectName()+","+o1+"]",label.getObjectName().equals(o1));
				assertTrue("["+label.getCategoryName()+","+c1+"]",label.getCategoryName().equals(c1));

			}

		}
	}
	/**
	 * Test method for {@link com.datascience.gal.scripts.Main#getAssignedLabels(java.lang.String[])}.
	 */
	@Test
	public final void testGetAssignedLabelsWithDuplications() {
		String labelForDuplication = new String("Label1\tOn\tc1");
		String [] labelsWhichAreNotUnique = {labelForDuplication, labelForDuplication};
		Set<AssignedLabel> cSetLabel2 = Main.getAssignedLabels(labelsWhichAreNotUnique);
		int sizeExpected = labelsWhichAreNotUnique.length-1;
		int sizeReturned = cSetLabel2.size();
		assertTrue("expected size="+sizeExpected+", returned size="+sizeReturned, sizeExpected == sizeReturned);
	}
	/**
	 * Test method for {@link com.datascience.gal.scripts.Main#getCategories(java.lang.String[])}.
	 */
	@Test
	public final void testGetCategories() {
		String [] names =  {"line1","line2","category"};
		Double [] priors =  {1e0, 2.5, 1e0};
		doTestGetCategories(names, priors);
	}
	/**
	 * Test method for {@link com.datascience.gal.scripts.Main#getCategories(java.lang.String[])}.
	 */
	@Test
	public final void testGetCategoriesWithDuplications() {
		String [] names = {"line1","line2","line1"};
		Double [] priors = {1e0, 2.5, null};
		doTestGetCategories(names, priors);

	}
	/**
	 * @param names
	 * @param priors
	 */
	public final void doTestGetCategories(String[] names, Double priors[]) {
		int len = names.length;
		String[] lines = new String[len];
		for(int i=0; i<len; i++) {
			lines[i] = names[i];
			if (priors[i]!=null)
				lines[i] += "\t"+priors[i];
		}
		Set<Category> categories = Main.getCategories(lines);

		assertTrue(categories.size() == len);
	}
	/**
	 * Test method for {@link com.datascience.gal.scripts.Main#getCategories(java.lang.String[])}.
	 */
	@Test
	public final void testGetCategoriesWrongPrior() {
		String CATEGORY_NAME_WITH_PRIOR = "line1";
		double CATEGORY_PRIORITY = 2.0;
		String[] lines = {CATEGORY_NAME_WITH_PRIOR+"\t"+CATEGORY_PRIORITY,
						  "line2","category3 ff\t"
						 };
		Set<Category> categories = Main.getCategories(lines);
		assertTrue(categories.size() == lines.length);
		for (Category c: categories) {
			System.out.println(c.toString());
			if (c.getName().equals(CATEGORY_NAME_WITH_PRIOR)) {
				assertEquals(c.getPrior(), CATEGORY_PRIORITY, TestDataManager.DELTA_DOUBLE);
			}
		}
	}
	/**
	 * Test method for {@link com.datascience.gal.scripts.Main#getClassificationCost(java.lang.String[])}.
	 */
	@Test
	public final void testGetClassificationCost() {
		String froms[] = {"f1", "f2", " ", "4"};
		String tos[] = {"t1", " ", "   ", "4"};
		Double costs[] = {-1e0, 0e0, -100e-1, 4e0};
		doTestGetClassificationCost(froms, tos, costs);
	}
	/**
	 * Test method for {@link com.datascience.gal.scripts.Main#getClassificationCost(java.lang.String[])}.
	 */
	@Test
	public final void testGetClassificationCostWithDuplications() {
		String froms[] = {"f1", "f2", " ", "4", "f1"};
		String tos[] = {"t1", " ", "   ", "4", "t1"};
		Double costs[] = {-1e0, 0e0, -100e-1, 4e0, 0e0};
		doTestGetClassificationCost(froms, tos, costs);
	}

	/**
	 * @param froms
	 * @param tos
	 * @param costs
	 */
	public final void doTestGetClassificationCost(String froms[], String tos[], Double costs[]) {
		int len = froms.length;
		String[] lines = new String[len];
		for (int i=0; i<len; i++) {
			lines[i] = froms[i]+"\t"+tos[i]+"\t"+costs[i];
		}
		Set<MisclassificationCost> ret = Main.getClassificationCost(lines);
		for (int i=0; i<len; i++) {
			MisclassificationCost mcc = new MisclassificationCost(
				froms[i], tos[i], costs[i]);
			assertTrue(ret.contains(mcc));
		}
		assertEquals("lines count="+len+", returned objects count="+ret.size(), len, ret.size());
	}
	/**
	 * Test method for {@link com.datascience.gal.scripts.Main#getCorrectLabels(java.lang.String[])}.
	 */
	@Test
	public final void testGetCorrectLabels() {
		String [] objectNames = {"1", "2", "on3", " "};
		String [] correctCategories = {"tc1","tc2","tc3"," "};
		String[] lines = new String[objectNames.length];
		for (int i=0; i<lines.length; i++) {
			lines[i] = objectNames[i]+"\t"+correctCategories[i];
		}
		Set<CorrectLabel> correctLabels = Main.getCorrectLabels(lines);
		for (int i=0; i<lines.length; i++) {
			CorrectLabel cl = new CorrectLabel(objectNames[i], correctCategories[i]);
			assertTrue("contains", correctLabels.contains(cl));
		}
		assertTrue("lines count="+objectNames.length+", labels count="+correctLabels.size(),
				   lines.length == correctLabels.size());
	}
	/**
	 * Test method for {@link com.datascience.gal.scripts.Main#getCorrectLabels(java.lang.String[])}.
	 */
	@Test
	public final void testGetCorrectLabelsWithDuplications() {
		String [] objectNames = {"1", "2", "on3", " ", "2"};
		String [] correctCategories = {"tc1","tc2","tc3"," ", "tc2"};
		String[] lines = new String[objectNames.length];
		for (int i=0; i<lines.length; i++) {
			lines[i] = objectNames[i]+"\t"+correctCategories[i];
		}
		Set<CorrectLabel> correctLabels = Main.getCorrectLabels(lines);
		for (int i=0; i<lines.length; i++) {
			CorrectLabel cl = new CorrectLabel(objectNames[i], correctCategories[i]);
			assertTrue("contains", correctLabels.contains(cl));
		}
		assertTrue("lines count="+objectNames.length+", labels count="+correctLabels.size(),
				   lines.length == correctLabels.size());
	}
}
