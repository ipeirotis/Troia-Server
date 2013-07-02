package test.java.integration.tests.gal;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.decision.LabelProbabilityDistributionCostCalculators;
import com.datascience.gal.BatchDawidSkene;
import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.IncrementalDawidSkene;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.datascience.core.nominal.Quality.getExpSpammerCost;
import static com.datascience.core.nominal.Quality.getMinSpammerCost;
import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class DSBaseTestScenario extends BaseTestScenario {

    public final static int NO_ITERATIONS = 10;
    public final static double EPSILON = 1e-6;
    public static Map<String, Double> params;

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
        params = new HashMap<String, Double>();
        params.put("Estm_NoVote_Exp", getExpSpammerCost(project));
        params.put("Estm_NoVote_Min", getMinSpammerCost(project));
        params.put("Estm_DS_Exp", estimateMissclassificationCost(LabelProbabilityDistributionCostCalculators.get("EXPECTEDCOST"), null));
        params.put("Estm_DS_ML", estimateMissclassificationCost(LabelProbabilityDistributionCostCalculators.get("MAXLIKELIHOOD"), null));
        params.put("Estm_DS_Min", estimateMissclassificationCost(LabelProbabilityDistributionCostCalculators.get("MINCOST"), null));
        params.put("Eval_DS_ML", evaluateMissclassificationCost("MAXLIKELIHOOD"));
        params.put("Eval_DS_Min", evaluateMissclassificationCost("MINCOST"));
        params.put("Eval_DS_Soft", evaluateMissclassificationCost("SOFT"));
        params.put("Estm_DS_ML_q", estimateCostToQuality(LabelProbabilityDistributionCostCalculators.get("MAXLIKELIHOOD"), null));
        params.put("Estm_DS_Exp_q", estimateCostToQuality(LabelProbabilityDistributionCostCalculators.get("EXPECTEDCOST"), null));
        params.put("Estm_DS_Min_q", estimateCostToQuality(LabelProbabilityDistributionCostCalculators.get("MINCOST"), null));
        params.put("Eval_DS_ML_q", evaluateCostToQuality("MAXLIKELIHOOD"));
        params.put("Eval_DS_Soft_q", evaluateCostToQuality("SOFT"));
        params.put("Eval_DS_Min_q", evaluateCostToQuality("MINCOST"));
        params.put("Estm_DS_Exp_n", estimateWorkerQuality("EXPECTEDCOST", "n"));
        params.put("Estm_DS_Exp_w", estimateWorkerQuality("EXPECTEDCOST", "w"));
        params.put("Estm_DS_ML_n", estimateWorkerQuality("MAXLIKELIHOOD", "n"));
        params.put("Estm_DS_ML_w", estimateWorkerQuality("MAXLIKELIHOOD", "w"));
        params.put("Estm_DS_Min_n", estimateWorkerQuality("MINCOST", "n"));
        params.put("Estm_DS_Min_w", estimateWorkerQuality("MINCOST", "w"));
        params.put("Eval_DS_Exp_n", evaluateWorkerQuality("EXPECTEDCOST", "n"));
        params.put("Eval_DS_Exp_w", evaluateWorkerQuality("EXPECTEDCOST", "w"));
        params.put("Eval_DS_ML_n", evaluateWorkerQuality("MAXLIKELIHOOD", "n"));
        params.put("Eval_DS_ML_w", evaluateWorkerQuality("MAXLIKELIHOOD", "w"));
        params.put("Eval_DS_Min_n", evaluateWorkerQuality("MINCOST", "n"));
        params.put("Eval_DS_Min_w", evaluateWorkerQuality("MINCOST", "w"));
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
    @Parameters
    public void testDataCost(String p1, String p2) {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        assertEquals(testHelper.format(dataQuality.get(p2)), testHelper.format(params.get(p1)), 0.05);
    }

    private Object[] parametersForTestDataCost() {
        return $(
                $("Estm_NoVote_Exp", "[DataCost_Estm_NoVote_Exp] Baseline classification cost (random spammer)"),
                $("Estm_NoVote_Min", "[DataCost_Estm_NoVote_Min] Baseline classification cost (strategic spammer)"),
                $("Estm_DS_Exp", "[DataCost_Estm_DS_Exp] Estimated classification cost (DS_Exp metric)"),
                $("Estm_DS_ML", "[DataCost_Estm_DS_ML] Estimated classification cost (DS_ML metric)"),
                $("Estm_DS_Min", "[DataCost_Estm_DS_Min] Estimated classification cost (DS_Min metric)"),
                $("Eval_DS_ML", "[DataCost_Eval_DS_ML] Actual classification cost for EM, maximum likelihood classification"),
                $("Eval_DS_Min", "[DataCost_Eval_DS_Min] Actual classification cost for EM, min-cost classification"),
                $("Eval_DS_Soft", "[DataCost_Eval_DS_Soft] Actual classification cost for EM, soft-label classification"));
    }

    @Test
    @Parameters
    public void testDataQuality(String p1, String p2) {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        assertEquals(testHelper.formatPercent(dataQuality.get(p2)), testHelper.formatPercent(params.get(p1)), 0.05);
    }

    private Object[] parametersForTestDataQuality() {
        return $(
                $("Estm_DS_ML_q", "[DataQuality_Estm_DS_ML] Estimated data quality, EM algorithm, maximum likelihood"),
                $("Estm_DS_Exp_q", "[DataQuality_Estm_DS_Exp] Estimated data quality, EM algorithm, soft label"),
                $("Estm_DS_Min_q", "[DataQuality_Estm_DS_Min] Estimated data quality, EM algorithm, mincost"),
                $("Eval_DS_ML_q", "[DataQuality_Eval_DS_ML] Actual data quality, EM algorithm, maximum likelihood"),
                $("Eval_DS_Min_q", "[DataQuality_Eval_DS_Min] Actual data quality, EM algorithm, mincost"),
                $("Eval_DS_Soft_q", "[DataQuality_Eval_DS_Soft] Actual data quality, EM algorithm, soft label"));
    }

    @Test
    @Parameters
    public void testWorkerQuality(String p1, String p2) {
        HashMap<String, Double> workerQuality = summaryResultsParser.getWorkerQuality();
        assertEquals(testHelper.formatPercent(workerQuality.get(p2)), testHelper.formatPercent(params.get(p1)), 0.05);
    }

    private Object[] parametersForTestWorkerQuality() {
        return $(
                $("Estm_DS_Exp_n", "[WorkerQuality_Estm_DS_Exp_n] Estimated worker quality (non-weighted, DS_Exp metric)"),
                $("Estm_DS_Exp_w", "[WorkerQuality_Estm_DS_Exp_w] Estimated worker quality (weighted, DS_Exp metric)"),
                $("Estm_DS_ML_n", "[WorkerQuality_Estm_DS_ML_n] Estimated worker quality (non-weighted, DS_ML metric)"),
                $("Estm_DS_ML_w", "[WorkerQuality_Estm_DS_ML_w] Estimated worker quality (weighted, DS_ML metric)"),
                $("Estm_DS_Min_n", "[WorkerQuality_Estm_DS_Min_n] Estimated worker quality (non-weighted, DS_Min metric)"),
                $("Estm_DS_Min_w", "[WorkerQuality_Estm_DS_Min_w] Estimated worker quality (weighted, DS_Min metric)"),
                $("Eval_DS_Exp_n", "[WorkerQuality_Eval_DS_Exp_n] Actual worker quality (non-weighted, DS_Exp metric)"),
                $("Eval_DS_Exp_w", "[WorkerQuality_Eval_DS_Exp_w] Actual worker quality (weighted, DS_Exp metric)"),
                $("Eval_DS_ML_n", "[WorkerQuality_Eval_DS_ML_n] Actual worker quality (non-weighted, DS_ML metric)"),
                $("Eval_DS_ML_w", "[WorkerQuality_Eval_DS_ML_w] Actual worker quality (weighted, DS_ML metric)"),
                $("Eval_DS_Min_n", "[WorkerQuality_Eval_DS_Min_n] Actual worker quality (non-weighted, DS_Min metric)"),
                $("Eval_DS_Min_w", "[WorkerQuality_Eval_DS_Min_w] Actual worker quality (weighted, DS_Min metric)"));
    }

    @Test
    public void test_LabelsPerWorker() {
        HashMap<String, Double> workerQuality = summaryResultsParser.getWorkerQuality();
        double noAssignedLabels = 0.0;
        Collection<LObject<String>> objects = data.getObjects();
        for (LObject<String> object : objects) {
            noAssignedLabels += data.getAssignsForObject(object).size();
        }
        Double actualNoLabelsPerWorker = testHelper.format(noAssignedLabels / data.getWorkers().size());
        Double expectedNoLabelsPerWorker = testHelper.format(workerQuality.get("[Number of labels] Labels per worker"));
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