package test.java.integration.tests.gal;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.decision.ILabelProbabilityDistributionCostCalculator;
import com.datascience.core.nominal.decision.LabelProbabilityDistributionCostCalculators;
import com.datascience.gal.BatchDawidSkene;
import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.IncrementalDawidSkene;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.datascience.core.nominal.Quality.getExpSpammerCost;
import static com.datascience.core.nominal.Quality.getMinSpammerCost;
import static test.java.integration.tests.gal.BaseTestScenario.testHelper;
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

        int expectedObjectsNo = Integer.parseInt(data.get("Objects in Data Set"));
        int actualObjectsNo = this.data.getObjects().size();
        assertEquals(expectedObjectsNo, actualObjectsNo);

        int expectedWorkersNo = Integer.parseInt(data.get("Workers in Data Set"));
        int actualWorkersNo = this.data.getWorkers().size();
        assertEquals(expectedWorkersNo, actualWorkersNo);

        //get the labels
        int noAssignedLabels = 0;
        Collection<LObject<String>> objects = this.data.getObjects();
        for (LObject<String> object : objects) {
            noAssignedLabels += this.data.getAssignsForObject(object).size();
        }

        int expectedLabelsNo = Integer.parseInt(data.get("Labels Assigned by Workers"));
        assertEquals(expectedLabelsNo, noAssignedLabels);
    }

    @Test
    public void test_ProbabilityDistributions_DS() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
		Collection<LObject<String>> objects = data.getObjects();

        //init the categoryProbabilities hashmap
        HashMap<String, Double> categoryProbabilities = new HashMap<String, Double>();
        for (String categoryName : data.getCategories()) {
            categoryProbabilities.put(categoryName, 0.0);
        }

        //iterate through the datum objects and calculate the sum of the probabilities associated  to each category
        int noObjects = objects.size();
        for (LObject<String> object : objects) {
            Map<String, Double> objectProbabilities = project.getObjectResults(object).getCategoryProbabilites();
            for (Map.Entry<String, Double> e : objectProbabilities.entrySet()) {
                categoryProbabilities.put(e.getKey(), categoryProbabilities.get(e.getKey()) + e.getValue());
            }
        }

        //calculate the average probability value for each category
        for (String categoryName : data.getCategories()) {
            categoryProbabilities.put(categoryName, categoryProbabilities.get(categoryName) / noObjects);
        }
        for (String categoryName : data.getCategories()) {
            String metricName = "[DS_Pr[" + categoryName + "]] DS estimate for prior probability of category " + categoryName;
            Double expectedCategoryProbability = dataQuality.get(metricName);
            Double actualCategoryProbability = categoryProbabilities.get(categoryName);
            testCondition(expectedCategoryProbability, actualCategoryProbability);
        }
    }

    @Test
    public void test_DataCost_Estm_NoVote_Exp() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        Double actualClassificationCost = testHelper.format(getExpSpammerCost(project));
        Double expectedClassificationCost = testHelper.format(dataQuality.get("[DataCost_Estm_NoVote_Exp] Baseline classification cost (random spammer)"));
        testCondition(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Estm_NoVote_Min() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        Double actualClassificationCost = testHelper.format(getMinSpammerCost(project));
        Double expectedClassificationCost = testHelper.format(dataQuality.get("[DataCost_Estm_NoVote_Min] Baseline classification cost (strategic spammer)"));
        testCondition(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Estm_DS_Exp() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("EXPECTEDCOST");
        Double actualClassificationCost = testHelper.format(estimateMissclassificationCost(labelProbabilityDistributionCostCalculator, null));
        Double expectedClassificationCost = testHelper.format(dataQuality.get("[DataCost_Estm_DS_Exp] Estimated classification cost (DS_Exp metric)"));
        testCondition(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Estm_DS_ML() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("MAXLIKELIHOOD");
        Double actualClassificationCost = testHelper.format( estimateMissclassificationCost(labelProbabilityDistributionCostCalculator, null));
        Double expectedClassificationCost = testHelper.format(dataQuality.get("[DataCost_Estm_DS_ML] Estimated classification cost (DS_ML metric)"));
        testCondition(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Estm_DS_Min() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("MINCOST");
        Double actualClassificationCost = testHelper.format(estimateMissclassificationCost(labelProbabilityDistributionCostCalculator, null));
        Double expectedClassificationCost = testHelper.format(dataQuality.get("[DataCost_Estm_DS_Min] Estimated classification cost (DS_Min metric)"));
        testCondition(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Eval_DS_ML() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        Double actualClassificationCost = testHelper.format(evaluateMissclassificationCost("MAXLIKELIHOOD"));
        Double expectedClassificationCost = testHelper.format(dataQuality.get("[DataCost_Eval_DS_ML] Actual classification cost for EM, maximum likelihood classification"));
        testCondition(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Eval_DS_Min() {
        HashMap<String, Double> data = summaryResultsParser.getDataQuality();
        Double actualClassificationCost = testHelper.format(evaluateMissclassificationCost("MINCOST"));
        Double expectedClassificationCost = testHelper.format(data.get("[DataCost_Eval_DS_Min] Actual classification cost for EM, min-cost classification"));
        testCondition(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Eval_DS_Soft() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        Double actualClassificationCost = testHelper.format(evaluateMissclassificationCost("SOFT"));
        Double expectedClassificationCost = testHelper.format(dataQuality.get("[DataCost_Eval_DS_Soft] Actual classification cost for EM, soft-label classification"));
        testCondition(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataQuality_Estm_DS_ML() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("MAXLIKELIHOOD");
        Double actualQuality = testHelper.formatPercent(estimateCostToQuality(labelProbabilityDistributionCostCalculator, null));
        Double expectedQuality = testHelper.formatPercent(dataQuality.get("[DataQuality_Estm_DS_ML] Estimated data quality, EM algorithm, maximum likelihood"));
        testCondition(expectedQuality, actualQuality);
    }

    @Test
    public void test_DataQuality_Estm_DS_Exp() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("EXPECTEDCOST");
        Double actualQuality = testHelper.formatPercent(estimateCostToQuality(labelProbabilityDistributionCostCalculator, null));
        Double expectedQuality = testHelper.formatPercent(dataQuality.get("[DataQuality_Estm_DS_Exp] Estimated data quality, EM algorithm, soft label"));
        testCondition(expectedQuality, actualQuality);
    }

    @Test
    public void test_DataQuality_Estm_DS_Min() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("MINCOST");
        Double actualQuality = testHelper.formatPercent(estimateCostToQuality(labelProbabilityDistributionCostCalculator, null));
        Double expectedQuality = testHelper.formatPercent(dataQuality.get("[DataQuality_Estm_DS_Min] Estimated data quality, EM algorithm, mincost"));
        testCondition(expectedQuality, actualQuality);
    }

    @Test
    public void test_DataQuality_Eval_DS_ML() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        Double actualQuality = testHelper.formatPercent(evaluateCostToQuality("MAXLIKELIHOOD"));
        Double expectedQuality = testHelper.formatPercent(dataQuality.get("[DataQuality_Eval_DS_ML] Actual data quality, EM algorithm, maximum likelihood"));
        testCondition(expectedQuality, actualQuality);
    }

    @Test
    public void test_DataQuality_Eval_DS_Min() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        Double actualQuality = testHelper.formatPercent(evaluateCostToQuality("MINCOST"));
        Double expectedQuality = testHelper.formatPercent(dataQuality.get("[DataQuality_Eval_DS_Min] Actual data quality, EM algorithm, mincost"));
        testCondition(expectedQuality, actualQuality);
    }

    @Test
    public void test_DataQuality_Eval_DS_Soft() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        Double actualQuality = testHelper.formatPercent(evaluateCostToQuality("SOFT"));
        Double expectedQuality = testHelper.formatPercent(dataQuality.get("[DataQuality_Eval_DS_Soft] Actual data quality, EM algorithm, soft label"));
        testCondition(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Estm_DS_Exp_n() {
        HashMap<String, Double> workerQuality = summaryResultsParser.getWorkerQuality();
        Double actualQuality = testHelper.formatPercent(estimateWorkerQuality("EXPECTEDCOST", "n"));
        Double expectedQuality = testHelper.formatPercent(workerQuality.get("[WorkerQuality_Estm_DS_Exp_n] Estimated worker quality (non-weighted, DS_Exp metric)"));
        testCondition(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Estm_DS_Exp_w() {
        HashMap<String, Double> workerQuality = summaryResultsParser.getWorkerQuality();
        Double actualQuality = testHelper.formatPercent(estimateWorkerQuality("EXPECTEDCOST", "w"));
        Double expectedQuality = testHelper.formatPercent(workerQuality.get("[WorkerQuality_Estm_DS_Exp_w] Estimated worker quality (weighted, DS_Exp metric)"));
        testCondition(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Estm_DS_ML_n() {
        HashMap<String, Double> workerQuality = summaryResultsParser.getWorkerQuality();
        Double actualQuality = testHelper.formatPercent(estimateWorkerQuality("MAXLIKELIHOOD", "n"));
        Double expectedQuality = testHelper.formatPercent(workerQuality.get("[WorkerQuality_Estm_DS_ML_n] Estimated worker quality (non-weighted, DS_ML metric)"));
        testCondition(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Estm_DS_ML_w() {
        HashMap<String, Double> workerQuality = summaryResultsParser.getWorkerQuality();
        Double actualQuality = testHelper.formatPercent(estimateWorkerQuality("MAXLIKELIHOOD", "w"));
        Double expectedQuality = testHelper.formatPercent(workerQuality.get("[WorkerQuality_Estm_DS_ML_w] Estimated worker quality (weighted, DS_ML metric)"));
        testCondition(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Estm_DS_Min_n() {
        HashMap<String, Double> workerQuality = summaryResultsParser.getWorkerQuality();
        Double actualQuality = testHelper.formatPercent(estimateWorkerQuality("MINCOST", "n"));
        Double expectedQuality = testHelper.formatPercent(workerQuality.get("[WorkerQuality_Estm_DS_Min_n] Estimated worker quality (non-weighted, DS_Min metric)"));
        testCondition(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Estm_DS_Min_w() {
        HashMap<String, Double> workerQuality = summaryResultsParser.getWorkerQuality();
        Double actualQuality = testHelper.formatPercent(estimateWorkerQuality("MINCOST", "w"));
        Double expectedQuality = testHelper.formatPercent(workerQuality.get("[WorkerQuality_Estm_DS_Min_w] Estimated worker quality (weighted, DS_Min metric)"));
        testCondition(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Eval_DS_Exp_n() {
        HashMap<String, Double> workerQuality = summaryResultsParser.getWorkerQuality();
        Double actualQuality = testHelper.formatPercent(evaluateWorkerQuality("EXPECTEDCOST", "n"));
        Double expectedQuality = testHelper.formatPercent(workerQuality.get("[WorkerQuality_Eval_DS_Exp_n] Actual worker quality (non-weighted, DS_Exp metric)"));
        testCondition(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Eval_DS_Exp_w() {
        HashMap<String, Double> workerQuality = summaryResultsParser.getWorkerQuality();
        Double actualQuality = testHelper.formatPercent(evaluateWorkerQuality("EXPECTEDCOST", "w"));
        Double expectedQuality = testHelper.formatPercent(workerQuality.get("[WorkerQuality_Eval_DS_Exp_w] Actual worker quality (weighted, DS_Exp metric)"));
        testCondition(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Eval_DS_ML_n() {
        HashMap<String, Double> workerQuality = summaryResultsParser.getWorkerQuality();
        Double actualQuality = testHelper.formatPercent(evaluateWorkerQuality("MAXLIKELIHOOD", "n"));
        Double expectedQuality = testHelper.formatPercent(workerQuality.get("[WorkerQuality_Eval_DS_ML_n] Actual worker quality (non-weighted, DS_ML metric)"));
        testCondition(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Eval_DS_ML_w() {
        HashMap<String, Double> workerQuality = summaryResultsParser.getWorkerQuality();
        Double actualQuality = testHelper.formatPercent(evaluateWorkerQuality("MAXLIKELIHOOD", "w"));
        Double expectedQuality = testHelper.formatPercent(workerQuality.get("[WorkerQuality_Eval_DS_ML_w] Actual worker quality (weighted, DS_ML metric)"));
        testCondition(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Eval_DS_Min_n() {
        HashMap<String, Double> workerQuality = summaryResultsParser.getWorkerQuality();
        Double actualQuality = testHelper.formatPercent(evaluateWorkerQuality("MINCOST", "n"));
        Double expectedQuality = testHelper.formatPercent(workerQuality.get("[WorkerQuality_Eval_DS_Min_n] Actual worker quality (non-weighted, DS_Min metric)"));
        testCondition(expectedQuality, actualQuality);
    }

    @Test
    public void test_WorkerQuality_Eval_DS_Min_w() {
        HashMap<String, Double> workerQuality = summaryResultsParser.getWorkerQuality();
        Double actualQuality = testHelper.formatPercent(evaluateWorkerQuality("MINCOST", "w"));
        Double expectedQuality = testHelper.formatPercent(workerQuality.get("[WorkerQuality_Eval_DS_Min_w] Actual worker quality (weighted, DS_Min metric)"));
        testCondition(expectedQuality, actualQuality);
    }

    @Test
    public void test_LabelsPerWorker() {
        HashMap<String, Double> workerQuality = summaryResultsParser.getWorkerQuality();
        double noAssignedLabels = 0.0;
	Collection<LObject<String>> objects = data.getObjects();
        for (LObject<String> object : objects) {
            noAssignedLabels += data.getAssignsForObject(object).size();
        }
        Double actualNoLabelsPerWorker =  testHelper.format(noAssignedLabels / data.getWorkers().size());
        Double expectedNoLabelsPerWorker =  testHelper.format(workerQuality.get("[Number of labels] Labels per worker"));
        assertEquals(expectedNoLabelsPerWorker, actualNoLabelsPerWorker);
    }

    @Test
    public void test_GoldTestsPerWorker() {
        HashMap<String, Double> workerQuality = summaryResultsParser.getWorkerQuality();
        double avgNoGoldTests = 0.0;
	Collection<Worker> workers = data.getWorkers();
        for (Worker worker : workers) {
            for (AssignedLabel<String> assign : project.getData().getWorkerAssigns(worker)) {
                if (assign.getLobject().isGold()) {
                    avgNoGoldTests += 1;
                }
            }
        }
        Double actualNoGoldTestsPerWorker = testHelper.format(avgNoGoldTests / workers.size());
        Double expectedNoGoldTestsPerWorker = testHelper.format(workerQuality.get("[Gold Tests] Gold tests per worker"));
        assertEquals(expectedNoGoldTestsPerWorker, actualNoGoldTestsPerWorker);
    }
}