package test.java.integration.tests.gal;

import com.datascience.core.base.LObject;
import com.datascience.core.nominal.decision.ILabelProbabilityDistributionCostCalculator;
import com.datascience.core.nominal.decision.LabelProbabilityDistributionCostCalculators;
import com.datascience.mv.BatchMV;
import com.datascience.mv.IncrementalMV;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

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
            String metricName = "[MV_Pr[" + categoryName + "]] Majority Vote estimate for prior probability of category " + categoryName;
            String expectedCategoryProbability = dataQuality.get(metricName);
            String actualCategoryProbability = testHelper.format(categoryProbabilities.get(categoryName));
            fileWriter.write("[MV_Pr[" + categoryName + "]]," + expectedCategoryProbability + "," + actualCategoryProbability);
            assertEquals(expectedCategoryProbability, actualCategoryProbability);
        }
    }

    @Test
    public void test_DataCost_Estm_MV_Exp() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("EXPECTEDCOST");

        double avgClassificationCost = estimateMissclassificationCost(labelProbabilityDistributionCostCalculator, null);

        String expectedClassificationCost = dataQuality.get("[DataCost_Estm_MV_Exp] Estimated classification cost (MV_Exp metric)");
        String actualClassificationCost = testHelper.format(avgClassificationCost);
        fileWriter.write("DataCost_Estm_MV_Exp," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Estm_MV_ML() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("MAXLIKELIHOOD");

        double avgClassificationCost = estimateMissclassificationCost(labelProbabilityDistributionCostCalculator, null);

        String expectedClassificationCost = dataQuality.get("[DataCost_Estm_MV_ML] Estimated classification cost (MV_ML metric)");
        String actualClassificationCost = testHelper.format(avgClassificationCost);
        fileWriter.write("DataCost_Estm_MV_ML," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Estm_MV_Min() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("MINCOST");

        double avgClassificationCost = estimateMissclassificationCost(labelProbabilityDistributionCostCalculator, null);

        String expectedClassificationCost = dataQuality.get("[DataCost_Estm_MV_Min] Estimated classification cost (MV_Min metric)");
        String actualClassificationCost = testHelper.format(avgClassificationCost);
        fileWriter.write("DataCost_Estm_MV_Min," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Eval_MV_ML() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();

        double avgClassificationCost = evaluateMissclassificationCost("MAXLIKELIHOOD");

        String expectedClassificationCost = dataQuality.get("[DataCost_Eval_MV_ML] Actual classification cost for majority vote classification");
        String actualClassificationCost = testHelper.format(avgClassificationCost);
        fileWriter.write("DataCost_Eval_MV_ML," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Eval_MV_Min() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();

        double avgClassificationCost = evaluateMissclassificationCost("MINCOST");

        String expectedClassificationCost = dataQuality.get("[DataCost_Eval_MV_Min] Actual classification cost for naive min-cost classification");
        String actualClassificationCost = testHelper.format(avgClassificationCost);
        fileWriter.write("DataCost_Eval_MV_Min," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataCost_Eval_MV_Soft() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();

        double avgClassificationCost = evaluateMissclassificationCost("SOFT");

        String expectedClassificationCost = dataQuality.get("[DataCost_Eval_MV_Soft] Actual classification cost for naive soft-label classification");
        String actualClassificationCost = testHelper.format(avgClassificationCost);
        fileWriter.write("DataCost_Eval_MV_Soft," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataQuality_Estm_MV_ML() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("MAXLIKELIHOOD");

        double avgQuality = estimateCostToQuality(labelProbabilityDistributionCostCalculator, null);

        String expectedClassificationCost = dataQuality.get("[DataQuality_Estm_MV_ML] Estimated data quality, naive majority label");
        String actualClassificationCost = testHelper.formatPercent(avgQuality);
        fileWriter.write("DataQuality_Estm_MV_ML," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataQuality_Estm_MV_Exp() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("EXPECTEDCOST");

        double avgQuality = estimateCostToQuality(labelProbabilityDistributionCostCalculator, null);

        String expectedClassificationCost = dataQuality.get("[DataQuality_Estm_MV_Exp] Estimated data quality, naive soft label");
        String actualClassificationCost = testHelper.formatPercent(avgQuality);
        fileWriter.write("DataQuality_Estm_MV_Exp," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataQuality_Estm_MV_Min() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator = LabelProbabilityDistributionCostCalculators.get("MINCOST");

        double avgQuality = estimateCostToQuality(labelProbabilityDistributionCostCalculator, null);

        String expectedClassificationCost = dataQuality.get("[DataQuality_Estm_MV_Min] Estimated data quality, naive mincost label");
        String actualClassificationCost = testHelper.formatPercent(avgQuality);
        fileWriter.write("DataQuality_Estm_MV_Min," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataQuality_Eval_MV_ML() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();

        double avgQuality = evaluateCostToQuality("MAXLIKELIHOOD");

        String expectedClassificationCost = dataQuality.get("[DataQuality_Eval_MV_ML] Actual data quality, naive majority label");
        String actualClassificationCost = testHelper.formatPercent(avgQuality);
        fileWriter.write("DataQuality_Eval_MV_ML," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataQuality_Eval_MV_Min() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();

        double avgQuality = evaluateCostToQuality("MINCOST");

        String expectedClassificationCost = dataQuality.get("[DataQuality_Eval_MV_Min] Actual data quality, naive mincost label");
        String actualClassificationCost = testHelper.formatPercent(avgQuality);
        fileWriter.write("DataQuality_Eval_MV_Min," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }

    @Test
    public void test_DataQuality_Eval_MV_Soft() {
        HashMap<String, String> dataQuality = summaryResultsParser.getDataQuality();
        double avgQuality = evaluateCostToQuality("SOFT");

        String expectedClassificationCost = dataQuality.get("[DataQuality_Eval_MV_Soft] Actual data quality, naive soft label");
        String actualClassificationCost = testHelper.formatPercent(avgQuality);
        fileWriter.write("DataQuality_Eval_MV_Soft," + expectedClassificationCost + "," + actualClassificationCost);
        assertEquals(expectedClassificationCost, actualClassificationCost);
    }
}
