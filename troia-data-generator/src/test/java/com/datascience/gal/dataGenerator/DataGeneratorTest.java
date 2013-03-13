package com.datascience.gal.dataGenerator;

import com.datascience.core.base.AssignedLabel;
import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The class <code>TestDataGeneratorTest</code> contains tests for the class
 * {@link <code>TestDataGenerator</code>}
 *
 * @pattern JUnit Test Case
 *
 * @generatedBy CodePro at 7/17/12 1:48 PM
 *
 * @author piotr.gnys@10clouds.com
 *
 * @version $Revision$
 */
public class DataGeneratorTest extends TestCase {


	/**
	 * Construct new test instance
	 *
	 * @param name
	 *            the test name
	 */
	public DataGeneratorTest(String name) {
		super(name);
	}

	/**
	 * Run the ArrayList<String> generateCategoryNames(int) method test
	 */
	@Test
	public void testGenerateCategoryNames() {
		final int categoriesCount = 100;
		final Pattern pattern = Pattern.compile("^Category-\\d+$");
		DataGenerator generator = DataGenerator.getInstance();
		Collection<String> names =  generator.generateCategoryNames(categoriesCount);
		for (String name : names) {
			assertTrue(pattern.matcher(name).matches());

		}
	}

	/**
	 * Run the TestObjectCollection generateTestObjects(int, int) method test
	 */
	@Test
	public void testGenerateTestObjectsWithEqualPercentage() {

		int objectsCount = 10000;
		int categoriesCount = 4;
		double prior = objectsCount / (double)(categoriesCount * objectsCount);
		DataGenerator generator = DataGenerator.getInstance();
		TroiaObjectCollection objects = generator.generateTestObjects(objectsCount, categoriesCount);
		Map<String, Integer> occurrences = new HashMap<String, Integer>();
		for (String object : objects) {
			String category = objects.getCategory(object);
			// Increment the category occurrences count.
			if (occurrences.containsKey(category)) {
				occurrences.put(category, Integer.valueOf(occurrences.get(
					category).intValue() + 1));
			} else {
				occurrences.put(category, Integer.valueOf(1));
			}
		}
		Collection<String> names = occurrences.keySet();
		// Check if the prior of each category is objectsCount /
		// categoriesCount indeed.
		for (String name : names) {
			double occ = occurrences.get(name).doubleValue();
			double prr = occ / (double) objectsCount;
			assertEquals(prior, prr, PRIOR_EPSILON);
		}
	}

	@Test
	public void testGenerateTestObjectsWithUnequalPercentage() {

		final int objectsCount = 5000;
		final double prior0 = 0.6;
		final double prior1 = 0.3;
		final double prior2 = 0.1;
		DataGenerator generator = DataGenerator.getInstance();
		Map<String, Double> categories = new HashMap<String, Double>();
		categories.put("Main", prior0);
		categories.put("Minor", prior1);
		categories.put("The smallest", prior2);
		TroiaObjectCollection objects = generator.generateTestObjects(objectsCount, categories);
		Map<String, Integer> occurrences = new HashMap<String, Integer>();
		for (String object : objects) {
			String category = objects.getCategory(object);
			// Increment the category occurrences count.
			if (occurrences.containsKey(category)) {
				occurrences.put(category, Integer.valueOf(occurrences.get(
					category).intValue() + 1));
			} else {
				occurrences.put(category, Integer.valueOf(1));
			}
		}
		Collection<String> names = occurrences.keySet();
		// Check if the prior of each category is requested category prior.
		for (String name : names) {
			double occ = occurrences.get(name).doubleValue();
			double prr = occ / (double) objectsCount;
			assertEquals(categories.get(name), prr, PRIOR_EPSILON);
		}
	}

	@Test
	public void testCreateSingleArtificialWorker() {
		final String workerName = "Worker name";
		final double workerQuality = 0.5;
		DataGenerator generator = DataGenerator.getInstance();
		Collection<String> categories = generator.generateCategoryNames(4);
		ArtificialWorker worker = generator.generateArtificialWorker(
				workerName, workerQuality, categories);
		// Simple checks.
		assertTrue(worker.getName().equals(workerName));
		// Check worker's confusion matrix.
		Map<String, Map<String, Double>> matrix = worker.getConfusionMatrix().getMatrix();
		// Check if rows sum to 1.
		for (String correctCategory : matrix.keySet()) {
			Map<String, Double> vector = matrix.get(correctCategory);
			double sum = 0.0;
			for (String className : vector.keySet()) {
				sum += vector.get(className).doubleValue();
			}
			assertEquals(sum, 1.0, DataGenerator.CONFUSION_VECTOR_SUM_EPSILON);
		}
		// Check diagonal elements.
		for (String category : categories) {
			Double prob = matrix.get(category).get(category);
			assertEquals(prob, workerQuality, 1E-6);
		}
	}

	@Test
	public void testGenerateLabels() {
		final int categoriesCount = 5;
		final int objectsCount = 100000;
		final int workersCount = 1000;
		final double minQuality = 0;
		final double maxQuality = 1;
		final int workersPerObject = 1;

		DataGenerator generator = DataGenerator.getInstance();
		Collection<String> categories = generator.generateCategoryNames(categoriesCount);
		TroiaObjectCollection objects = generator.generateTestObjects(objectsCount, categories);
		Collection<ArtificialWorker> workers = generator.generateArtificialWorkers(
				workersCount, categories, minQuality, maxQuality);
		Collection<AssignedLabel<String>> labels = generator.generateLabels(workers, objects,
					workersPerObject);

		int correct = 0;
		int total = 0;
		for (AssignedLabel<String> label : labels) {
			if (label.getLabel().equals(
					objects.getCategory(label.getLobject().getName()))) {
				correct++;
			}
			total++;
		}
		double percentage = (double)correct / (double)total;
		assertEquals(percentage, 0.5, 0.1);

	}

	@Test
	public void testGenerateGoldLabels() {
		final int objectsCount = 100;
		final int categoriesCount = 2;
		final double goldCoverage = 0.526;
		DataGenerator generator = DataGenerator.getInstance();
		Collection<String> categories = generator.generateCategoryNames(categoriesCount);
		TroiaObjectCollection objects = generator.generateTestObjects(objectsCount, categories);
		Collection<CorrectLabel> goldLabels = generator.generateGoldLabels(objects, goldCoverage);
		assertEquals(goldLabels.size(), (int)(objectsCount * goldCoverage));
	}

	public final static double PRIOR_EPSILON = 1E-1;

	private static Logger logger = Logger.getLogger(DataGeneratorTest.class);
}

/*
 * $CPS$ This comment was generated by CodePro. Do not edit it. patternId =
 * com.instantiations.assist.eclipse.pattern.testCasePattern strategyId =
 * com.instantiations.assist.eclipse.pattern.testCasePattern.junitTestCase
 * additionalTestNames = assertTrue = false callTestMethod = true createMain =
 * false createSetUp = false createTearDown = false createTestFixture = false
 * createTestStubs = false methods =
 * generateCategoryNames(I),generateTestObjects
 * (I!I),generateTestObjects(I!QMap<QString;QDouble;>;) package =
 * tests.dawidSkeneTester package.sourceFolder = LabelingTester/src
 * superclassType = junit.framework.TestCase testCase = TestDataGeneratorTest
 * testClassType = main.com.dawidSkeneTester.TestDataGenerator
 */
