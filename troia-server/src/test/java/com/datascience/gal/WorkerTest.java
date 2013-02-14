/**
 *
 */
package com.datascience.gal;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.datascience.core.storages.JSONUtils;
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
public class WorkerTest {

	Category category1 = new Category("category1");
	Category category2 = new Category("category2");
	Category category3 = new Category("category3");
	Category category4 = new Category("category4");
	Category category5 = new Category("category5");

	List<Category> categoryList1 = new ArrayList<Category>();
	List<Category> categoryList2 = new ArrayList<Category>();
	List<Category> categoryList3 = new ArrayList<Category>();

	HashSet<Category> categorySet1;
	HashSet<Category> categorySet2;
	HashSet<Category> categorySet3;

	List<HashSet<Category>> categoriesList = new ArrayList<HashSet<Category>>();

	/**
	 *
	 */
	public WorkerTest() {
		categoryList1.add(category1);
		categoryList1.add(category2);

		categoryList2.add(category3);
		categoryList2.add(category4);
		categoryList2.add(category5);

		categoryList3.add(category1);
		categoryList3.add(category2);

		categorySet1 = new HashSet<Category>(categoryList1);
		categorySet2 = new HashSet<Category>(categoryList2);
		categorySet3 = new HashSet<Category>(categoryList3);

		categoriesList = new ArrayList<HashSet<Category>>();
		categoriesList.add(categorySet1);
		categoriesList.add(categorySet2);
		categoriesList.add(categorySet3);
	}
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
	 * Test method for {@link com.datascience.gal.Worker#hashCode()}.
	 */
	@Test
	public final void testHashCodeName() {
		doTestEqualsBySetField(EFieldsEquals.NAME, true);
	}
	/**
	 * Test method for {@link com.datascience.gal.Worker#hashCode()}.
	 */
	@Test
	public final void testHashCodeCategories() {
		doTestEqualsBySetField(EFieldsEquals.CATEGORIES, true);
	}
	/**
	 * Test method for {@link com.datascience.gal.Worker#hashCode()}.
	 */
	@Test
	public final void testHashCodeAssignedLabels() {
		doTestEqualsBySetField(EFieldsEquals.ASSIGNEDLABELS, true);
	}
	/**
	 * Test method for {@link com.datascience.gal.Worker#hashCode()}.
	 */
	@Test
	public final void testHashCodeErrors() {
		doTestEqualsBySetField(EFieldsEquals.ERRORS, true);
	}
	/**
	 * Test method for {@link com.datascience.gal.Worker#getAssignedLabels()}.
	 * Test method for {@link com.datascience.gal.Worker#addAssignedLabel(com.datascience.gal.AssignedLabel)}.
	 */
	@Test
	public final void testGetAssignedLabels() {
		String[] names= {"worker1", "", "  "};
		for (int i=0; i<names.length; i++) {
			Worker worker = new Worker(names[i], categoriesList.get(i));
			Set<AssignedLabel>  assignedLabels = worker.getAssignedLabels();

			assertEquals(0, assignedLabels.size());

			Category category = (Category) categoriesList.get(i).toArray()[0];
			AssignedLabel assignedLabel1 =
				new AssignedLabel(names[i], "datum1",category.getName());
			AssignedLabel assignedLabel2 =
				new AssignedLabel(names[i], "datum2",category.getName());

			worker.addAssignedLabel(assignedLabel1);
			worker.addAssignedLabel(assignedLabel2);

			assignedLabels = worker.getAssignedLabels();
			assertEquals(2, assignedLabels.size());
		}
	}

	/**
	 * Test method for {@link com.datascience.gal.Worker#empty()}.
	 */
	@Test
	public final void testEmpty() {
		String[] names = {"worker1", "", null};
		String matrixIsEmpty = "\"matrix\":[]";
		String rowDenominatorIsEmpty = "\"rowDenominator\":[]";
		for (int i=0; i<names.length; i++) {
			Worker worker = new Worker(names[i], categoriesList.get(i));
			String cms = JSONUtils.getOldGson().toJson(worker.cm);
			assertFalse(cms.contains(matrixIsEmpty));
			assertFalse(cms.contains(rowDenominatorIsEmpty));
			worker.empty();
			cms = JSONUtils.getOldGson().toJson(worker.cm);
			assertTrue(cms.contains(matrixIsEmpty));
			assertTrue(cms.contains(rowDenominatorIsEmpty));
		}
	}

	/**
	 * Test method for {@link com.datascience.gal.Worker#getPrior(java.util.Map)}.
	 */
	@Test
	public final void testGetPrior() {
		String[] names = {"worker1", "", null};
		double[] priors = {0.5, 1./3., 0.5};
		
		for (int i=0; i<names.length; i++) {
			Worker worker = new Worker(names[i], categoriesList.get(i));
			LinkedList<String> categoryNames = new LinkedList<String>();
			for (Category category: categoriesList.get(i)) {
				categoryNames.add(category.getName());
			}
			Map<String, Double> testedMap = worker.getPrior(categoryNames);
			for (Double priority: testedMap.values()) {  
				assertEquals(new Double(priors[i]), priority, TestDataManager.DELTA_DOUBLE);
			}
		}
	}

	/**
	 * Test method for {@link com.datascience.gal.Worker#removeError(java.lang.String, java.lang.String, double)}.
	 */
	@Test
	public final void testRemoveError() {
		String[] names = {"worker1", "", null};
		double errorAdditionValue = 0.15;
		for (int i=0; i<names.length; i++) {
			Worker worker = new Worker(names[i], categoriesList.get(i));
			Category categoryFrom = (Category) categoriesList.get(i).toArray()[0];
			Category categoryTo = (Category) categoriesList.get(i).toArray()[1];
			String from = categoryFrom.getName();
			String to = categoryTo.getName();

			double 	testedValueBefore = worker.getErrorRateBatch(from, to);
//			System.err.println("Before{"+i+"},"+testedValueBefore);
			worker.addError(from, to, errorAdditionValue);

			double testedValueAfter = worker.getErrorRateBatch(from, to);
//			System.err.println("After{"+i+"},"+testedValueAfter);
			assertEquals(errorAdditionValue+testedValueBefore, testedValueAfter, TestDataManager.DELTA_DOUBLE);
		}
	}

	/**
	 * Test method for {@link com.datascience.gal.Worker#normalize(com.datascience.gal.ConfusionMatrixNormalizationType)}.
	 */
	@Test
	public final void testNormalize() {
		String[] names = {"worker1", "", null};
		for (int i=0; i<names.length; i++) {
			Worker workerForLaplace = new Worker(names[i], categoriesList.get(i));
			Worker workerForUniform = new Worker(names[i], categoriesList.get(i));
			Set<Category> categorySet = categoriesList.get(i);

			Category categoryFrom = new Category(((Category)categorySet.toArray()[0]).getName());
			Category categoryTo = new Category(((Category)categorySet.toArray()[1]).getName());
			String from = categoryFrom.getName();
			String to = categoryTo.getName();

			int categoriesCount = categoriesList.get(i).size();
			double[] toCompareTheoreticallyComputed = new double[8];
			toCompareTheoreticallyComputed[0] =
				laplaceNormalization(workerForLaplace.getErrorRateBatch(from, from), categoriesCount);
			toCompareTheoreticallyComputed[1] =
				laplaceNormalization(workerForLaplace.getErrorRateBatch(from, to), categoriesCount);
			toCompareTheoreticallyComputed[2] =
				laplaceNormalization(workerForLaplace.getErrorRateBatch(to, from), categoriesCount);
			toCompareTheoreticallyComputed[3] =
				laplaceNormalization(workerForLaplace.getErrorRateBatch(to, to), categoriesCount);

			toCompareTheoreticallyComputed[4] = workerForUniform.getErrorRateBatch(from, from);
			toCompareTheoreticallyComputed[5] = workerForUniform.getErrorRateBatch(from, to);
			toCompareTheoreticallyComputed[6] = workerForUniform.getErrorRateBatch(to, from);
			toCompareTheoreticallyComputed[7] = workerForUniform.getErrorRateBatch(to, to);



			workerForLaplace.normalize(ConfusionMatrixNormalizationType.LAPLACE);
			workerForUniform.normalize(ConfusionMatrixNormalizationType.UNIFORM);

			double[] toCompareNormilized = new double[8];
			toCompareNormilized[0] = workerForLaplace.getErrorRateBatch(from, from);
			toCompareNormilized[1] = workerForLaplace.getErrorRateBatch(from, to);
			toCompareNormilized[2] = workerForLaplace.getErrorRateBatch(to, from);
			toCompareNormilized[3] = workerForLaplace.getErrorRateBatch(to, to);

			toCompareNormilized[4] = workerForUniform.getErrorRateBatch(from, from);
			toCompareNormilized[5] = workerForUniform.getErrorRateBatch(from, to);
			toCompareNormilized[6] = workerForUniform.getErrorRateBatch(to, from);
			toCompareNormilized[7] = workerForUniform.getErrorRateBatch(to, to);

			assertArrayEquals(toCompareTheoreticallyComputed, toCompareNormilized, TestDataManager.DELTA_DOUBLE);
		}
	}

	/**
	 * @param probability
	 * @param n
	 * @return
	 */
	private double laplaceNormalization(double probability, int n) {
		return (1+probability)/(n+1);
	}
	/**
	 * Test method for {@link com.datascience.gal.Worker#getErrorRateIncremental(java.lang.String, java.lang.String, com.datascience.gal.ConfusionMatrixNormalizationType)}.
	 */
	@Test
	public final void testGetErrorRateIncremental() {
		String[] names = {"worker1", "", null};
		for (int i=0; i<names.length; i++) {
			Worker workerLaplace = new Worker(names[i], categoriesList.get(i));
			Worker workerUniform = new Worker(names[i], categoriesList.get(i));
			Set<Category> categorySet = categoriesList.get(i);
			Category categoryFrom = new Category(((Category)categorySet.toArray()[0]).getName());
			Category categoryTo = new Category(((Category)categorySet.toArray()[1]).getName());
			String from = categoryFrom.getName();
			String to = categoryTo.getName();
			int categoriesCount = categoriesList.get(i).size();
			double plainProbability = workerLaplace.getErrorRateBatch(from, to);
			double expectedProbability = laplaceNormalization(plainProbability, categoriesCount);
			double actualProbability = workerLaplace.getErrorRateIncremental(from, to, ConfusionMatrixNormalizationType.LAPLACE);
			assertEquals(expectedProbability, actualProbability, TestDataManager.DELTA_DOUBLE);

			plainProbability = workerUniform.getErrorRateBatch(from, to);
			actualProbability = workerUniform.getErrorRateIncremental(from, to, ConfusionMatrixNormalizationType.UNIFORM);
			assertEquals(plainProbability, actualProbability, TestDataManager.DELTA_DOUBLE);
		}
	}

	/**
	 * Test method for {@link com.datascience.gal.Worker#getErrorRateBatch(java.lang.String, java.lang.String)}.
	 * Test method for {@link com.datascience.gal.Worker#addError(java.lang.String, java.lang.String, double)}.
	 */
	@Test
	public final void testGetErrorRateBatch() {
		String[] names = {"worker1", "", null};
		double[] errors = {0.11, 0.22, 0.33};
		for (int i=0; i<names.length; i++) {
			Worker worker = new Worker(names[i], categoriesList.get(i));
			String categoryFrom = categoriesList.get(i).toArray()[0].toString();
			String categoryTo = categoriesList.get(i).toArray()[1].toString();

			double errorRateBatch = worker.getErrorRateBatch(categoryFrom, categoryTo);
			assertEquals(0.0, errorRateBatch, TestDataManager.DELTA_DOUBLE);

			worker.addError(categoryFrom, categoryTo, errors[i]);
			errorRateBatch = worker.getErrorRateBatch(categoryFrom, categoryTo);
			assertEquals(errors[i], errorRateBatch, TestDataManager.DELTA_DOUBLE);
		}
	}

	/**
	 * Test method for {@link com.datascience.gal.Worker#getName()}.
	 */
	@Test
	public final void testGetName() {
		String[] names = {"worker1", "", null};
		List<HashSet<Category>> categoriesList = new ArrayList<HashSet<Category>>();
		categoriesList.add(categorySet1);
		categoriesList.add(categorySet2);
		categoriesList.add(categorySet3);
		for (int i=0; i<names.length; i++) {
			Worker worker = new Worker(names[i], categoriesList.get(i));
			assertEquals(names[i], worker.getName());
		}
	}

	/**
	 * Test method for {@link com.datascience.gal.Worker#equals(java.lang.Object)}.
	 */
	@Test
	public final void testEqualsObjectName() {
		doTestEqualsBySetField(EFieldsEquals.NAME, false);
	}
	/**
	 * Test method for {@link com.datascience.gal.Worker#equals(java.lang.Object)}.
	 */
	@Test
	public final void testEqualsObjectCategories() {
		doTestEqualsBySetField(EFieldsEquals.CATEGORIES, false);
	}
	/**
	 * Test method for {@link com.datascience.gal.Worker#equals(java.lang.Object)}.
	 */
	@Test
	public final void testEqualsObjectAssignedLabels() {
		doTestEqualsBySetField(EFieldsEquals.ASSIGNEDLABELS, false);
	}
	/**
	 * Test method for {@link com.datascience.gal.Worker#equals(java.lang.Object)}.
	 */
	@Test
	public final void testEqualsObjectErrors() {
		doTestEqualsBySetField(EFieldsEquals.ERRORS, false);
	}
	/**
	 * @param eFieldsEquals
	 */
	public final void doTestEqualsBySetField(EFieldsEquals eFieldsEquals, boolean isHashCodeChecking) {
		if (eFieldsEquals == EFieldsEquals.NAME) {
			Worker worker1 = new Worker("worker1", categorySet1);
			Worker worker2 = new Worker("worker2", categorySet1);
			Worker worker3 = new Worker("worker1", categorySet1);
			if (isHashCodeChecking) {
				assertEquals(worker1.hashCode(), worker3.hashCode());
				assertNotSame(worker1.hashCode(), worker2.hashCode());
				assertNotSame(worker2.hashCode(), worker3.hashCode());
			} else {
				assertTrue(worker1.equals(worker3));
				assertFalse(worker1.equals(worker2));
				assertFalse(worker2.equals(worker3));
			}
		}
		if (eFieldsEquals == EFieldsEquals.CATEGORIES) {
			Worker worker1 = new Worker("worker1", categorySet1);
			Worker worker2 = new Worker("worker2", categorySet2);
			Worker worker3 = new Worker("worker1", categorySet3);
			if (isHashCodeChecking) {
				assertEquals(worker1.hashCode(), worker3.hashCode());
				assertNotSame(worker1.hashCode(), worker2.hashCode());
				assertNotSame(worker2.hashCode(), worker3.hashCode());
			} else {
				assertTrue(worker1.equals(worker3));
				assertFalse(worker1.equals(worker2));
				assertFalse(worker2.equals(worker3));
			}
		}
		if (eFieldsEquals == EFieldsEquals.ASSIGNEDLABELS) {
			Worker worker1 = new Worker("worker1", categorySet1);
			Worker worker2 = new Worker("worker1", categorySet1);
			if (isHashCodeChecking) {
				assertEquals(worker1.hashCode(), worker2.hashCode());
			} else {
				assertTrue(worker1.equals(worker2));
			}
			AssignedLabel assignedLabel = new AssignedLabel("worker1", "datum1", "category1");
			worker1.addAssignedLabel(assignedLabel);
			if (isHashCodeChecking) {
				assertNotSame(worker1+"\n should not be the same as \n"+worker2, worker1.hashCode(), worker2.hashCode());
			} else {
				assertFalse(worker1+"\n should not be the same as \n"+worker2, worker1.equals(worker2));
			}
		}
		if (eFieldsEquals == EFieldsEquals.ERRORS) {
			Worker worker1 = new Worker("worker1", categorySet1);
			Worker worker2 = new Worker("worker1", categorySet1);
			if (isHashCodeChecking) {
				assertEquals(worker1+"\n should not be the same as \n"+worker2, worker1.hashCode(), worker2.hashCode());
			} else {
				assertTrue(worker1+"\n should not be the same as \n"+worker2, worker1.equals(worker2));
			}
			worker1.addError("source", "destination", 0.4);
			if (isHashCodeChecking) {
				assertNotSame(worker1+"\n should not be the same as \n"+worker2, worker1.hashCode(), worker2.hashCode());
			} else {
				assertFalse(worker1+"\n should not be the same as \n"+worker2, worker1.equals(worker2));
			}
		}
	}

	/**
	 * @author Michael Arshynov
	 *
	 */
	enum EFieldsEquals {
		NAME, CATEGORIES, ASSIGNEDLABELS, ERRORS
	}

}
