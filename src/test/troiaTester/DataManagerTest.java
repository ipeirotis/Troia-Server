package test.troiaTester;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.Test;

import junit.framework.TestCase;
import troiaClient.Label;
import main.com.troiaTester.ArtificialWorker;
import main.com.troiaTester.Data;
import main.com.troiaTester.DataGenerator;
import main.com.troiaTester.DataManager;
import main.com.troiaTester.TroiaObjectCollection;

/**
 * The class <code>TestDataManagerTest</code> contains tests for the class
 * {@link <code>TestDataManager</code>}
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
    }

    /**
     * Run the void saveTestObjectsToFile(String, TestObjectCollection) method
     * test
     */
    @Test
    public void testSaveTestObjectsToFile() {

        DataManager fixture = DataManager.getInstance();
        DataGenerator generator = DataGenerator.getInstance();
        String filename = TEST_OBJECTS_FILE;
        TroiaObjectCollection objects = generator.generateTestObjects(10, 2);
        try {
            fixture.saveTestObjectsToFile(filename, objects);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testLoadTestObjectsFromFile() {
        DataManager fixture = DataManager.getInstance();
        try {
            TroiaObjectCollection objects = fixture
                                            .loadTestObjectsFromFile(TEST_OBJECTS_FILE);
            for (String object : objects) {
                logger.info(object + '\t' + objects.getCategory(object));
            }
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testSaveArtificialWorkers() {
        DataManager fixture = DataManager.getInstance();
        DataGenerator generator = DataGenerator.getInstance();
        String filename = ARTIFICIAL_WORKERS_FILE;
        Collection<String> categories = generator.generateCategoryNames(3);
        Collection<ArtificialWorker> workers = generator
                                               .generateArtificialWorkers(10, categories, 0, 1);
        try {
            fixture.saveArtificialWorkers(filename, workers);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testLoadArtificialWorkers() {
        DataManager fixture = DataManager.getInstance();
        try {
            Collection<ArtificialWorker> workers = fixture
                                                   .loadArtificialWorkersFromFile(ARTIFICIAL_WORKERS_FILE);
            for (ArtificialWorker worker : workers) {
                logger.info(worker.getName());
            }
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testSaveLabelsToFile() {
        DataManager fixture = DataManager.getInstance();
        DataGenerator generator = DataGenerator.getInstance();
        String filename = LABELS_FILE;
        Collection<String> categories = generator.generateCategoryNames(3);
        TroiaObjectCollection objects = generator.generateTestObjects(10,
                                        categories);
        Collection<ArtificialWorker> workers = generator
                                               .generateArtificialWorkers(10, categories, 0, 1);
        Collection<Label> labels = generator
                                   .generateLabels(workers, objects, 2);
        try {
            fixture.saveLabelsToFile(filename, labels);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testLoadLabelsFromFile() {
        DataManager fixture = DataManager.getInstance();
        try {
            Collection<Label> labels = fixture.loadLabelsFromFile(LABELS_FILE);
            for (Label label : labels) {
                logger.info(label.getWorkerName() + '\t'
                            + label.getObjectName() + '\t'
                            + label.getCategoryName());
            }
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testSaveTestData() {
        DataManager fixture = DataManager.getInstance();
        int objectCount = 100;
        int categoryCount = 3;
        int workerCount = 7;
        double minQuality = 0;
        double maxQuality = 1;
        double goldRatio = 0.1;
        int workersPerObject = 3;
        Data data = DataGenerator.getInstance().generateTestData(
                        "Test request", objectCount, categoryCount, workerCount,
                        minQuality, maxQuality, goldRatio, workersPerObject);
        try {
            fixture.saveTestData(FILENAME_BASE, data);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testLoadTestData() {
        DataManager fixture = DataManager.getInstance();
        try {
            fixture.loadTestData(FILENAME_BASE);
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        }
    }

    private static final String TEST_OBJECTS_FILE = "testResults/testObjects.txt";
    private static final String ARTIFICIAL_WORKERS_FILE = "testResults/artificialWorkers.txt";
    private static final String LABELS_FILE = "testResults/labels.txt";
    private static final String FILENAME_BASE = "testResults/test";

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