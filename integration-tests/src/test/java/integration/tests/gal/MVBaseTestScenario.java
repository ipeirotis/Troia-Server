package test.java.integration.tests.gal;

import com.datascience.core.base.LObject;
import com.datascience.core.nominal.decision.ILabelProbabilityDistributionCostCalculator;
import com.datascience.core.nominal.decision.LabelProbabilityDistributionCostCalculators;
import com.datascience.mv.BatchMV;
import com.datascience.mv.IncrementalMV;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MVBaseTestScenario extends BaseTestScenario {

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
    }

    @Test
    public void test_ProbabilityDistributions_MV() {
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
            for (Map.Entry<String,Double> e : objectProbabilities.entrySet()) {
                categoryProbabilities.put(e.getKey(), categoryProbabilities.get(e.getKey()) + e.getValue());
            }
        }

        //calculate the average probability value for each category
        for (String categoryName : data.getCategories()) {
            categoryProbabilities.put(categoryName, categoryProbabilities.get(categoryName) / noObjects);
        }

        for (String categoryName : data.getCategories()) {
            String metricName = "[MV_Pr[" + categoryName + "]] Majority Vote estimate for prior probability of category " + categoryName;
            Double expectedCategoryProbability = dataQuality.get(metricName);
            Double actualCategoryProbability = testHelper.format(categoryProbabilities.get(categoryName));
            testCondition(expectedCategoryProbability, actualCategoryProbability);
        }
    }

    @Test
    public void test_DataCost_Estm_MV_Exp() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("EXPECTEDCOST");
        Double actualClassificationCost = estimateMissclassificationCost(labelProbabilityDistributionCostCalculator, null);
        Double expectedClassificationCost = dataQuality.get("[DataCost_Estm_MV_Exp] Estimated classification cost (MV_Exp metric)");
        testCondition(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Estm_MV_ML() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("MAXLIKELIHOOD");
        Double actualClassificationCost = estimateMissclassificationCost(labelProbabilityDistributionCostCalculator, null);
        Double expectedClassificationCost = dataQuality.get("[DataCost_Estm_MV_ML] Estimated classification cost (MV_ML metric)");
        testCondition(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Estm_MV_Min() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("MINCOST");
        Double actualClassificationCost = estimateMissclassificationCost(labelProbabilityDistributionCostCalculator, null);
        Double expectedClassificationCost = dataQuality.get("[DataCost_Estm_MV_Min] Estimated classification cost (MV_Min metric)");
        testCondition(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Eval_MV_ML() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        Double actualClassificationCost = evaluateMissclassificationCost("MAXLIKELIHOOD");
        Double expectedClassificationCost = dataQuality.get("[DataCost_Eval_MV_ML] Actual classification cost for majority vote classification");
        testCondition(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Eval_MV_Min() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        Double actualClassificationCost = evaluateMissclassificationCost("MINCOST");
        Double expectedClassificationCost = dataQuality.get("[DataCost_Eval_MV_Min] Actual classification cost for naive min-cost classification");
        testCondition(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Eval_MV_Soft() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        Double actualClassificationCost = evaluateMissclassificationCost("SOFT");
        Double expectedClassificationCost = dataQuality.get("[DataCost_Eval_MV_Soft] Actual classification cost for naive soft-label classification");
        testCondition(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataQuality_Estm_MV_ML() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("MAXLIKELIHOOD");
        Double actualClassificationCost = estimateCostToQuality(labelProbabilityDistributionCostCalculator, null);
        Double expectedClassificationCost = dataQuality.get("[DataQuality_Estm_MV_ML] Estimated data quality, naive majority label");
        testCondition(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataQuality_Estm_MV_Exp() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("EXPECTEDCOST");
        Double actualClassificationCost = estimateCostToQuality(labelProbabilityDistributionCostCalculator, null);
        Double expectedClassificationCost = dataQuality.get("[DataQuality_Estm_MV_Exp] Estimated data quality, naive soft label");
        testCondition(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataQuality_Estm_MV_Min() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("MINCOST");
        Double avgQuality = estimateCostToQuality(labelProbabilityDistributionCostCalculator, null);
        Double expectedClassificationCost = dataQuality.get("[DataQuality_Estm_MV_Min] Estimated data quality, naive mincost label");
        testCondition(expectedClassificationCost, avgQuality);
    }

    @Test
    public void test_DataQuality_Eval_MV_ML() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        Double actualQuality = evaluateCostToQuality("MAXLIKELIHOOD");
        Double expectedDataQuality = dataQuality.get("[DataQuality_Eval_MV_ML] Actual data quality, naive majority label");
        testCondition(expectedDataQuality, actualQuality);
    }

    @Test
    public void test_DataQuality_Eval_MV_Min() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        Double avgQuality = evaluateCostToQuality("MINCOST");
        Double actualQuality = dataQuality.get("[DataQuality_Eval_MV_Min] Actual data quality, naive mincost label");
        Double expectedQuality = testHelper.formatPercent(avgQuality);
        testCondition(expectedQuality, actualQuality);
    }

    @Test
    public void test_DataQuality_Eval_MV_Soft() {
        HashMap<String, Double> dataQuality = summaryResultsParser.getDataQuality();
        Double actualQuality = evaluateCostToQuality("SOFT");
        Double expectedQuality = dataQuality.get("[DataQuality_Eval_MV_Soft] Actual data quality, naive soft label");
        testCondition(expectedQuality, actualQuality);
    }
}
