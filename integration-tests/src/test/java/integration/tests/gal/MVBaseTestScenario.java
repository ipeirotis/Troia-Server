package test.java.integration.tests.gal;

import com.datascience.core.nominal.decision.LabelProbabilityDistributionCostCalculators;
import com.datascience.core.nominal.decision.WorkerEstimator;
import com.datascience.mv.BatchMV;
import com.datascience.mv.IncrementalMV;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(JUnitParamsRunner.class)
public class MVBaseTestScenario extends BaseTestScenario {

    private static Map<String, Double> params;

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
        if (testSetup.algorithm.equals("BMV")) {
            setUp(new BatchMV(), testSetup.testName, testSetup.loadEvaluationLabels);
        } else {
            setUp(new IncrementalMV(), testSetup.testName, testSetup.loadEvaluationLabels);
        }
        params = new HashMap<String, Double>();
        params.put("Estm_MV_Exp", estimateMissclassificationCost(LabelProbabilityDistributionCostCalculators.get("EXPECTEDCOST"), null));
        params.put("Estm_MV_ML", estimateMissclassificationCost(LabelProbabilityDistributionCostCalculators.get("MAXLIKELIHOOD"), null));
        params.put("Estm_MV_Min", estimateMissclassificationCost(LabelProbabilityDistributionCostCalculators.get("MINCOST"), null));
        params.put("Eval_MV_ML", evaluateMissclassificationCost("MAXLIKELIHOOD"));
        params.put("Eval_MV_Min", evaluateMissclassificationCost("MINCOST"));
        params.put("Eval_MV_Soft", evaluateMissclassificationCost("SOFT"));
        params.put("Estm_MV_ML_q", estimateCostToQuality(LabelProbabilityDistributionCostCalculators.get("MAXLIKELIHOOD"), null));
        params.put("Estm_MV_Exp_q", estimateCostToQuality(LabelProbabilityDistributionCostCalculators.get("EXPECTEDCOST"), null));
        params.put("Estm_MV_Min_q", estimateCostToQuality(LabelProbabilityDistributionCostCalculators.get("MINCOST"), null));
        params.put("Eval_MV_ML_q", evaluateCostToQuality("MAXLIKELIHOOD"));
        params.put("Eval_MV_Soft_q", evaluateCostToQuality("SOFT"));
        params.put("Eval_MV_Min_q", evaluateCostToQuality("MINCOST"));
    }

    @Test
    @Parameters
    public void testDataCost(String p1, String p2) {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        assertEquals(dataQuality.get(p2), params.get(p1), 0.05);
    }

    private Object[] parametersForTestDataCost() {
        return $(
                $("Estm_MV_Exp", "[DataCost_Estm_MV_Exp] Estimated classification cost (MV_Exp metric)"),
                $("Estm_MV_ML", "[DataCost_Estm_MV_ML] Estimated classification cost (MV_ML metric)"),
                $("Estm_MV_Min", "[DataCost_Estm_MV_Min] Estimated classification cost (MV_Min metric)"),
                $("Eval_MV_ML", "[DataCost_Eval_MV_ML] Actual classification cost for majority vote classification"),
                $("Eval_MV_Min", "[DataCost_Eval_MV_Min] Actual classification cost for naive min-cost classification"),
                $("Eval_MV_Soft", "[DataCost_Eval_MV_Soft] Actual classification cost for naive soft-label classification"));
    }

    @Test
    @Parameters
    public void testDataQuality(String p1, String p2) {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        assertEquals(dataQuality.get(p2), params.get(p1), 0.05);
    }

    private Object[] parametersForTestDataQuality() {
        return $(
                $("Estm_MV_ML_q", "[DataQuality_Estm_MV_ML] Estimated data quality, naive majority label"),
                $("Estm_MV_Exp_q", "[DataQuality_Estm_MV_Exp] Estimated data quality, naive soft label"),
                $("Estm_MV_Min_q", "[DataQuality_Estm_MV_Min] Estimated data quality, naive mincost label"),
                $("Eval_MV_ML_q", "[DataQuality_Eval_MV_ML] Actual data quality, naive majority label"),
                $("Eval_MV_Min_q", "[DataQuality_Eval_MV_Min] Actual data quality, naive mincost label"),
                $("Eval_MV_Soft_q", "[DataQuality_Eval_MV_Soft] Actual data quality, naive soft label"));
    }

    
}
