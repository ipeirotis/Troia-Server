package test.java.com.troiaTester;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.Test;

import junit.framework.TestCase;
import troiaClient.Label;
import troiaClient.Category;
import main.com.troiaTester.ArtificialWorker;
import main.com.troiaTester.Data;
import main.com.troiaTester.DataGenerator;
import main.com.troiaTester.DataManager;
import main.com.troiaTester.TroiaObjectCollection;

import static java.io.File.separator;

/**
 * The class <code>DataManagerTest</code> contains tests for the class
 * {@link <code>DataManager</code>}
 *
 * @pattern JUnit Test Case
 *
 * @generatedBy CodePro at 7/25/12 10:47 AM
 *
 * @author piotr.gnys@10clouds.com
 *
 * @version $Revision$
 */
public class DataManagerTest extends TestCase {

	/**
	 * Construct new test instance
	 *
	 * @param name
	 *            the test name
	 */
	public DataManagerTest(String name) {
		super(name);
		// Create results directory.
		new File(RESULTS_ROOT).mkdir();
	}

	@Test
	public void testSaveLoadTestObjectsToFile() throws
		java.io.IOException, java.io.FileNotFoundException {
		// Save
		DataManager managerS = DataManager.getInstance();
		DataGenerator generator = DataGenerator.getInstance();
		TroiaObjectCollection objectsS = generator.generateTestObjects(10, 2);
		String filename = TEST_OBJECTS_FILE;
		managerS.saveTestObjectsToFile(filename, objectsS);
		// Load
		DataManager managerL = DataManager.getInstance();
		TroiaObjectCollection objectsL = managerL.loadTestObjectsFromFile(filename);
		assertTrue(objectsS.size() == objectsL.size());
		for (String key : objectsS) {
			assertTrue(objectsL.contains(key));
			assertTrue(objectsL.getCategory(key).equals(objectsS.getCategory(key)));
		}
	}

	@Test
	public void testSaveLoadArtificialWorkers() throws
		java.io.IOException, java.io.FileNotFoundException {
		// Save
		DataManager managerS = DataManager.getInstance();
		DataGenerator generator = DataGenerator.getInstance();
		String filename = ARTIFICIAL_WORKERS_FILE;
		Collection<String> categories = generator.generateCategoryNames(3);
		Collection<ArtificialWorker> workersS = generator
												.generateArtificialWorkers(10, categories, 0, 1);
		managerS.saveArtificialWorkers(filename, workersS);
		// Load
		DataManager managerL = DataManager.getInstance();
		Collection<ArtificialWorker> workersL = managerL
												.loadArtificialWorkersFromFile(filename);
		// TODO redirect output to a file, extract this collections with grep
		// and and check with diff.
		logger.debug("ArtificialWorkersSaved:  " + workersS);
		logger.debug("ArtificialWorkersLoaded: " + workersL);
		assertTrue(workersS.equals(workersL));
	}

	@Test
	public void testSaveLoadLabelsToFile() throws
		java.io.IOException, java.io.FileNotFoundException {
		DataManager managerS = DataManager.getInstance();
		DataGenerator generator = DataGenerator.getInstance();
		String filename = LABELS_FILE;
		Collection<String> categories = generator.generateCategoryNames(3);
		TroiaObjectCollection objects = generator.generateTestObjects(10,
										categories);
		Collection<ArtificialWorker> workers = generator
											   .generateArtificialWorkers(10, categories, 0, 1);
		Collection<Label> labelsS = generator
									.generateLabels(workers, objects, 2);
		managerS.saveLabelsToFile(filename, labelsS);
		DataManager managerL = DataManager.getInstance();
		Collection<Label> labelsL = managerL.loadLabelsFromFile(LABELS_FILE);
		assertTrue(labelsL.equals(labelsS));
	}

	@Test
	public void testSaveLoadTestData() throws
		java.io.IOException, java.io.FileNotFoundException {
		DataManager managerS = DataManager.getInstance();
		int objectCount = 100;
		int categoryCount = 3;
		int workerCount = 7;
		double minQuality = 0;
		double maxQuality = 1;
		double goldRatio = 0.1;
		int workersPerObject = 3;
		Data dataS = DataGenerator.getInstance().generateTestData(
						 "Test request", objectCount, categoryCount, workerCount,
						 minQuality, maxQuality, goldRatio, workersPerObject);
		managerS.saveTestData(FILENAME_BASE, dataS);
		DataManager managerL = DataManager.getInstance();
		Data dataL = managerL.loadTestData(FILENAME_BASE);

		assertTrue(dataS.getWorkers().equals(dataL.getWorkers()));
		assertTrue(dataS.getObjects().equals(dataL.getObjects()));
		assertTrue(dataS.getObjectCollection().equals(dataL.getObjectCollection()));
		assertTrue(dataS.getLabels().equals(dataL.getLabels()));
		assertTrue(dataS.getGoldLabels().equals(dataL.getGoldLabels()));
		assertTrue(dataS.getArtificialWorkers().equals(dataL.getArtificialWorkers()));
		// TODO getMisclasificationCost returns null
		// TODO getRequestId returns null
		//assertTrue(dataS.getMisclassificationCost().equals(dataL.getMisclassificationCost()));
		//assertTrue(dataS.getRequestId().equals(dataL.getRequestId()));
		assertTrue(dataS.size() == dataL.size());
		for (Category c : dataL.getCategories()) {
			assertTrue(dataS.getCategories().contains(c));
		}
	}

	private static final String RESULTS_ROOT = "target" + separator + "test-results";
	private static final String TEST_OBJECTS_FILE = RESULTS_ROOT + separator + "testObjects.txt";
	private static final String ARTIFICIAL_WORKERS_FILE = RESULTS_ROOT + separator + "artificialWorkers.txt";
	private static final String LABELS_FILE = RESULTS_ROOT + separator + "labels.txt";
	private static final String FILENAME_BASE = RESULTS_ROOT + separator + "test";

	private static Logger logger = Logger.getLogger(DataManagerTest.class);
}

/*
 * $CPS$ This comment was generated by CodePro. Do not edit it. patternId =
 * com.instantiations.assist.eclipse.pattern.testCasePattern strategyId =
 * com.instantiations.assist.eclipse.pattern.testCasePattern.junitTestCase
 * additionalTestNames = assertTrue = false callTestMethod = true createMain =
 * false createSetUp = false createTearDown = false createTestFixture = false
 * createTestStubs = false methods =
 * saveTestObjectsToFile(QString;!QTestObjectCollection;) package =
 * tests.dawidSkeneTester package.sourceFolder = DSaSJavaClient/src
 * superclassType = junit.framework.TestCase testCase = TestDataManagerTest
 * testClassType = main.com.dawidSkeneTester.TestDataManager
 */
