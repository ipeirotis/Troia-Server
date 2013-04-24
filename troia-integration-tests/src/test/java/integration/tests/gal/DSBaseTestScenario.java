package test.java.integration.tests.gal;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.decision.ILabelProbabilityDistributionCostCalculator;
import com.datascience.core.nominal.decision.LabelProbabilityDistributionCostCalculators;
import com.datascience.gal.BatchDawidSkene;
import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.IncrementalDawidSkene;
import com.datascience.utils.ProbabilityDistributions;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DSBaseTestScenario extends BaseTestScenario {

    public final static int NO_ITERATIONS = 10;
    public final static double EPSILON = 1e-6;

    public static class Setup {

        public String algorithm;
        public String testName;
        public boolean loadEvaluationLabels;

        public Setup(String alg, String tName, boolean lEvaluationLabels) {
            algorithm = alg;
            testName = tName;
            loadEvaluationLabels = lEvaluationLabels;
        }
    }

    public static void initSetup(Setup testSetup) {
        AbstractDawidSkene algorithm = null;
        if (testSetup.algorithm.equals("BDS")) {
            algorithm = new BatchDawidSkene();
        } else if (testSetup.algorithm.equals("IDS")) {
            algorithm = new IncrementalDawidSkene();
        } else {
            System.err.println("Incorrect algorithm");
        }
        algorithm.setEpsilon(EPSILON);
        algorithm.setIterations(NO_ITERATIONS);
        setUp(algorithm, testSetup.testName, testSetup.loadEvaluationLabels);
    }

    @Test
    public void test_Data() {
        HashMap<String, String> data = summaryResultsParser.getData();

        int expectedCategoriesNo = Integer.parseInt(data.get("Categories"));
        int actualCategoriesNo = this.data.getCategories().size();

        assertEquals(expectedCategoriesNo, actualCategoriesNo);
        fileWriter.write("Categories," + expectedCategoriesNo + "," + actualCategoriesNo);

        int expectedObjectsNo = Integer.parseInt(data.get("Objects in Data Set"));
        int actualObjectsNo = this.data.getObjects().size();
        assertEquals(expectedObjectsNo, actualObjectsNo);
        fileWriter.write("Objects in Data Set," + expectedObjectsNo + "," + actualObjectsNo);

        int expectedWorkersNo = Integer.parseInt(data.get("Workers in Data Set"));
        int actualWorkersNo = this.data.getWorkers().size();
        assertEquals(expectedWorkersNo, actualWorkersNo);
        fileWriter.write("Workers in Data Set," + expectedWorkersNo + "," + actualWorkersNo);

        //get the labels
        int noAssignedLabels = 0;
        Set<LObject<String>> objects = this.data.getObjects();
        for (LObject<String> object : objects) {
            noAssignedLabels += this.data.getAssignsForObject(object).size();
        }

        int expectedLabelsNo = Integer.parseInt(data.get("Labels Assigned by Workers"));
        assertEquals(expectedLabelsNo, noAssignedLabels);
        fileWriter.write("Labels Assigned by Workers," + expectedLabelsNo + "," + noAssignedLabels);
    }

    @Test
    public void test_ProbabilityDistributions_DS() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();
        Set<LObject<String>> objects = data.getObjects();

        //init the categoryProbabilities hashmap
        HashMap<String, Double> categoryProbabilities = new HashMap<String, Double>();
        for (String categoryName : data.getCategories()) {
            categoryProbabilities.put(categoryName, 0.0);
        }

        //iterate through the datum objects and calculate the sum of the probabilities associated  to each category
        int noObjects = objects.size();
        for (LObject<String> object : objects) {
            Map<String, Double> objectProbabilities = project.getObjectResults(object).getCategoryProbabilites();
            for (String categoryName : objectProbabilities.keySet()) {
                categoryProbabilities.put(categoryName, categoryProbabilities.get(categoryName) + objectProbabilities.get(categoryName));
            }
        }

        //calculate the average probability value for each category
        for (String categoryName : data.getCategories()) {
            categoryProbabilities.put(categoryName, categoryProbabilities.get(categoryName) / noObjects);
        }
        for (String categoryName : data.getCategories()) {
            String metricName = "[DS_Pr[" + categoryName + "]] DS estimate for prior probability of category " + categoryName;
            String expectedCategoryProbability = dataQuality.get(metricName);
            String actualCategoryProbability = testHelper.format(categoryProbabilities.get(categoryName));
            fileWriter.write("[DS_Pr[" + categoryName + "]]," + expectedCategoryProbability + "," + actualCategoryProbability);
            //TODO: enable the assert
            //assertEquals(expectedCategoryProbability, actualCategoryProbability);
        }
    }

    @Test
    public void test_DataCost_Estm_NoVote_Exp() {
        //TODO this test does not make sense with the current form
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();
        double avgClassificationCost = 0.0;
        Map<String, Double> temp = ProbabilityDistributions.getPriorBasedDistribution(data, project.getAlgorithm());
        for (Double val : temp.values()) {
            avgClassificationCost += val;
        }
        avgClassificationCost /= temp.size();
        String expectedClassificationCost = dataQuality.get("[DataCost_Estm_NoVote_Exp] Baseline classification cost (random spammer)");
        String actualClassificationCost = testHelper.format(avgClassificationCost);
        fileWriter.write("DataCost_Estm_NoVote_Exp," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Estm_NoVote_Min() {
        //TODO this tests does not make sense with the current form
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();
        double avgClassificationCost = 0.0;
        Map<String, Double> temp = ProbabilityDistributions.getPriorBasedDistribution(data, project.getAlgorithm());
        for (Double val : temp.values()) {
            avgClassificationCost += val;
        }
        avgClassificationCost /= temp.size();
        String expectedClassificationCost = dataQuality.get("[DataCost_Estm_NoVote_Min] Baseline classification cost (strategic spammer)");
        String actualClassificationCost = testHelper.format(avgClassificationCost);
        fileWriter.write("DataCost_Estm_NoVote_Min," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Estm_DS_Exp() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("EXPECTEDCOST");

        double avgClassificationCost = estimateMissclassificationCost(labelProbabilityDistributionCostCalculator, null);

        String expectedClassificationCost = dataQuality.get("[DataCost_Estm_DS_Exp] Estimated classification cost (DS_Exp metric)");
        String actualClassificationCost = testHelper.format(avgClassificationCost);
        fileWriter.write("DataCost_Estm_DS_Exp," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Estm_DS_ML() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("MAXLIKELIHOOD");

        double avgClassificationCost = estimateMissclassificationCost(labelProbabilityDistributionCostCalculator, null);

        String expectedClassificationCost = dataQuality.get("[DataCost_Estm_DS_ML] Estimated classification cost (DS_ML metric)");
        String actualClassificationCost = testHelper.format(avgClassificationCost);
        fileWriter.write("DataCost_Estm_DS_ML," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);

    }

    @Test
    public void test_DataCost_Estm_DS_Min() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("MINCOST");

        double avgClassificationCost = estimateMissclassificationCost(labelProbabilityDistributionCostCalculator, null);

        String expectedClassificationCost = dataQuality.get("[DataCost_Estm_DS_Min] Estimated classification cost (DS_Min metric)");
        String actualClassificationCost = testHelper.format(avgClassificationCost);
        fileWriter.write("DataCost_Estm_DS_Min," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Eval_DS_ML() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();

        double avgClassificationCost = evaluateMissclassificationCost("MAXLIKELIHOOD");

        String expectedClassificationCost = dataQuality.get("[DataCost_Eval_DS_ML] Actual classification cost for EM, maximum likelihood classification");
        String actualClassificationCost = testHelper.format(avgClassificationCost);
        fileWriter.write("DataCost_Eval_DS_ML," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Eval_DS_Min() {
        HashMap<String, String> data = summaryResultsParser.getDataQuality();

        double avgClassificationCost = evaluateMissclassificationCost("MINCOST");

        String expectedClassificationCost = data.get("[DataCost_Eval_DS_Min] Actual classification cost for EM, min-cost classification");
        String actualClassificationCost = testHelper.format(avgClassificationCost);
        fileWriter.write("DataCost_Eval_DS_Min," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Eval_DS_Soft() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();

        double avgClassificationCost = evaluateMissclassificationCost("SOFT");

        String expectedClassificationCost = dataQuality.get("[DataCost_Eval_DS_Soft] Actual classification cost for EM, soft-label classification");
        String actualClassificationCost = testHelper.format(avgClassificationCost);
        fileWriter.write("DataCost_Eval_DS_Soft," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataQuality_Estm_DS_ML() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("MAXLIKELIHOOD");

        double avgQuality = estimateCostToQuality(labelProbabilityDistributionCostCalculator, null);

        String expectedClassificationCost = dataQuality.get("[DataQuality_Estm_DS_ML] Estimated data quality, EM algorithm, maximum likelihood");
        String actualClassificationCost = testHelper.formatPercent(avgQuality);
        fileWriter.write("DataQuality_Estm_DS_ML," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataQuality_Estm_DS_Exp() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("EXPECTEDCOST");

        double avgQuality = estimateCostToQuality(labelProbabilityDistributionCostCalculator, null);

        String expectedClassificationCost = dataQuality.get("[DataQuality_Estm_DS_Exp] Estimated data quality, EM algorithm, soft label");
        String actualClassificationCost = testHelper.formatPercent(avgQuality);
        fileWriter.write("DataQuality_Estm_DS_Exp," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataQuality_Estm_DS_Min() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("MINCOST");

        double avgQuality = estimateCostToQuality(labelProbabilityDistributionCostCalculator, null);

        String expectedClassificationCost = dataQuality.get("[DataQuality_Estm_DS_Min] Estimated data quality, EM algorithm, mincost");
        String actualClassificationCost = testHelper.formatPercent(avgQuality);
        fileWriter.write("DataQuality_Estm_DS_Min," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataQuality_Eval_DS_ML() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();

        double avgQuality = evaluateCostToQuality("MAXLIKELIHOOD");

        String expectedClassificationCost = dataQuality.get("[DataQuality_Eval_DS_ML] Actual data quality, EM algorithm, maximum likelihood");
        String actualClassificationCost = testHelper.formatPercent(avgQuality);
        fileWriter.write("DataQuality_Eval_DS_ML," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataQuality_Eval_DS_Min() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();

        double avgQuality = evaluateCostToQuality("MINCOST");

        String expectedClassificationCost = dataQuality.get("[DataQuality_Eval_DS_Min] Actual data quality, EM algorithm, mincost");
        String actualClassificationCost = testHelper.formatPercent(avgQuality);
        fileWriter.write("DataQuality_Eval_DS_Min," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataQuality_Eval_DS_Soft() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();
        double avgQuality = evaluateCostToQuality("SOFT");

        String expectedClassificationCost = dataQuality.get("[DataQuality_Eval_DS_Soft] Actual data quality, EM algorithm, soft label");
        String actualClassificationCost = testHelper.formatPercent(avgQuality);
        fileWriter.write("DataQuality_Eval_DS_Soft," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_WorkerQuality_Estm_DS_Exp_n() {
        HashMap<String, String> workerQuality = summaryResultsParser.getWorkerQuality();
        double avgQuality = estimateWorkerQuality("EXPECTEDCOST", "n");

        String expectedQuality = workerQuality.get("[WorkerQuality_Estm_DS_Exp_n] Estimated worker quality (non-weighted, DS_Exp metric)");
        String actualQuality = testHelper.formatPercent(avgQuality);
        fileWriter.write("WorkerQuality_Estm_DS_Exp_n," + expectedQuality + "," + actualQuality);
        assertEquals(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Estm_DS_Exp_w() {
        HashMap<String, String> workerQuality = summaryResultsParser.getWorkerQuality();
        double avgQuality = estimateWorkerQuality("EXPECTEDCOST", "w");

        String expectedQuality = workerQuality.get("[WorkerQuality_Estm_DS_Exp_w] Estimated worker quality (weighted, DS_Exp metric)");
        String actualQuality = testHelper.formatPercent(avgQuality);
        fileWriter.write("WorkerQuality_Estm_DS_Exp_w," + expectedQuality + "," + actualQuality);
        assertEquals(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Estm_DS_ML_n() {
        HashMap<String, String> workerQuality = summaryResultsParser.getWorkerQuality();
        double avgQuality = estimateWorkerQuality("MAXLIKELIHOOD", "n");

        String expectedQuality = workerQuality.get("[WorkerQuality_Estm_DS_ML_n] Estimated worker quality (non-weighted, DS_ML metric)");
        String actualQuality = testHelper.formatPercent(avgQuality);
        fileWriter.write("WorkerQuality_Estm_DS_ML_n," + expectedQuality + "," + actualQuality);
        assertEquals(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Estm_DS_ML_w() {
        HashMap<String, String> workerQuality = summaryResultsParser.getWorkerQuality();
        double avgQuality = estimateWorkerQuality("MAXLIKELIHOOD", "w");

        String expectedQuality = workerQuality.get("[WorkerQuality_Estm_DS_ML_w] Estimated worker quality (weighted, DS_ML metric)");
        String actualQuality = testHelper.formatPercent(avgQuality);
        fileWriter.write("WorkerQuality_Estm_DS_ML_w," + expectedQuality + "," + actualQuality);
        assertEquals(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Estm_DS_Min_n() {
        HashMap<String, String> workerQuality = summaryResultsParser.getWorkerQuality();
        double avgQuality = estimateWorkerQuality("MINCOST", "n");

        String expectedQuality = workerQuality.get("[WorkerQuality_Estm_DS_Min_n] Estimated worker quality (non-weighted, DS_Min metric)");
        String actualQuality = testHelper.formatPercent(avgQuality);
        fileWriter.write("WorkerQuality_Estm_DS_Min_n," + expectedQuality + "," + actualQuality);
        assertEquals(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Estm_DS_Min_w() {
        HashMap<String, String> workerQuality = summaryResultsParser.getWorkerQuality();
        double avgQuality = estimateWorkerQuality("MINCOST", "w");

        String expectedQuality = workerQuality.get("[WorkerQuality_Estm_DS_Min_w] Estimated worker quality (weighted, DS_Min metric)");
        String actualQuality = testHelper.formatPercent(avgQuality);
        fileWriter.write("WorkerQuality_Estm_DS_Min_w," + expectedQuality + "," + actualQuality);
        assertEquals(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Eval_DS_Exp_n() {
        HashMap<String, String> workerQuality = summaryResultsParser.getWorkerQuality();
        double avgQuality = evaluateWorkerQuality("EXPECTEDCOST", "n");

        String expectedQuality = workerQuality.get("[WorkerQuality_Eval_DS_Exp_n] Actual worker quality (non-weighted, DS_Exp metric)");
        String actualQuality = testHelper.formatPercent(avgQuality);
        fileWriter.write("WorkerQuality_Eval_DS_Exp_n," + expectedQuality + "," + actualQuality);
        assertEquals(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Eval_DS_Exp_w() {
        HashMap<String, String> workerQuality = summaryResultsParser.getWorkerQuality();
        double avgQuality = evaluateWorkerQuality("EXPECTEDCOST", "w");

        String expectedQuality = workerQuality.get("[WorkerQuality_Eval_DS_Exp_w] Actual worker quality (weighted, DS_Exp metric)");
        String actualQuality = testHelper.formatPercent(avgQuality);
        fileWriter.write("WorkerQuality_Eval_DS_Exp_w," + expectedQuality + "," + actualQuality);
        assertEquals(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Eval_DS_ML_n() {
        HashMap<String, String> workerQuality = summaryResultsParser.getWorkerQuality();
        double avgQuality = evaluateWorkerQuality("MAXLIKELIHOOD", "n");

        String expectedQuality = workerQuality.get("[WorkerQuality_Eval_DS_ML_n] Actual worker quality (non-weighted, DS_ML metric)");
        String actualQuality = testHelper.formatPercent(avgQuality);
        fileWriter.write("WorkerQuality_Eval_DS_ML_n," + expectedQuality + "," + actualQuality);
        assertEquals(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Eval_DS_ML_w() {
        HashMap<String, String> workerQuality = summaryResultsParser.getWorkerQuality();
        double avgQuality = evaluateWorkerQuality("MAXLIKELIHOOD", "w");

        String expectedQuality = workerQuality.get("[WorkerQuality_Eval_DS_ML_w] Actual worker quality (weighted, DS_ML metric)");
        String actualQuality = testHelper.formatPercent(avgQuality);
        fileWriter.write("WorkerQuality_Eval_DS_ML_w," + expectedQuality + "," + actualQuality);
        assertEquals(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Eval_DS_Min_n() {
        HashMap<String, String> workerQuality = summaryResultsParser.getWorkerQuality();
        double avgQuality = evaluateWorkerQuality("MINCOST", "n");

        String expectedQuality = workerQuality.get("[WorkerQuality_Eval_DS_Min_n] Actual worker quality (non-weighted, DS_Min metric)");
        String actualQuality = testHelper.formatPercent(avgQuality);
        fileWriter.write("WorkerQuality_Eval_DS_Min_n," + expectedQuality + "," + actualQuality);
        assertEquals(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Eval_DS_Min_w() {
        HashMap<String, String> workerQuality = summaryResultsParser.getWorkerQuality();
        double avgQuality = evaluateWorkerQuality("MINCOST", "w");

        String expectedQuality = workerQuality.get("[WorkerQuality_Eval_DS_Min_w] Actual worker quality (weighted, DS_Min metric)");
        String actualQuality = testHelper.formatPercent(avgQuality);
        fileWriter.write("WorkerQuality_Eval_DS_Min_w," + expectedQuality + "," + actualQuality);
        assertEquals(expectedQuality, actualQuality);
    }

    @Test
    public void test_LabelsPerWorker() {
        HashMap<String, String> workerQuality = summaryResultsParser.getWorkerQuality();
        double noAssignedLabels = 0.0;
        Set<LObject<String>> objects = data.getObjects();
        for (LObject<String> object : objects) {
            noAssignedLabels += data.getAssignsForObject(object).size();
        }
        double labelsPerWorker = noAssignedLabels / data.getWorkers().size();
        String expectedNoLabelsPerWorker = workerQuality.get("[Number of labels] Labels per worker");
        String actualNoLabelsPerWorker = testHelper.format(labelsPerWorker);
        fileWriter.write("Labels per worker," + expectedNoLabelsPerWorker + "," + actualNoLabelsPerWorker);
        assertEquals(expectedNoLabelsPerWorker, actualNoLabelsPerWorker);
    }

    @Test
    public void test_GoldTestsPerWorker() {
        HashMap<String, String> workerQuality = summaryResultsParser.getWorkerQuality();
        double avgNoGoldTests = 0.0;
        Set<Worker<String>> workers = data.getWorkers();
        for (Worker<String> worker : workers) {
            for (AssignedLabel<String> assign : project.getData().getWorkerAssigns(worker)) {
                if (assign.getLobject().isGold()) {
                    avgNoGoldTests += 1;
                }
            }
        }
        avgNoGoldTests = avgNoGoldTests / workers.size();
        String expectedNoGoldTestsPerWorker = workerQuality.get("[Gold Tests] Gold tests per worker");
        String actualNoGoldTestsPerWorker = testHelper.format(avgNoGoldTests);
        fileWriter.write("Gold Tests per worker," + expectedNoGoldTestsPerWorker + "," + actualNoGoldTestsPerWorker);
        assertEquals(expectedNoGoldTestsPerWorker, actualNoGoldTestsPerWorker);
    }
}