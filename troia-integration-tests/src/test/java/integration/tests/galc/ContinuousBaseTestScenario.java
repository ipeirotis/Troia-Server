package test.java.integration.tests.galc;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.results.DatumContResults;
import com.datascience.core.results.WorkerContResults;
import com.datascience.galc.ContinuousIpeirotis;
import com.datascience.galc.ContinuousProject;
import com.datascience.galc.EmpiricalData;
import org.junit.Test;
import test.java.integration.helpers.FileWriters;
import test.java.integration.helpers.ObjectsResultsParser;
import test.java.integration.helpers.TestSettings;
import test.java.integration.helpers.WorkersResultsParser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ContinuousBaseTestScenario {

    public final static String DATA_BASE_DIR = TestSettings.GALC_TESTDATA_BASEDIR;
    public final static String RESULTS_BASE_DIR = TestSettings.GALC_RESULTS_BASEDIR;
    public static int MAX_ITERATIONS = 20;
    public static double EPSILON = 1e-5;
    public static double TOLERANCE = 0.0000000001;
    public static ContinuousIpeirotis algorithm;
    public static ContinuousProject project;
    public static FileWriters fileWriter;
    public static ObjectsResultsParser objectsResultsParser;
    public static WorkersResultsParser workersResultsParser;

    public static class Setup {

        public ContinuousIpeirotis algorithm;
        public String testName;

        public Setup(ContinuousIpeirotis alg, String tName) {
            algorithm = alg;
            testName = tName;
        }
    }

    public static void initSetup(Setup testSetup) {
        algorithm = testSetup.algorithm;
        project = new ContinuousProject(algorithm);

        String inputDir = DATA_BASE_DIR + testSetup.testName + TestSettings.FILEPATH_SEPARATOR + "input" + TestSettings.FILEPATH_SEPARATOR;
        String outputDir = DATA_BASE_DIR + testSetup.testName + TestSettings.FILEPATH_SEPARATOR + "output" + TestSettings.FILEPATH_SEPARATOR;

        EmpiricalData empData = new EmpiricalData();
        empData.loadLabelFile(inputDir + "assignedLabels.txt");
        empData.loadGoldLabelsFile(inputDir + "goldObjects.txt");
        empData.loadTrueObjectData(inputDir + "evaluationObjects.txt");

		project.setData(empData);
		algorithm.setData(empData);
		algorithm.setEpsilon(EPSILON);
		algorithm.setIterations(MAX_ITERATIONS);
        algorithm.compute();

        //prepare the test results file
        fileWriter = new FileWriters(RESULTS_BASE_DIR + "Results_" + testSetup.testName + ".csv");
        fileWriter.write("Metric,Original GALC value,Troia value");

        objectsResultsParser = new ObjectsResultsParser();
        objectsResultsParser.ParseResultsObjectsFile(outputDir + "results-objects.txt");

        workersResultsParser = new WorkersResultsParser();
        workersResultsParser.ParseWorkerResultsFile(outputDir + "results-workers.txt");
    }

    @Test
    public void test_Objects_AverageLabel() {
        Map<String, Map<String, Double>> expEstObjects = objectsResultsParser.getEstimatedObjectValues();
        Map<LObject<ContValue>, DatumContResults> objectsResult = algorithm.getObjectsResults();
        assertEquals(expEstObjects.size(), objectsResult.size());
        Iterator<Entry<LObject<ContValue>, DatumContResults>> entries = objectsResult.entrySet().iterator();
        while (entries.hasNext()) {
            Entry<LObject<ContValue>, DatumContResults> entry = entries.next();
            LObject<ContValue> object = entry.getKey();
            String objectName = object.getName();
            Double actualAvgLabel = algorithm.getAverageLabel(object);
            Double expectedAvgLabel = expEstObjects.get(objectName).get("avgLabel");

            fileWriter.write("AvgLabel-" + objectName + "," + expectedAvgLabel + "," + actualAvgLabel);
            assertTrue(Math.abs(expectedAvgLabel - actualAvgLabel) < TOLERANCE);
        }
    }

    @Test
    public void test_Objects_EstimatedValues() {
        Map<String, Map<String, Double>> expEstObjects = objectsResultsParser.getEstimatedObjectValues();
        Map<LObject<ContValue>, DatumContResults> objectsResult = algorithm.getObjectsResults();
        assertEquals(expEstObjects.size(), objectsResult.size());
        Iterator<Entry<LObject<ContValue>, DatumContResults>> entries = objectsResult.entrySet().iterator();
        while (entries.hasNext()) {
            Entry<LObject<ContValue>, DatumContResults> entry = entries.next();
            LObject<ContValue> object = entry.getKey();
            String objectName = object.getName();

            DatumContResults datumContResults = entry.getValue();
            Double actualEstimatedValue = datumContResults.getEst_value();
            Double actualEstimatedZeta = datumContResults.getEst_zeta();
            Double expectedEstimatedValue = expEstObjects.get(objectName).get("estValue");
            Double expectedEstimatedZeta = expEstObjects.get(objectName).get("estZeta");

            fileWriter.write("EstValue-" + objectName + "," + expectedEstimatedValue + "," + actualEstimatedValue);
            fileWriter.write("EstValue-" + objectName + "," + expectedEstimatedZeta + "," + actualEstimatedZeta);

            assertTrue(Math.abs(expectedEstimatedValue - actualEstimatedValue) < TOLERANCE);
            assertTrue(Math.abs(expectedEstimatedZeta - actualEstimatedZeta) < TOLERANCE);
        }
    }

    @Test
    public void test_Workers_Labels() {
        Map<String, HashMap<String, Object>> expWorkersResults = workersResultsParser.getWorkersResults();
        Map<Worker<ContValue>, WorkerContResults> workersResults = algorithm.getWorkersResults();
        assertEquals(expWorkersResults.size(), workersResults.size());
        Iterator<Entry<Worker<ContValue>, WorkerContResults>> entries = workersResults.entrySet().iterator();
        while (entries.hasNext()) {
            Entry<Worker<ContValue>, WorkerContResults> entry = entries.next();
            Worker<ContValue> worker = entry.getKey();
            String workerName = worker.getName();
            int expectedNoAssigns = Integer.parseInt(expWorkersResults.get(workerName).get("labels").toString());
            int actualNoAssigns = project.getData().getWorkerAssigns(worker).size();
            fileWriter.write("NoAssigns-" + workerName + "," + expectedNoAssigns + "," + actualNoAssigns);
            assertEquals(expectedNoAssigns, actualNoAssigns);
        }
    }

    @Test
    public void test_Workers_EstimatedValues() {
        Map<String, HashMap<String, Object>> expWorkersResults = workersResultsParser.getWorkersResults();
        Map<Worker<ContValue>, WorkerContResults> workersResults = algorithm.getWorkersResults();
        assertEquals(expWorkersResults.size(), workersResults.size());
        Iterator<Entry<Worker<ContValue>, WorkerContResults>> entries = workersResults.entrySet().iterator();
        while (entries.hasNext()) {
            Entry<Worker<ContValue>, WorkerContResults> entry = entries.next();
            Worker<ContValue> worker = entry.getKey();
            String workerName = worker.getName();
            WorkerContResults workerContResults = entry.getValue();
            Double expectedEstMu = (Double) expWorkersResults.get(workerName).get("est_mu");
            Double expectedEstSigma = (Double) expWorkersResults.get(workerName).get("est_sigma");
            Double expectedEstRho = (Double) expWorkersResults.get(workerName).get("est_rho");

            Double actualEstMu = workerContResults.getEst_mu();
            Double actualEstSigma = workerContResults.getEst_sigma();
            Double actualEstRho = workerContResults.getEst_rho();

            fileWriter.write("Est_Mu_" + workerName + "," + expectedEstMu + "," + actualEstMu);
            fileWriter.write("Est_Sigma_" + workerName + "," + expectedEstSigma + "," + actualEstSigma);
            fileWriter.write("Est_Rho_" + workerName + "," + expectedEstRho + "," + actualEstRho);

            assertTrue(Math.abs(expectedEstMu - actualEstMu) < TOLERANCE);
            assertTrue(Math.abs(expectedEstSigma - actualEstSigma) < TOLERANCE);
            assertTrue(Math.abs(expectedEstRho - actualEstRho) < TOLERANCE);
        }
    }
}