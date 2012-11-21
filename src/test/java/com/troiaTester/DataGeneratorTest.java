package test.java.com.troiaTester;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import junit.framework.TestCase;
import org.junit.Test;
import troiaClient.GoldLabel;
import troiaClient.Label;
import main.com.troiaTester.ArtificialWorker;
import main.com.troiaTester.RouletteNoisedLabelGenerator;
import main.com.troiaTester.DataGenerator;
import main.com.troiaTester.TroiaObjectCollection;

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
	 * Compares two floating point values with specified accuracy.
	 *
	 * @param val0
	 * @param val1
	 * @param eps
	 */
	public static void assertAround(double val0, double val1, double eps) {
		assertTrue(Math.abs(val0 - val1) < eps);
	}

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
		logger.info("--------AUTOMATIC CATEGORY GENERATION--------");
		DataGenerator fixture = DataGenerator.getInstance();
		int categoryCount = 10;
		Collection<String> result = fixture
									.generateCategoryNames(categoryCount);
		logger.info(result);
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
		TroiaObjectCollection objects = generator.generateTestObjects(
											objectsCount, categoriesCount);
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
			assertAround(prior, prr, PRIOR_EPSILON);
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
		TroiaObjectCollection objects = generator.generateTestObjects(
											objectsCount, categories);
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
			assertAround(categories.get(name), prr, PRIOR_EPSILON);
		}
	}

	@Test
	public void testCreateArtificialWorker() {
		logger.info("--------SINGLE WORKER GENERATION--------");
		DataGenerator fixture = DataGenerator.getInstance();
		Collection<String> categories = fixture.generateCategoryNames(4);
		ArtificialWorker worker = fixture.generateArtificialWorker(
									  "Worker name", 0.5, categories);
		logger.info(worker);
	}

	@Test
	public void testGenerateLabels() {
		Logger.getLogger(RouletteNoisedLabelGenerator.class).setLevel(Level.INFO);
		logger.info("--------LABELS GENERATION--------");
		DataGenerator fixture = DataGenerator.getInstance();
		int categoryCount = 5;
		int objectCount = 100000;
		int workerCount = 1000;
		double minQuality = 0;
		double maxQuality = 1;
		int workersPerObject = 1;
		logger.info("Number of categories : " + categoryCount);
		logger.info("Number of objects : " + objectCount);
		logger.info("Number of workers : " + workerCount);
		logger.info("Minimal worker quality : " + minQuality);
		logger.info("Maximal worker quality : " + maxQuality);
		Collection<String> categories = fixture
										.generateCategoryNames(categoryCount);
		TroiaObjectCollection objects = fixture.generateTestObjects(objectCount,
										categories);
		Collection<ArtificialWorker> workers = fixture
											   .generateArtificialWorkers(workerCount, categories, minQuality,
													   maxQuality);
		Collection<Label> labels = fixture.generateLabels(workers, objects,
								   workersPerObject);
		int correctLabels = 0;
		int totalLabels = 0;
		double correctLabelsPercentage;
		for (Label label : labels) {
			if (label.getCategoryName().equalsIgnoreCase(
						objects.getCategory(label.getObjectName()))) {
				correctLabels++;
			}
			totalLabels++;
		}
		correctLabelsPercentage = ((double) correctLabels / (double) totalLabels) * 100;
		logger.info("Correct labels percentage : " + correctLabelsPercentage);

	}

	@Test
	public void testGenerateGoldLabels() {
		logger.info("--------GOLD LABELS GENERATION--------");
		DataGenerator fixture = DataGenerator.getInstance();
		int objectCount = 100;
		int categoryCount = 2;
		double goldCoverage = 0.5;
		Collection<String> categories = fixture
										.generateCategoryNames(categoryCount);
		TroiaObjectCollection objects = fixture.generateTestObjects(objectCount,
										categories);
		Collection<GoldLabel> goldLabels = fixture.generateGoldLabels(objects,
										   goldCoverage);
		logger.info("For " + objectCount + " objects and " + goldCoverage
					+ " gold coverage " + goldLabels.size()
					+ " gold labels were generated");

	}

	public final static double PRIOR_EPSILON = 1E-1;

	private static Logger logger = Logger
								   .getLogger(DataGeneratorTest.class);
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
