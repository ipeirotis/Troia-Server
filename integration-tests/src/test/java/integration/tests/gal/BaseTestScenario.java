package test.java.integration.tests.gal;

import com.datascience.core.algorithms.INewDataObserver;
import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.*;
import com.datascience.core.nominal.decision.*;
import com.datascience.datastoring.jobs.IJobStorage;
import com.datascience.datastoring.datamodels.full.MemoryJobStorage;
import com.datascience.core.nominal.Quality;
import com.datascience.gal.evaluation.DataEvaluator;
import com.datascience.gal.evaluation.WorkerEvaluator;
import com.datascience.utils.CostMatrix;
import test.java.integration.helpers.SummaryResultsParser;
import test.java.integration.helpers.TestHelpers;
import test.java.integration.helpers.TestSettings;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BaseTestScenario {

    public final static String DATA_BASE_DIR = TestSettings.GAL_TESTDATA_BASEDIR;
    public final static String RESULTS_BASE_DIR = TestSettings.GAL_RESULTS_BASEDIR;
    public final static double TOLERANCE = 0.05;
    protected static String inputDir;
    protected static String outputDir;
    protected static TestHelpers testHelper = new TestHelpers();
    protected static SummaryResultsParser summaryResultsParser;
    protected static NominalProject project;
    protected static INominalData data;

    public static void setUp(NominalAlgorithm algorithm, String testName, boolean loadEvaluationLabels) {
        inputDir = DATA_BASE_DIR + testName + TestSettings.FILEPATH_SEPARATOR + "input" + TestSettings.FILEPATH_SEPARATOR;
        outputDir = DATA_BASE_DIR + testName + TestSettings.FILEPATH_SEPARATOR + "output" + TestSettings.FILEPATH_SEPARATOR;
        summaryResultsParser = new SummaryResultsParser(outputDir + "summary.txt");

        Collection<String> categories = testHelper.LoadCategories(inputDir + "categories.txt");
        Collection<CategoryValue> priors = testHelper.loadCategoryPriors(inputDir + "categories.txt");
        CostMatrix<String> costs = testHelper.loadCostsMatrix(inputDir + "costs.txt");

        IJobStorage js = new MemoryJobStorage();
        project = new NominalProject(algorithm, js.getNominalData(testName), js.getNominalResults(testName, categories));
        data = project.getData();
        if (algorithm instanceof INewDataObserver) {
            data.addNewUpdatableAlgorithm((INewDataObserver) algorithm);
        }
        project.initializeCategories(categories, priors, costs);
        loadData(loadEvaluationLabels);
        project.getAlgorithm().compute();
    }

    public static void loadData(boolean loadEvaluationLabels) {
        loadAssignedLabels();
        loadGoldLabels();
        if (loadEvaluationLabels) {
            loadEvaluationLabels();
        }
    }

    public static void loadGoldLabels() {
        Collection<LObject<String>> goldLabels = testHelper.LoadGoldLabels(inputDir + "correct.txt");
        for (LObject<String> goldLabel : goldLabels) {
            data.addObject(goldLabel);
        }
    }

    public static void loadEvaluationLabels() {
        Collection<LObject<String>> evaluationLabels = testHelper.LoadEvaluationLabels(inputDir + "evaluation.txt");
        for (LObject<String> evaluationLabel : evaluationLabels) {
            data.addObject(evaluationLabel);
        }
    }

    public static void loadAssignedLabels() {
        Collection<AssignedLabel<String>> assignedLabels = testHelper.LoadWorkerAssignedLabels(inputDir + "input.txt");
        for (AssignedLabel<String> assign : assignedLabels) {
            Worker worker = data.getOrCreateWorker(assign.getWorker().getName());
            data.addWorker(worker);
            assign.setWorker(worker);
            LObject<String> object = data.getOrCreateObject(assign.getLobject().getName());
            data.addObject(object);
            assign.setLobject(object);
        }
        for (AssignedLabel<String> assign : assignedLabels) {
            data.addAssign(assign);
        }
    }

    public static double estimateMissclassificationCost(
            ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator,
            IObjectLabelDecisionAlgorithm objectLabelDecisionAlgorithm) {
        DecisionEngine decisionEngine = new DecisionEngine(labelProbabilityDistributionCostCalculator,
                objectLabelDecisionAlgorithm);
        Collection<LObject<String>> objects = data.getObjects();
        double avgClassificationCost = 0.0;

        //compute the estimated misclassification cost for each object, using DS
        for (LObject<String> object : objects) {
            avgClassificationCost += decisionEngine.estimateMissclassificationCost(project, object);
        }

        //calculate the average
        avgClassificationCost = avgClassificationCost / objects.size();
        return avgClassificationCost;
    }

    public static double evaluateMissclassificationCost(String labelChoosingMethod) {
        DataEvaluator dataEvaluator = new DataEvaluator(labelChoosingMethod);

        //compute the evaluated misclassification cost
        Map<LObject<String>, Double> evaluated = dataEvaluator.evaluate(project);

        double avgCost = 0.0;

        for (Double entryValue : evaluated.values()) {
            avgCost += entryValue;
        }

        //calculate the average cost
        avgCost = avgCost / data.getEvaluationObjects().size();
        return avgCost;
    }

    public static double estimateCostToQuality(ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator,
            IObjectLabelDecisionAlgorithm objectLabelDecisionAlgorithm) {
        DecisionEngine decisionEngine = new DecisionEngine(labelProbabilityDistributionCostCalculator, objectLabelDecisionAlgorithm);
        Map<LObject<String>, Double> costQuality = Quality.fromCosts(project, decisionEngine.estimateMissclassificationCosts(project));

        double avgQuality = 0.0;

        //compute the estimated quality cost for each object, using MV
        for (Double cQuality : costQuality.values()) {
            avgQuality += cQuality;
        }

        //calculate the average
        avgQuality /= costQuality.size();
        return avgQuality;
    }

    public static double evaluateCostToQuality(String labelChoosingMethod) {
        DataEvaluator dataEvaluator = new DataEvaluator(labelChoosingMethod);

        Map<LObject<String>, Double> costQuality = Quality.fromCosts(project, dataEvaluator.evaluate(project));
        double avgQuality = 0.0;

        //compute the estimated quality cost for each object, using MV
        for (Double cQuality : costQuality.values()) {
            avgQuality += cQuality;
        }

        //calculate the average
        avgQuality /= costQuality.size();
        return avgQuality;
    }

    public static double estimateWorkerQuality(String method, String estimationType) {
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator =
                LabelProbabilityDistributionCostCalculators.get(method);
        WorkerEstimator workerEstimator = new WorkerEstimator(labelProbabilityDistributionCostCalculator);
        Map<Worker, Double> result = new HashMap<Worker, Double>();
        Map<Worker, Integer> workerAssignedLabels = new HashMap<Worker, Integer>();

        for (Worker worker : project.getData().getWorkers()) {
            result.put(worker, workerEstimator.getCost(project, worker));
            workerAssignedLabels.put(worker, project.getData().getWorkerAssigns(worker).size());
        }
        Map<Worker, Double> workersQuality = Quality.fromCosts(project, result);
        return getAverageValue(workersQuality, estimationType.equals("n") ? null : workerAssignedLabels);
    }

    public static double evaluateWorkerQuality(String method, String estimationType) {
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator =
                LabelProbabilityDistributionCostCalculators.get(method);
        WorkerEvaluator workerEvaluator = new WorkerEvaluator(labelProbabilityDistributionCostCalculator);
        Map<Worker, Double> result = new HashMap<Worker, Double>();
        Map<Worker, Integer> workerAssignedLabels = new HashMap<Worker, Integer>();

        for (Worker worker : project.getData().getWorkers()) {
            result.put(worker, workerEvaluator.getCost(project, worker));
            workerAssignedLabels.put(worker, project.getData().getWorkerAssigns(worker).size());
        }

        Map<Worker, Double> workersQuality = Quality.fromCosts(project, result);
        return getAverageValue(workersQuality, estimationType.equals("n") ? null : workerAssignedLabels);
    }

    private static <T> double getAverageValue(Map<T, Double> values, Map<T, Integer> weight) {
        double quality = 0.;
        int cnt = 0;
        for (Map.Entry<T, Double> e : values.entrySet()) {
            Double val = e.getValue();
            if (val == null || val.isNaN()) {
                continue;
            }
            if (weight != null) {
                cnt += weight.get(e.getKey());
                quality += e.getValue() * weight.get(e.getKey());
            } else {
                quality += e.getValue();
                cnt++;
            }
        }
        return quality / cnt;
    }

    public void testCondition(Double expectedValue, Double actualValue) {
        if (expectedValue.isNaN() && actualValue.isNaN()) {
            assertEquals(expectedValue, actualValue);
        } else {
            assertTrue(Math.abs(expectedValue - actualValue) / 100 < TOLERANCE);
        }
    }
}
